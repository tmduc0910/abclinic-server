package com.abclinic.server.controller;

import com.abclinic.server.annotation.authorized.Restricted;
import com.abclinic.server.common.base.BaseController;
import com.abclinic.server.common.base.Views;
import com.abclinic.server.common.constant.RecordType;
import com.abclinic.server.exception.BadRequestException;
import com.abclinic.server.exception.ForbiddenException;
import com.abclinic.server.exception.NotFoundException;
import com.abclinic.server.factory.NotificationFactory;
import com.abclinic.server.model.entity.payload.Inquiry;
import com.abclinic.server.model.entity.user.*;
import com.abclinic.server.service.NotificationService;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.*;
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

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * @author tmduc
 * @package com.abclinic.server.controller
 * @created 2/4/2020 2:24 PM
 */
@RestController
public class InquiryResourceController extends BaseController {

    @Autowired
    private NotificationService notificationService;

    @Override
    public void init() {
        this.logger = LoggerFactory.getLogger(InquiryResourceController.class);
    }

    @PostMapping("/inquiries")
    @Restricted(included = Patient.class)
    @ApiOperation(
            value = "Tạo mới yêu cầu tư vấn",
            notes = "Trả về 201 CREATED hoặc 400 BAD REQUEST",
            tags = "Bệnh nhân"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "album_id", value = "Mã ID của album ảnh", type = "string"),
            @ApiImplicitParam(name = "type", value = "Kiểu yêu cầu tư vấn (0 khám bệnh, 1 dinh dưỡng)", required = true, type = "int", allowableValues = "0, 1", example = "0"),
            @ApiImplicitParam(name = "content", value = "Mô tả yêu cầu", required = true, type = "string")
    })
    @ApiResponses({
            @ApiResponse(code = 201, message = "Tạo mới thành công"),
            @ApiResponse(code = 400, message = "Loại yêu cầu không hợp lệ")
    })
    public ResponseEntity createInquiry(@ApiIgnore @RequestAttribute("User") User user,
                                        @Nullable @RequestParam("album_id") String albumId,
                                        @RequestParam("type") int type,
                                        @RequestParam("content") String content) {
        Patient patient = patientRepository.findById(user.getId());
        if (type < RecordType.values().length) {
            Inquiry inquiry = new Inquiry(patient, albumId, content, type);
            save(inquiry);

            //Send notification
            notificationService.makeNotification(user, NotificationFactory.getMessages(inquiry));
            return new ResponseEntity(HttpStatus.CREATED);
        } else throw new BadRequestException(user.getId(), "Loại yêu cầu không hợp lệ");
    }

    @GetMapping("/inquiries")
    @ApiOperation(
            value = "Lấy danh sách yêu cầu tư vấn",
            notes = "Trả về danh sách tư vấn hoặc 404 NOT FOUND",
            tags = {"Nhân viên phòng khám", "Bệnh nhân"}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "assigned", value = "Tìm bệnh nhân đã được đa khoa gán cho cấp dưới", paramType = "query", type = "boolean", allowableValues = "true, false"),
            @ApiImplicitParam(name = "page", value = "Số thứ tự trang", required = true, paramType = "query", allowableValues = "range[1, infinity]", example = "1"),
            @ApiImplicitParam(name = "size", value = "Kích thước trang", required = true, paramType = "query", example = "4")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Danh sách yêu cầu tư vấn"),
            @ApiResponse(code = 404, message = "Không tìm thấy yêu cầu hợp lệ")
    })
    @JsonView(Views.Public.class)
    public ResponseEntity<Page<Inquiry>> getInquiryList(@ApiIgnore @RequestAttribute("User") User user,
                                                        @RequestParam(value = "assigned", defaultValue = "false") boolean assigned,
                                                        @RequestParam("page") int page,
                                                        @RequestParam("size") int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt"));
        Optional<Page<Inquiry>> inquiries = Optional.empty();
        switch (user.getRole()) {
            case COORDINATOR:
                inquiries = inquiryRepository.findByPatientPractitioner(null, pageable);
                break;
            case PRACTITIONER:
                Practitioner practitioner = practitionerRepository.findById(user.getId());
                if (!assigned)
                    inquiries = inquiryRepository.findByPatientPractitionerAndPatientSpecialistsIsNullOrPatientDietitiansIsNull(practitioner, pageable);
                else inquiries = inquiryRepository.findByPatientPractitioner(practitioner, pageable);
                break;
            case SPECIALIST:
                Specialist specialist = specialistRepository.findById(user.getId());
                inquiries = inquiryRepository.findByPatientIn(specialist.getPatients(), pageable);
                break;
            case DIETITIAN:
                Dietitian dietitian = dietitianRepository.findById(user.getId());
                inquiries = inquiryRepository.findByPatientIn(dietitian.getPatients(), pageable);
                break;
            case PATIENT:
                Patient patient = patientRepository.findById(user.getId());
                inquiries = inquiryRepository.findByPatient(patient, pageable);
                break;
        }
        if (inquiries.isPresent())
            return new ResponseEntity<>(inquiries.get(), HttpStatus.OK);
        else throw new NotFoundException(user.getId());
    }

    @GetMapping("/inquiries/{id}")
    @ApiOperation(
            value = "Lấy thông tin chi tiết yêu cầu tư vấn",
            notes = "Trả về thông tin chi tiết yêu cầu tư vấn hoặc 403 FORBIDDEN hoặc 404 NOT FOUND",
            tags = {"Nhân viên phòng khám", "Bệnh nhân"}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Mã ID của yêu cầu tư vấn", required = true, paramType = "path", dataType = "int", example = "1")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Thông tin chi tiết yêu cầu tư vấn"),
            @ApiResponse(code = 403, message = "Bạn không có quyền truy cập"),
            @ApiResponse(code = 404, message = "Yêu cầu tư vấn không tồn tại")
    })
    @JsonView(Views.Private.class)
    public ResponseEntity<Inquiry> getInquiryDetail(@ApiIgnore @RequestAttribute("User") User user,
                                                    @PathVariable("id") long id) {
        Optional<Inquiry> op = inquiryRepository.findById(id);
        if (op.isPresent()) {
            Inquiry inquiry = op.get();
            switch (user.getRole()) {
                case COORDINATOR:
                    if (inquiry.getPatient().getPractitioner() != null)
                        throw new ForbiddenException(user.getId(), "Điều phối viên không được truy cập vào yêu cầu này.");
                    break;
                case PRACTITIONER:
                    if (!inquiry.getPatient().getPractitioner().equals(user))
                        throw new ForbiddenException(user.getId(), "Bệnh nhân này không thuộc phạm vi quản lý của bác sĩ");
                    break;
                case DIETITIAN:
                    if (!inquiry.getPatient().getDietitians().contains(user))
                        throw new ForbiddenException(user.getId(), "Bệnh nhân này không thuộc phạm vi quản lý của bác sĩ");
                    break;
                case SPECIALIST:
                    if (!inquiry.getPatient().getSpecialists().contains(user))
                        throw new ForbiddenException(user.getId(), "Bệnh nhân này không thuộc phạm vi quản lý của bác sĩ");
                    break;
                case PATIENT:
                    if (!inquiry.getPatient().equals(user))
                        throw new ForbiddenException(user.getId(), "Bệnh nhân không được truy cập vào yêu cầu từ bệnh nhân khác");
            }
            return new ResponseEntity<>(inquiry, HttpStatus.OK);
        } else throw new NotFoundException(user.getId());
    }
}
