package com.abclinic.server.controller;

import com.abclinic.server.annotation.authorized.Restricted;
import com.abclinic.server.common.constant.Role;
import com.abclinic.server.common.constant.UserStatus;
import com.abclinic.server.common.criteria.PatientPredicateBuilder;
import com.abclinic.server.common.base.CustomController;
import com.abclinic.server.common.base.Views;
import com.abclinic.server.common.constant.MessageType;
import com.abclinic.server.common.constant.RecordType;
import com.abclinic.server.common.utils.StatusUtils;
import com.abclinic.server.exception.BadRequestException;
import com.abclinic.server.exception.ForbiddenException;
import com.abclinic.server.exception.NotFoundException;
import com.abclinic.server.factory.NotificationFactory;
import com.abclinic.server.model.dto.DoctorListDto;
import com.abclinic.server.model.entity.notification.Notification;
import com.abclinic.server.model.entity.notification.NotificationMessage;
import com.abclinic.server.model.entity.payload.Inquiry;
import com.abclinic.server.model.entity.user.*;
import com.abclinic.server.service.NotificationService;
import com.abclinic.server.service.entity.DoctorService;
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

import java.util.ArrayList;
import java.util.List;

/**
 * @author tmduc
 * @package com.abclinic.server.controller
 * @created 1/11/2020 2:42 PM
 */
