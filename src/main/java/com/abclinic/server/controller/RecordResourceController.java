package com.abclinic.server.controller;

import com.abclinic.server.annotation.authorized.Restricted;
import com.abclinic.server.common.base.CustomController;
import com.abclinic.server.common.base.Views;
import com.abclinic.server.common.constant.MessageType;
import com.abclinic.server.common.constant.RecordType;
import com.abclinic.server.common.constant.Role;
import com.abclinic.server.common.constant.PayloadStatus;
import com.abclinic.server.exception.BadRequestException;
import com.abclinic.server.exception.ForbiddenException;
import com.abclinic.server.exception.NotFoundException;
import com.abclinic.server.factory.NotificationFactory;
import com.abclinic.server.model.dto.request.post.RequestCreateRecordDto;
import com.abclinic.server.model.dto.request.put.RequestUpdateRecordDto;
import com.abclinic.server.model.entity.payload.Inquiry;
import com.abclinic.server.model.entity.payload.record.DietRecord;
import com.abclinic.server.model.entity.payload.record.MedicalRecord;
import com.abclinic.server.model.entity.payload.record.Record;
import com.abclinic.server.model.entity.user.*;
import com.abclinic.server.service.NotificationService;
import com.abclinic.server.service.entity.DoctorService;
import com.abclinic.server.service.entity.InquiryService;
import com.abclinic.server.service.entity.RecordService;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.*;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.websocket.server.PathParam;

import java.util.Arrays;

import static com.abclinic.server.common.constant.RecordType.DIET;
import static com.abclinic.server.common.constant.RecordType.MEDICAL;

/**
 * @author tmduc
 * @package com.abclinic.server.controller
 * @created 1/11/2020 2:42 PM
 */
@RestController
public class RecordResourceController extends CustomController {

    @Autowired
    private InquiryService inquiryService;

    @Autowired
    private DoctorService doctorService;

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
    @ApiResponses({
            @ApiResponse(code = 201, message = "Tạo tư vấn thành công"),
            @ApiResponse(code = 400, message = "Mã ID của yêu cầu tư vấn không tồn tại"),
            @ApiResponse(code = 403, message = "Chỉ có bác sĩ... mới có thể tư vấn...")
    })
    public ResponseEntity<? extends Record> createRecord(@ApiIgnore @RequestAttribute("User") User user,
                                                         @RequestBody RequestCreateRecordDto requestCreateRecordDto) {
        try {
            Inquiry inquiry = inquiryService.getById(requestCreateRecordDto.getInquiryId());
            inquiry.setStatus(PayloadStatus.PROCESSED);
            inquiry = inquiryService.save(inquiry);
            Record record;
            if (inquiry.getType() == MEDICAL.getValue()) {
                if (user.getRole() == Role.SPECIALIST)
                    record = new MedicalRecord(inquiry,
                            doctorService.getById(user.getId()),
                            requestCreateRecordDto.getNote(),
                            requestCreateRecordDto.getPrescription(),
                            requestCreateRecordDto.getDiagnose());
                else
                    throw new ForbiddenException(user.getId(), "Chỉ có bác sĩ chuyên khoa mới có thể tư vấn khám bệnh");
            } else {
                if (user.getRole() == Role.DIETITIAN) {
                    record = new DietRecord(inquiry,
                            doctorService.getById(user.getId()),
                            requestCreateRecordDto.getNote(),
                            requestCreateRecordDto.getPrescription());
                    record.setStatus(PayloadStatus.PROCESSED);
                } else
                    throw new ForbiddenException(user.getId(), "Chỉ có bác sĩ dinh dưỡng mới có thể tư vấn dinh dưỡng");
            }
            record = recordService.save(record);
            notificationService.makeNotification(user, NotificationFactory.getMessage(getType(inquiry.getType()), inquiry.getPatient().getPractitioner(), record));
            if (inquiry.getType() == DIET.getValue())
                notificationService.makeNotification(user, NotificationFactory.getMessage(MessageType.DIET_ADVICE, inquiry.getPatient(), record));
            return new ResponseEntity<>(record, HttpStatus.CREATED);
        } catch (NotFoundException e) {
            throw new BadRequestException(user.getId(), "Mã ID của yêu cầu tư vấn không tồn tại");
        }
    }

