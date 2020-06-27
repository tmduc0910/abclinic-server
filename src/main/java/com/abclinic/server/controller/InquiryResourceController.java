package com.abclinic.server.controller;

import com.abclinic.server.annotation.authorized.Restricted;
import com.abclinic.server.common.base.CustomController;
import com.abclinic.server.common.base.Views;
import com.abclinic.server.common.constant.MessageType;
import com.abclinic.server.common.constant.RecordType;
import com.abclinic.server.common.constant.UserStatus;
import com.abclinic.server.common.utils.StatusUtils;
import com.abclinic.server.exception.BadRequestException;
import com.abclinic.server.exception.ForbiddenException;
import com.abclinic.server.factory.NotificationFactory;
import com.abclinic.server.model.dto.request.post.RequestCreateInquiryDto;
import com.abclinic.server.model.entity.payload.Inquiry;
import com.abclinic.server.model.entity.user.*;
import com.abclinic.server.service.NotificationService;
import com.abclinic.server.service.entity.InquiryService;
import com.abclinic.server.service.entity.PatientService;
import com.abclinic.server.service.entity.UserService;
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

import java.util.List;

/**
 * @author tmduc
 * @package com.abclinic.server.controller
 * @created 2/4/2020 2:24 PM
 */
@RestController
public class InquiryResourceController extends CustomController {

    @Autowired
    private PatientService patientService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    @Autowired
    private InquiryService inquiryService;

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
    @ApiResponses({
            @ApiResponse(code = 201, message = "Tạo mới thành công"),
            @ApiResponse(code = 400, message = "Loại yêu cầu không hợp lệ")
    })
    @JsonView(Views.Public.class)
    public ResponseEntity<Inquiry> createInquiry(@ApiIgnore @RequestAttribute("User") User user,
                                                 @RequestBody RequestCreateInquiryDto requestCreateInquiryDto) {
        Patient patient = patientService.getById(user.getId());
        if (!StatusUtils.containsStatus(user, UserStatus.DEACTIVATED)) {
            if (StatusUtils.equalsStatus(user, UserStatus.NEW)) {
                if (!patient.getInquiries().isEmpty())
                    throw new ForbiddenException(user.getId(), "Yêu cầu của bạn đang được xử lý");
            }
            if (requestCreateInquiryDto.getType() < RecordType.values().length) {
                Inquiry inquiry = new Inquiry(
                        patient,
                        requestCreateInquiryDto.getAlbumId(),
                        requestCreateInquiryDto.getContent(),
                        requestCreateInquiryDto.getType(),
                        requestCreateInquiryDto.getDate());
                inquiry = inquiryService.save(inquiry);

                //Send notification
                if (!StatusUtils.containsStatus(patient, UserStatus.NEW))
                    notificationService.makeNotification(user, NotificationFactory.getInquiryMessages(inquiry));
                else {
                    List<User> coordinators = userService.findAllCoordinators();
                    Inquiry finalInquiry = inquiry;
                    coordinators.forEach(c -> notificationService.makeNotification(user,
                            NotificationFactory.getMessage(MessageType.INQUIRE, c, finalInquiry)));
                }
                return new ResponseEntity<>(inquiry, HttpStatus.CREATED);
            } else throw new BadRequestException(user.getId(), "Loại yêu cầu không hợp lệ");
        } else throw new ForbiddenException(user.getId(), "Trạng thái người dùng không hợp lệ");
    }

    @GetMapping("/inquiries")
    @ApiOperation(
            value = "Lấy danh sách yêu cầu tư vấn",
            notes = "Trả về danh sách tư vấn hoặc 404 NOT FOUND",
            tags = {"Nhân viên phòng khám", "Bệnh nhân"}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "assigned", value = "Tìm bệnh nhân đã được đa khoa gán cho cấp dưới", paramType = "query", dataType = "boolean", allowableValues = "true, false"),
            @ApiImplicitParam(name = "type", value = "Kiểu yêu cầu tư vấn", paramType = "query", allowableValues = "0, 1", example = "0"),
            @ApiImplicitParam(name = "page", value = "Số thứ tự trang", required = true, paramType = "query", allowableValues = "range[1, infinity]", example = "1"),
            @ApiImplicitParam(name = "size", value = "Kích thước trang", required = true, paramType = "query", example = "4")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Danh sách yêu cầu tư vấn"),
            @ApiResponse(code = 404, message = "Không tìm thấy yêu cầu hợp lệ")
    })
    @JsonView(Views.Private.class)
    public ResponseEntity<Page<Inquiry>> getInquiryList(@ApiIgnore @RequestAttribute("User") User user,
                                                        @RequestParam(value = "type") Integer type,
                                                        @RequestParam(value = "assigned", defaultValue = "false") boolean assigned,
                                                        @RequestParam("page") int page,
                                                        @RequestParam("size") int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        return new ResponseEntity<>(inquiryService.getList(user, type, assigned, pageable), HttpStatus.OK);
    }

    @GetMapping("/inquiries/monthly")
    @ApiOperation(
            value = "Lấy danh sách yêu cầu theo tháng",
            notes = "Trả về 200 OK hoặc 404 not found",
            tags = "Bệnh nhân"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "month", value = "Tháng", required = true, dataType = "int", example = "1"),
            @ApiImplicitParam(name = "year", value = "Năm", required = true, dataType = "int", example = "2020")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Danh sách yêu cầu"),
            @ApiResponse(code = 404, message = "Không có kết quả nào theo yêu cầu")
    })
    @Restricted(included = Patient.class)
    public ResponseEntity<List<Inquiry>> getInquiryList(@ApiIgnore @RequestAttribute("User") User user,
                                                        @RequestParam("month") int month,
                                                        @RequestParam("year") int year) {
        return new ResponseEntity<>(inquiryService.getList(user, month, year), HttpStatus.OK);
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
        Inquiry inquiry = inquiryService.getById(id);
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
    }
}