@RestController
public class PatientResourceController extends CustomController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private InquiryService inquiryService;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private UserService userService;

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
    public ResponseEntity<Page<Patient>> getPatients(@ApiIgnore @RequestAttribute("User") User user,
                                      @RequestParam(value = "search", defaultValue = "") String search,
                                      @RequestParam("page") int page,
                                      @RequestParam("size") int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("name").ascending());
        Page<Patient> list = patientService.getList(user, search, new PatientPredicateBuilder(), pageable);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

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
    @ApiResponses({
            @ApiResponse(code = 200, message = "Thông tin chi tiết bệnh nhân"),
            @ApiResponse(code = 403, message = "Bệnh nhân không thuộc quyền quản lý")
    })
    @JsonView(Views.Private.class)
    public ResponseEntity<Patient> getPatient(@ApiIgnore @RequestAttribute("User") User user,
                                              @PathVariable("id") long id) {
        Patient patient = patientService.getById(id);
        if (patientService.isPatientOf(patient, user) || user.getRole() == Role.COORDINATOR)
            return new ResponseEntity<>(patient, HttpStatus.OK);
        throw new ForbiddenException(user.getId(), "Bệnh nhân không thuộc quyền quản lý");
    }

    @GetMapping("/patients/{id}/doctor")
    @Restricted(excluded = {Coordinator.class, Patient.class})
    @ApiOperation(
            value = "Lấy danh sách bác sĩ đang phụ trách bệnh nhân",
            notes = "Trả về 200 OK hoặc 403 FORBIDDEN",
            tags = {"Chuyên khoa", "Đa khoa", "Dinh dưỡng"}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Mã ID của bệnh nhân", required = true, paramType = "path", dataType = "int", example = "1"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Danh sách bệnh nhân"),
            @ApiResponse(code = 403, message = "Bệnh nhân không thuộc quyền quản lý")
    })
    public ResponseEntity<DoctorListDto> getPatientDoctorList(@ApiIgnore @RequestAttribute("User") User user,
                                                              @PathVariable("id") long id) {
        Patient patient = patientService.getById(id);
        if (patientService.isPatientOf(patient, user)) {
            return new ResponseEntity<>(new DoctorListDto(patient), HttpStatus.OK);
        } else throw new ForbiddenException(user.getId(), "Bệnh nhân không thuộc quyền quản lý");
    }

    //TODO: Optimize using State pattern or Command pattern
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
        try {
            Inquiry inquiry = inquiryService.getById(inquiryId);
            UserStatus newStatus = null;
            Patient patient = patientService.getById(id);
            if (!StatusUtils.containsStatus(patient, UserStatus.NEW) && patientService.isPatientOf(patient, doctorService.getById(doctorId)))
                throw new BadRequestException(user.getId(), "Bác sĩ này đã được gán cho bệnh nhân này");

            NotificationMessage message = NotificationFactory.getMessage(MessageType.ASSIGN, null, inquiry);
            switch (user.getRole()) {
                case COORDINATOR:
                    Practitioner practitioner = (Practitioner) doctorService.getById(doctorId);
                    message.setTargetUser(practitioner);

                    //Gán bệnh nhân cho bác sĩ
                    patient.setPractitioner(practitioner);
                    newStatus = UserStatus.ASSIGN_L1;
                    break;
                case PRACTITIONER:
                    User subDoc;
                    if (inquiry.getType() == RecordType.MEDICAL.getValue()) {
                        newStatus = UserStatus.ASSIGN_L2;
                    } else {
                        newStatus = UserStatus.ASSIGN_L3;
                    }
                    subDoc = doctorService.getById(doctorId);
                    patient.addSubDoc(subDoc);
                    message.setTargetUser(subDoc);
                    break;
            }
            patient = StatusUtils.remove(patient, UserStatus.NEW);
            patient = StatusUtils.update(patient, newStatus);
            patientService.save(patient);

            if (message.getTargetUser() != null)
                notificationService.makeNotification(user, message);
            return new ResponseEntity(HttpStatus.CREATED);
        } catch (NotFoundException e) {
            throw new BadRequestException(user.getId(), "ID không tồn tại");
        } catch (ClassCastException e) {
            throw new BadRequestException(user.getId(), "Không thể gán yêu cầu khám bệnh cho bác sĩ dinh dưỡng và ngược lại");
        }
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
        try {
            Patient patient = patientService.getById(id);
            try {
                Notification notification = notificationService.getById(notificationId);
                if (notification.getType() == MessageType.ASSIGN.getValue()) {
                    Inquiry inquiry = inquiryService.getById(notification.getPayloadId());
                    if (!isAccepted) {
                        switch (user.getRole()) {
                            case PRACTITIONER:
                                patient = (Patient) StatusUtils.remove(user, UserStatus.ASSIGN_L1);
                                patient.setPractitioner(null);
                                break;
                            case SPECIALIST:
                                patient.removeSubDoc(user);
                                if (patient.getSpecialists().isEmpty())
                                    patient = (Patient) StatusUtils.remove(user, UserStatus.ASSIGN_L2);
                                break;
                            case DIETITIAN:
                                patient.removeSubDoc(user);
                                if (patient.getDietitians().isEmpty())
                                    patient = (Patient) StatusUtils.remove(user, UserStatus.ASSIGN_L3);
                                break;
                        }
                        patientService.save(patient);
                    }
                    notificationService.makeNotification(user, NotificationFactory.getMessage(isAccepted ? MessageType.ACCEPT_ASSIGN : MessageType.DENY_ASSIGN, notification.getSender(), inquiry));
                }
                return new ResponseEntity(HttpStatus.OK);
            } catch (NotFoundException e) {
                throw new BadRequestException(user.getId(), "Mã ID thông báo không tồn tại");
            }
        } catch (NotFoundException e) {
            throw new BadRequestException(user.getId(), "Mã ID bệnh nhân không tồn tại");
        }
    }

    @DeleteMapping("/patients/{id}/doctor")
    @Restricted(excluded = {Coordinator.class, Patient.class})
    @ApiOperation(
            value = "Xóa bệnh nhân khỏi quyền quản lý của mình",
            notes = "Trả về 200 OK hoặc 403 FORBIDDEN",
            tags = {"Đa khoa", "Chuyên khoa", "Dinh dưỡng"}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Mã ID của bệnh nhân", required = true, paramType = "path", dataType = "int", example = "1"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Xóa thành công"),
            @ApiResponse(code = 403, message = "Bệnh nhân không thuộc quyền quản lý")
    })
    public ResponseEntity deletePatientDoctor(@ApiIgnore @RequestAttribute("User") User user,
                                              @PathVariable long id) {
        Patient patient = patientService.getById(id);
        List<User> targets = new ArrayList<>();
        if (patientService.isPatientOf(patient, user)) {
            switch (user.getRole()) {
                case PRACTITIONER:
                    patient.setPractitioner(null);
                    patient = (Patient) StatusUtils.remove(user, UserStatus.ASSIGN_L1);
                    targets = userService.findAllCoordinators();
                    break;
                case DIETITIAN:
                    patient.removeSubDoc(user);
                    patient = (Patient) StatusUtils.remove(user, UserStatus.ASSIGN_L3);
                    targets.add(patient.getPractitioner());
                    break;
                case SPECIALIST:
                    patient.removeSubDoc(user);
                    patient = (Patient) StatusUtils.remove(user, UserStatus.ASSIGN_L2);
                    targets.add(patient.getPractitioner());
                    break;
            }
            Patient finalPatient = patient;
            targets.forEach(t -> notificationService.makeNotification(user,
                    NotificationFactory.getMessage(
                            MessageType.REMOVE_ASSIGN,
                            t,
                            finalPatient)));
            patientService.save(patient);
            return new ResponseEntity(HttpStatus.OK);
        }
        throw new BadRequestException(user.getId(), "Bệnh nhân không thuộc quyền quản lý");
    }
}
