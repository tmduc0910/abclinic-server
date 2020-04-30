package com.abclinic.server.controller;

import com.abclinic.server.annotation.authorized.Restricted;
import com.abclinic.server.common.constant.UserStatus;
import com.abclinic.server.common.criteria.PatientPredicateBuilder;
import com.abclinic.server.common.base.BaseController;
import com.abclinic.server.common.base.Views;
import com.abclinic.server.common.constant.MessageType;
import com.abclinic.server.common.constant.RecordType;
import com.abclinic.server.common.constant.Status;
import com.abclinic.server.common.utils.StatusUtils;
import com.abclinic.server.exception.BadRequestException;
import com.abclinic.server.factory.NotificationFactory;
import com.abclinic.server.model.entity.notification.Notification;
import com.abclinic.server.model.entity.notification.NotificationMessage;
import com.abclinic.server.model.entity.payload.Inquiry;
import com.abclinic.server.model.entity.user.*;
import com.abclinic.server.service.NotificationService;
import com.abclinic.server.service.entity.PatientService;
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

/**
 * @author tmduc
 * @package com.abclinic.server.controller
 * @created 1/11/2020 2:42 PM
 */
@RestController
public class PatientResourceController extends BaseController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private PatientService patientService;

    @Override
    public void init() {
        this.logger = LoggerFactory.getLogger(PatientResourceController.class);
    }

    @GetMapping("/patients")
    @ApiOperation(
            value = "Danh sách bệnh nhân",
            notes = "Trả về 200 OK hoặc 404 NOT FOUND",
            tags = "Nhân viên phòng khám"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "search", value = "Filter lọc bệnh nhân (name, status, gender, age)", paramType = "query", example = "status=1,name=admin,"),
            @ApiImplicitParam(name = "page", value = "Số thứ tự trang", required = true, paramType = "query", allowableValues = "range[1, infinity]", example = "1"),
            @ApiImplicitParam(name = "size", value = "Kích thước trang", required = true, paramType = "query", example = "4")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Danh sách bệnh nhân theo yêu cầu"),
            @ApiResponse(code = 404, message = "Không tìm thấy bệnh nhân nào đúng yêu cầu")
    })
    @JsonView(Views.Abridged.class)
    @Restricted(excluded = Patient.class)
    public ResponseEntity getPatients(@ApiIgnore @RequestAttribute("User") User user,
                                      @RequestParam(value = "search", defaultValue = "") String search,
                                      @RequestParam("page") int page,
                                      @RequestParam("size") int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("name").ascending());
        return new ResponseEntity(patientService.getPatients(user, search, new PatientPredicateBuilder(), pageable), HttpStatus.OK);
    }

    //TODO: Optimize using State pattern or Command pattern
    @GetMapping("/patients/{id}")
    @Restricted(excluded = Patient.class)
    @ApiOperation(
            value = "Lấy thông tin chi tiết bệnh nhân",
            notes = "Trả về thông tin chi tiết bệnh nhân",
            tags = "Nhân viên phòng khám"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Mã ID của bệnh nhân", required = true, paramType = "path", dataType = "int", example = "1")
    })
    @JsonView(Views.Private.class)
    public ResponseEntity<Patient> getPatient(@PathVariable("id") long id) {
        return new ResponseEntity<>(patientService.getById(id), HttpStatus.OK);
    }

    @PostMapping("/patients/{id}/doctor")
    @Restricted(included = {Coordinator.class, Practitioner.class})
    @ApiOperation(
            value = "Tạo yêu cầu gán bệnh nhân cho bác sĩ quản lý",
            notes = "Trả về 200 OK hoặc 400 BAD REQUEST",
            tags = {"Điều phối viên", "Đa khoa"}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Mã ID của bệnh nhân", required = true, paramType = "path", dataType = "int", example = "1"),
            @ApiImplicitParam(name = "doctor-id", value = "Mã ID của bác sĩ muốn gán", required = true, dataType = "int", example = "2"),
            @ApiImplicitParam(name = "inquiry-id", value = "Mã ID của yêu cầu tư vấn muốn duyệt", required = true, dataType = "int", example = "1")
    })
    @ApiResponses({
            @ApiResponse(code = 201, message = "Yêu cầu thành công"),
            @ApiResponse(code = 400, message = "ID bác sĩ không tồn tại")
    })
    public ResponseEntity addPatientDoctor(@ApiIgnore @RequestAttribute("User") User user,
                                           @PathVariable("id") long id,
                                           @RequestParam("doctor-id") long doctorId,
                                           @RequestParam("inquiry-id") long inquiryId) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId).get();
        UserStatus newStatus = null;
        Patient patient = patientService.getById((int) id);
        NotificationMessage message = NotificationFactory.getMessage(MessageType.ASSIGN, null, inquiry);
        switch (user.getRole()) {
            case COORDINATOR:
                Practitioner practitioner = practitionerRepository.findById(doctorId);
                message.setTargetUser(practitioner);

                //Gán bệnh nhân cho bác sĩ
                patient.setPractitioner(practitioner);
                newStatus = UserStatus.ASSIGN_L1;
                break;
            case PRACTITIONER:
                Doctor subDoc;
                if (inquiry.getType() == RecordType.MEDICAL.getValue()) {
                    subDoc = specialistRepository.findById(doctorId);
                    patient.addSpecialist((Specialist) subDoc);
                    newStatus = UserStatus.ASSIGN_L2;
                }
                else {
                    subDoc = dietitianRepository.findById(doctorId);
                    patient.addDietitian((Dietitian) subDoc);
                    newStatus = UserStatus.ASSIGN_L3;
                }
                message.setTargetUser(subDoc);
                break;
        }
        patient = StatusUtils.update(patient, newStatus);
        save(patient);
        if (message.getTargetUser() != null)
            notificationService.makeNotification(user, message);

        else throw new BadRequestException(user.getId(), "ID bác sĩ không tồn tại");
        return new ResponseEntity(HttpStatus.CREATED);
    }

    //TODO: Optimize
    @PutMapping("/patients/{id}/doctor")
    @Restricted(included = {Practitioner.class, Specialist.class, Dietitian.class})
    @ApiOperation(
            value = "Trả lời yêu cầu gán bệnh nhân cho bác sĩ quản lý",
            notes = "Trả về 200 OK",
            tags = {"Đa khoa", "Chuyên khoa", "Dinh dưỡng"}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Mã ID của bệnh nhân", required = true, paramType = "path", dataType = "int", example = "1"),
            @ApiImplicitParam(name = "notification-id", value = "Mã ID của thông báo gán bệnh nhân", required = true, dataType = "int", example = "1"),
            @ApiImplicitParam(name = "is-accepted", value = "Chấp nhận yêu cầu hay không", required = true, dataType = "boolean", allowableValues = "true, false")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Trả lời thành công"),
    })
    public ResponseEntity editPatientDoctor(@ApiIgnore @RequestAttribute("User") User user,
                                            @RequestParam("notification-id") long notificationId,
                                            @RequestParam("is-accepted") boolean isAccepted,
                                            @PathVariable("id") long id) {
        Patient patient = patientService.getById(id);
        Notification notification = notificationRepository.findById(notificationId);
        if (notification.getType() == MessageType.ASSIGN.getValue()) {
            Inquiry inquiry = inquiryRepository.findById(notification.getPayloadId()).get();
            if (!isAccepted) {
                switch (user.getRole()) {
                    case PRACTITIONER:
                        patient = (Patient) StatusUtils.remove(user, UserStatus.ASSIGN_L1);
                        break;
                    case SPECIALIST:
                        patient = (Patient) StatusUtils.remove(user, UserStatus.ASSIGN_L2);
                        break;
                    case DIETITIAN:
                        patient = (Patient) StatusUtils.remove(user, UserStatus.ASSIGN_L3);
                        break;
                }
                save(patient);
            }
            notificationService.makeNotification(user, NotificationFactory.getMessage(isAccepted ? MessageType.ACCEPT_ASSIGN : MessageType.DENY_ASSIGN, notification.getSender(), inquiry));
        }
        return new ResponseEntity(HttpStatus.OK);
    }
}