    //TODO: Đa khoa backup lại tư vấn
    @PutMapping("/records")
    @Restricted(included = Practitioner.class)
    @ApiOperation(
            value = "Chỉnh sửa tư vấn",
            notes = "Trả về 200 OK hoặc 400 BAD REQUEST hoặc 403 FORBIDDEN",
            tags = {"Đa khoa", "Chuyên khoa", "Dinh dưỡng"}
    )
    @ApiResponses({
            @ApiResponse(code = 201, message = "Chỉnh sửa thành công"),
            @ApiResponse(code = 400, message = "Mã ID của tư vấn không tồn tại"),
            @ApiResponse(code = 403, message = "Bác sĩ không phụ trách bệnh nhân này")
    })
    @JsonView(Views.Abridged.class)
    public ResponseEntity<? extends Record> editRecord(@ApiIgnore @RequestAttribute("User") User user,
                                                       @RequestBody RequestUpdateRecordDto requestUpdateRecordDto) {
        try {
            Record record = recordService.getById(requestUpdateRecordDto.getId());
            if (!record.getDoctor().equals(user)) {
                if (record.getInquiry().of(user)) {
                    Patient patient = record.getInquiry().getPatient();

                    Record clone = SerializationUtils.clone(record);
                    clone.setId(0);
                    clone.setNote(requestUpdateRecordDto.getNote());
                    clone.setPrescription(requestUpdateRecordDto.getPrescription());
                    if (record.getRecordType() == MEDICAL.getValue()) {
                        ((MedicalRecord) clone).setDiagnose(requestUpdateRecordDto.getDiagnose());
                    }
                    clone.setDoctor(user);
                    if (user.getRole() == Role.PRACTITIONER) {
                        clone.setStatus(PayloadStatus.PROCESSED);
                        clone = recordService.save(clone);
                    } else {
                        clone = recordService.save(clone);
                        notificationService.makeNotification(user, NotificationFactory.getMessage(getType(record.getRecordType()), patient.getPractitioner(), clone));
                    }
                    record.setStatus(PayloadStatus.IN_PROCESS);
                    recordService.save(record);
                    notificationService.makeNotification(user, NotificationFactory.getMessage(getType(record.getRecordType()), patient, clone));
                    return new ResponseEntity<>(recordService.save(clone), HttpStatus.CREATED);
                } else throw new ForbiddenException(user.getId(), "Bác sĩ không phụ trách bệnh nhân này");
            } else throw new ForbiddenException(user.getId(), "Bác sĩ đa khoa không được phép duyệt chính tư vấn của mình");
        } catch (NotFoundException e) {
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
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        Page result = null;
        switch (user.getRole()) {
            case PRACTITIONER:
            case PATIENT:
                if (type == MEDICAL.getValue())
                    result = recordService.getMedicalRecordsByUser(user, pageable);
                else if (type == DIET.getValue())
                    result = recordService.getDietitianRecordsByUser(user, pageable);
                else throw new BadRequestException(user.getId(), "Kiểu chỉ có thể là Khám bệnh hoặc Dinh dưỡng");
                break;
            case SPECIALIST:
                result = recordService.getMedicalRecordsByUser(user, pageable);
                break;
            case DIETITIAN:
                result = recordService.getDietitianRecordsByUser(user, pageable);
                break;
        }
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @GetMapping("/records/{id}")
    @Restricted(excluded = Coordinator.class)
    @ApiOperation(
            value = "Lấy chi tiết tư vấn",
            notes = "Trả về chi tiết tư vấn hoặc 404 NOT FOUND",
            tags = {"Đa khoa", "Chuyên khoa", "Dinh dưỡng", "Bệnh nhân"}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Mã ID của tư vấn", required = true, paramType = "path", dataType = "int", example = "1"),
            @ApiImplicitParam(name = "type", value = "Loại tư vấn (0: khám bệnh, 1: dinh dưỡng", required = true, paramType = "query", dataType = "int", example = "0")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Thông tin chi tiết tư vấn"),
            @ApiResponse(code = 404, message = "Mã ID của tư vấn không tồn tại")
    })
    @JsonView(Views.Private.class)
    public ResponseEntity getRecordDetail(@ApiIgnore @RequestAttribute("User") User user,
                                          @RequestParam("type") int type,
                                          @PathVariable long id) {
        Record record = recordService.getByTypeAndId(type,  id);
        if (record.getInquiry().of(user))
            return new ResponseEntity(record, HttpStatus.OK);
        else throw new ForbiddenException(user.getId(), "Bạn không có quyền truy cập vào tư vấn này");
    }

    private MessageType getType(int type) {
        RecordType recordType = RecordType.getType(type);
        switch (recordType) {
            case MEDICAL:
                return MessageType.MED_ADVICE;
            case DIET:
                return MessageType.DIET_ADVICE;
        }
        return null;
    }
}
