package com.abclinic.server.controller;

import com.abclinic.server.annotation.authorized.Restricted;
import com.abclinic.server.base.BaseController;
import com.abclinic.server.base.Views;
import com.abclinic.server.constant.MessageType;
import com.abclinic.server.constant.RecordType;
import com.abclinic.server.exception.BadRequestException;
import com.abclinic.server.exception.NotFoundException;
import com.abclinic.server.factory.NotificationFactory;
import com.abclinic.server.model.entity.notification.Notification;
import com.abclinic.server.model.entity.notification.NotificationMessage;
import com.abclinic.server.model.entity.payload.Inquiry;
import com.abclinic.server.model.entity.user.*;
import com.abclinic.server.service.NotificationService;
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

import java.util.List;
import java.util.Optional;

/**
 * @author tmduc
 * @package com.abclinic.server.controller
 * @created 1/11/2020 2:42 PM
 */
@RestController
public class PatientResourceController extends BaseController {

    @Autowired
    private NotificationService notificationService;

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
                                      @RequestParam("page") int page,
                                      @RequestParam("size") int size) {
        Optional<List<Patient>> op = Optional.empty();
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        switch (user.getRole()) {
            case COORDINATOR:
                op = patientRepository.findByPractitioner(null, pageable);
                break;
            case PRACTITIONER:
                Practitioner practitioner = practitionerRepository.findById(user.getId());
                op = patientRepository.findByPractitioner(practitioner, pageable);
                break;
            case SPECIALIST:
                Specialist specialist = specialistRepository.findById(user.getId());
                op = patientRepository.findBySpecialists(specialist, pageable);
                break;
            case DIETITIAN:
                Dietitian dietitian = dietitianRepository.findById(user.getId());
                op = patientRepository.findByDietitians(dietitian, pageable);
                break;
        }
        if (op.isPresent())
            return new ResponseEntity(op.get(), HttpStatus.OK);
        else throw new NotFoundException(user.getId());
    }

    @GetMapping("/patients/{id}")
    @Restricted(excluded = Patient.class)
    @JsonView(Views.Private.class)
    public ResponseEntity<Patient> getPatient(@PathVariable("id") long id) {
        return new ResponseEntity<>(patientRepository.findById(id), HttpStatus.OK);
    }

    @PostMapping("/patients/{id}/doctor")
    @Restricted(included = {Coordinator.class, Practitioner.class})
    public ResponseEntity addPatientDoctor(@ApiIgnore @RequestAttribute("User") User user,
                                           @PathVariable("id") long id,
                                           @RequestParam("doctor-id") long doctorId,
                                           @RequestParam("inquiry-id") long inquiryId) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId).get();
        NotificationMessage message = NotificationFactory.getMessage(MessageType.ASSIGN, null, inquiry);
        switch (user.getRole()) {
            case COORDINATOR:
                Practitioner practitioner = practitionerRepository.findById(doctorId);
                message.setTargetUser(practitioner);
            case PRACTITIONER:
                Doctor subDoc;
                if (inquiry.getType() == RecordType.MEDICAL.getValue())
                    subDoc = specialistRepository.findById(doctorId);
                else subDoc = dietitianRepository.findById(doctorId);
                message.setTargetUser(subDoc);
            default:
                if (message.getTargetUser() != null)
                    notificationService.makeNotification(user, message);
                else throw new BadRequestException(user.getId(), "ID bác sĩ không tồn tại");
        }
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @PutMapping("/patients/{id}/doctor")
    @Restricted(included = {Practitioner.class, Specialist.class, Dietitian.class})
    public ResponseEntity editPatientDoctor(@ApiIgnore @RequestAttribute("User") User user,
                                            @RequestParam("notification-id") long notificationId,
                                            @RequestParam("is_accepted") boolean isAccepted,
                                            @PathVariable("id") long id) {
        Patient patient = patientRepository.findById(id);
        Notification notification = notificationRepository.findById(notificationId);
        Doctor doctor;
        if (notification.getType() == MessageType.ASSIGN.getValue()) {
            Inquiry inquiry = inquiryRepository.findById(notification.getPayloadId()).get();
            if (isAccepted) {
                switch (user.getRole()) {
                    case PRACTITIONER:
                        doctor = practitionerRepository.findById(user.getId());
                        patient.setPractitioner((Practitioner) doctor);
                        break;
                    case SPECIALIST:
                        doctor = specialistRepository.findById(user.getId());
                        patient.addSpecialist((Specialist) doctor);
                        break;
                    case DIETITIAN:
                        doctor = dietitianRepository.findById(user.getId());
                        patient.addDietitian((Dietitian) doctor);
                        break;
                }
                save(patient);
            }
            notificationService.makeNotification(user, NotificationFactory.getMessage(isAccepted ? MessageType.ACCEPT_ASSIGN : MessageType.DENY_ASSIGN, notification.getSender(), inquiry));
        }
        return new ResponseEntity(HttpStatus.OK);
    }
}
