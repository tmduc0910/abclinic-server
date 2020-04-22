package com.abclinic.server.controller;

import com.abclinic.server.annotation.authorized.Restricted;
import com.abclinic.server.common.base.BaseController;
import com.abclinic.server.common.base.Views;
import com.abclinic.server.common.constant.MessageType;
import com.abclinic.server.common.constant.RecordType;
import com.abclinic.server.common.constant.Role;
import com.abclinic.server.common.constant.Status;
import com.abclinic.server.exception.BadRequestException;
import com.abclinic.server.exception.ForbiddenException;
import com.abclinic.server.exception.NotFoundException;
import com.abclinic.server.factory.NotificationFactory;
import com.abclinic.server.model.entity.payload.Inquiry;
import com.abclinic.server.model.entity.payload.record.DietRecord;
import com.abclinic.server.model.entity.payload.record.MedicalRecord;
import com.abclinic.server.model.entity.payload.record.Record;
import com.abclinic.server.model.entity.user.*;
import com.abclinic.server.service.NotificationService;
import com.abclinic.server.service.entity.RecordService;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Nullable;
import javax.naming.directory.NoSuchAttributeException;
import java.util.Optional;

/**
 * @author tmduc
 * @package com.abclinic.server.controller
 * @created 1/11/2020 2:42 PM
 */
@RestController
public class RecordResourceController extends BaseController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private RecordService recordService;

    @Override
    public void init() {
        this.logger = LoggerFactory.getLogger(RecordResourceController.class);
    }

    @PostMapping("/records")
    @Restricted(included = {Specialist.class, Dietitian.class})
    @ApiOperation(
            value = "Tạo một tư vấn mới",
            notes = "Trả về 201 CREATED hoặc 400 BAD REQUEST hoặc 403 FORBIDDEN",
            tags = {"Chuyên khoa", "Dinh dưỡng"}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "inquiry-id", value = "Mã ID của yêu cầu tư vấn", required = true, dataType = "int", example = "1"),
            @ApiImplicitParam(name = "diagnose", value = "Chẩn đoán", dataType = "string", example = "Chẩn đoán"),
            @ApiImplicitParam(name = "note", value = "Ghi chú", required = true, dataType = "string", example = "Ghi chú"),
            @ApiImplicitParam(name = "prescription", value = "Kê đơn", required = true, dataType = "string", example = "Kê đơn")
    })
    @ApiResponses({
            @ApiResponse(code = 201, message = "Tạo tư vấn thành công"),
            @ApiResponse(code = 400, message = "Mã ID của yêu cầu tư vấn không tồn tại"),
            @ApiResponse(code = 403, message = "Chỉ có bác sĩ... mới có thể tư vấn...")
    })
    public ResponseEntity createRecord(@ApiIgnore @RequestAttribute("User") User user,
                                       @RequestParam("inquiry-id") long inquiryId,
                                       @RequestParam("diagnose") @Nullable String diagnose,
                                       @RequestParam("note") String note,
                                       @RequestParam("prescription") String prescription) {
        Optional<Inquiry> op = inquiryRepository.findById(inquiryId);
        if (op.isPresent()) {
            Inquiry inquiry = op.get();
            Record record;
            if (inquiry.getType() == RecordType.MEDICAL.getValue()) {
                if (user.getRole() == Role.SPECIALIST)
                    record = new MedicalRecord(inquiry, diagnose, prescription, specialistRepository.findById(user.getId()), note);
                else
                    throw new ForbiddenException(user.getId(), "Chỉ có bác sĩ chuyên khoa mới có thể tư vấn khám bệnh");
            } else {
                if (user.getRole() == Role.DIETITIAN)
                    record = new DietRecord(inquiry, prescription, note, dietitianRepository.findById(user.getId()));
                else
                    throw new ForbiddenException(user.getId(), "Chỉ có bác sĩ dinh dưỡng mới có thể tư vấn dinh dưỡng");
            }
            inquiry.setStatus(Status.Payload.PROCESSED);
            save(inquiry);
            save(record);
            notificationService.makeNotification(user, NotificationFactory.getMessage(MessageType.ADVICE, inquiry.getPatient().getPractitioner(), record));
            return new ResponseEntity(HttpStatus.CREATED);
        } else throw new BadRequestException(user.getId(), "Mã ID của yêu cầu tư vấn không tồn tại");
    }

    @PutMapping("/records")
    @Restricted(included = Practitioner.class)
    @ApiOperation(
            value = "Chỉnh sửa tư vấn",
            notes = "Trả về 200 OK hoặc 400 BAD REQUEST hoặc 403 FORBIDDEN"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "record-id", value = "Mã ID của tư vấn", required = true, dataType = "long", example = "1"),
            @ApiImplicitParam(name = "diagnose", value = "Chẩn đoán", dataType = "string", example = "Chẩn đoán"),
            @ApiImplicitParam(name = "note", value = "Ghi chú", required = true, dataType = "string", example = "Ghi chú"),
            @ApiImplicitParam(name = "prescription", value = "Kê đơn", required = true, dataType = "string", example = "Kê đơn")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Chỉnh sửa thành công"),
            @ApiResponse(code = 400, message = "Mã ID của tư vấn không tồn tại"),
            @ApiResponse(code = 403, message = "Bác sĩ không phụ trách bệnh nhân này")
    })
    public ResponseEntity editRecord(@ApiIgnore @RequestAttribute("User") User user,
                                     @RequestParam("record-id") long recordId,
                                     @RequestParam("diagnose") @Nullable String diagnose,
                                     @RequestParam("note") String note,
                                     @RequestParam("prescription") String prescription) {
        try {
            Record record = recordService.getRecord(recordId);
            if (record.getInquiry().of(user)) {
                record.setNote(note);
                if (record.getRecordType() == RecordType.MEDICAL.getValue())
                    ((MedicalRecord) record).setDiagnose(diagnose);
                record.setPrescription(prescription);
                record.setStatus(Status.Payload.PROCESSED);
                save(record);
            } else throw new ForbiddenException(user.getId(), "Bác sĩ không phụ trách bệnh nhân này");
            return new ResponseEntity(HttpStatus.OK);
        } catch (NoSuchAttributeException e) {
            throw new BadRequestException(user.getId(), "Mã ID của tư vấn không tồn tại");
        }
    }

    @GetMapping("/records")
    @Restricted(excluded = Coordinator.class)
    @ApiOperation(
            value = "Lấy danh sách tư vấn",
            notes = "Trả về danh sách tư vấn hoặc 404 NOT FOUND",
            tags = {"Đa khoa", "Chuyên khoa", "Dinh dưỡng", "Bệnh nhân"}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "Loại tư vấn(0: khám bệnh, 1: dinh dưỡng)", allowableValues = "0, 1", dataType = "int", example = "0"),
            @ApiImplicitParam(name = "page", value = "Số thứ tự trang", required = true, paramType = "query", allowableValues = "range[1, infinity]", example = "1"),
            @ApiImplicitParam(name = "size", value = "Kích thước trang", required = true, paramType = "query", example = "4")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Danh sách tư vấn theo yêu cầu"),
            @ApiResponse(code = 404, message = "Không tìm thấy tư vấn nào")
    })
    @JsonView(Views.Abridged.class)
    public ResponseEntity getRecordList(@ApiIgnore @RequestAttribute("User") User user,
                                        @RequestParam(value = "type", defaultValue = "0") int type,
                                        @RequestParam("page") int page,
                                        @RequestParam("size") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Optional op = Optional.empty();
        switch (user.getRole()) {
            case PRACTITIONER:
            case PATIENT:
                if (type == RecordType.MEDICAL.getValue())
                    op = recordService.getMedicalRecordsByUser(user, pageable);
                else if (type == RecordType.DIET.getValue())
                    op = recordService.getDietitianRecordsByUser(user, pageable);
                else throw new BadRequestException(user.getId(), "Kiểu chỉ có thể là 0 hoặc 1");
                break;
            case SPECIALIST:
                op = recordService.getMedicalRecordsByUser(user, pageable);
                break;
            case DIETITIAN:
                op = recordService.getDietitianRecordsByUser(user, pageable);
                break;
        }
        if (op.isPresent())
            return new ResponseEntity(op.get(), HttpStatus.OK);
        else throw new NotFoundException(user.getId());
    }

    @GetMapping("/records/{id}")
    @Restricted(excluded = Coordinator.class)
    @ApiOperation(
            value = "Lấy chi tiết tư vấn",
            notes = "Trả về chi tiết tư vấn hoặc 404 NOT FOUND",
            tags = {"Đa khoa", "Chuyên khoa", "Dinh dưỡng", "Bệnh nhân"}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Mã ID của tư vấn", required = true, paramType = "path", dataType = "int", example = "1")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Thông tin chi tiết tư vấn"),
            @ApiResponse(code = 404, message = "Mã ID của tư vấn không tồn tại")
    })
    @JsonView(Views.Private.class)
    public ResponseEntity getRecordDetail(@ApiIgnore @RequestAttribute("User") User user,
                                          @PathVariable long id) {
        try {
            Record record = recordService.getRecord(id);
            if (record.getInquiry().of(user))
                return new ResponseEntity(record, HttpStatus.OK);
            else throw new ForbiddenException(user.getId(), "Bạn không có quyền truy cập vào tư vấn này");
        } catch (NoSuchAttributeException e) {
            throw new NotFoundException(user.getId());
        }
    }
}
