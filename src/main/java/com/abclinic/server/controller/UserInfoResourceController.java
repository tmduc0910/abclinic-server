package com.abclinic.server.controller;

import com.abclinic.server.annotation.authorized.Restricted;
import com.abclinic.server.common.base.CustomController;
import com.abclinic.server.common.base.Views;
import com.abclinic.server.common.constant.Role;
import com.abclinic.server.common.constant.UserStatus;
import com.abclinic.server.common.utils.DateTimeUtils;
import com.abclinic.server.exception.BadRequestException;
import com.abclinic.server.exception.ForbiddenException;
import com.abclinic.server.exception.NotFoundException;
import com.abclinic.server.factory.NotificationFactory;
import com.abclinic.server.model.dto.IndexResultResponseDto;
import com.abclinic.server.model.dto.request.delete.RequestDeleteDto;
import com.abclinic.server.model.dto.request.post.RequestCreateHealthIndexResultDto;
import com.abclinic.server.model.dto.request.post.RequestCreateOtherHealthIndexResultDto;
import com.abclinic.server.model.dto.request.post.RequestReactivateUser;
import com.abclinic.server.model.dto.request.put.RequestUpdateDoctorSpecialtyDto;
import com.abclinic.server.model.dto.request.put.RequestUpdateOtherUserInfo;
import com.abclinic.server.model.dto.request.put.RequestUpdatePatientDiseaseDto;
import com.abclinic.server.model.dto.request.put.RequestUpdateUserInfoDto;
import com.abclinic.server.model.entity.payload.health_index.HealthIndex;
import com.abclinic.server.model.entity.user.*;
import com.abclinic.server.service.NotificationService;
import com.abclinic.server.service.entity.*;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author tmduc
 * @package com.abclinic.server.controller
 * @created 2/6/2020 3:10 PM
 */
@RestController
@Api(tags = "Thông tin cá nhân")
public class UserInfoResourceController extends CustomController {

    @Autowired
    private PatientService patientService;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private UserService userService;

    @Autowired
    private SpecialtyService specialtyService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private DiseaseService diseaseService;

    @Autowired
    private HealthIndexService healthIndexService;

    @Autowired
    private HealthIndexResourceController healthIndexResourceController;

    @Override
    public void init() {
        this.logger = LoggerFactory.getLogger(UserInfoResourceController.class);
    }

    @GetMapping("/user")
    @Restricted
    @ApiOperation(
            value = "Xem thông tin cá nhân",
            notes = "Trả về chi tiết thông tin cá nhân"
    )
    @JsonView(Views.Private.class)
    public ResponseEntity getUserInfo(@ApiIgnore @RequestAttribute("User") User user) {
        return new ResponseEntity(userService.getById(user.getId()), HttpStatus.OK);
    }

    @PutMapping("/user")
    @ApiOperation(
            value = "Sửa thông tin cá nhân",
            notes = "Trả về 200 OK"
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "Chỉnh sửa thành công")
    })
    public ResponseEntity<? extends User> editUserInfo(@ApiIgnore @RequestAttribute("User") User user,
                                                       @RequestBody RequestUpdateUserInfoDto requestUpdateUserInfoDto) {
        user.setEmail(requestUpdateUserInfoDto.getEmail());
        user.setPhoneNumber(requestUpdateUserInfoDto.getPhone());
        user = userService.save(user);

        switch (user.getRole()) {
            case PATIENT:
                Patient patient = patientService.getById(user.getId());
                patient.setAddress(requestUpdateUserInfoDto.getAddress());
                patientService.save(patient);
                break;
            case SPECIALIST:
                Specialist specialist = (Specialist) doctorService.getById(user.getId());
                specialist.setDescription(requestUpdateUserInfoDto.getDescription());
                doctorService.save(specialist);
                break;
            case PRACTITIONER:
                Practitioner practitioner = (Practitioner) doctorService.getById(user.getId());
                practitioner.setDescription(requestUpdateUserInfoDto.getDescription());
                doctorService.save(practitioner);
                break;
            case DIETITIAN:
                Dietitian dietitian = (Dietitian) doctorService.getById(user.getId());
                dietitian.setDescription(requestUpdateUserInfoDto.getDescription());
                doctorService.save(dietitian);
                break;
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping("/user/{id}")
    @Restricted(included = Coordinator.class)
    @ApiOperation(
            value = "Sửa thông tin người dùng",
            notes = "Trả về 200 OK hoặc 400 BAD REQUEST",
            tags = "Điều phối viên"
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "Chỉnh sửa thành công"),
            @ApiResponse(code = 400, message = "Format ngày tháng không hợp lệ")
    })
    public ResponseEntity<? extends User> editUserInfo(@ApiIgnore @RequestAttribute("User") User user,
                                                       @RequestBody RequestUpdateOtherUserInfo requestUpdateOtherUserInfo,
                                                       @PathVariable("id") long id) {
        User u = userService.getById(id);
        try {
            u.setName(requestUpdateOtherUserInfo.getName());
            u.setDateOfBirth(DateTimeUtils.parseDate(requestUpdateOtherUserInfo.getDob()));
            return editUserInfo(u, new RequestUpdateUserInfoDto(requestUpdateOtherUserInfo.getEmail(),
                    requestUpdateOtherUserInfo.getPhone(),
                    requestUpdateOtherUserInfo.getAddress(),
                    requestUpdateOtherUserInfo.getDescription()));
        } catch (DateTimeParseException e) {
            throw new BadRequestException(user.getId(), "Format ngày tháng không hợp lệ");
        }
    }

    @DeleteMapping("/user")
    @Restricted(included = Coordinator.class)
    @ApiOperation(
            value = "Hủy kích hoạt tài khoản",
            notes = "Trả về 200 OK",
            tags = "Điều phối viên"
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "Hủy thành công")
    })
    public ResponseEntity deleteUser(@ApiIgnore @RequestAttribute("User") User user,
                                     @RequestBody RequestDeleteDto requestDeleteDto) {
        User u = userService.getById(requestDeleteDto.getId());
        if (u.getRole() == Role.PATIENT) {
            Patient p = patientService.getById(u.getId());
            notificationService.makeNotification(u, NotificationFactory.getDeactivateMessages(p));
        }
        userService.deactivateUser(u);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/user")
    @Restricted(included = Coordinator.class)
    @ApiOperation(
            value = "Tái kích hoạt tài khoản",
            notes = "Trả về 200 OK",
            tags = "Điều phối viên"
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "Tái kích hoạt thành công")
    })
    public ResponseEntity reactivateUser(@RequestBody RequestReactivateUser requestReactivateUser) {
        User u = userService.getById(requestReactivateUser.getId());
        u.setStatus(UserStatus.NEW.getValue());
        userService.save(u);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PutMapping("/user/specialties")
    @ApiOperation(
            value = "Sửa chuyên môn của bác sĩ",
            notes = "Trả về 200 OK hoặc 400 BAD REQUEST",
            tags = {"Đa khoa", "Chuyên khoa", "Dinh dưỡng"}
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "Sửa thành công"),
            @ApiResponse(code = 400, message = "Bác sĩ dinh dưỡng/chuyên khoa chỉ có thể chọn tối đa 1 chuyên môn")
    })
    @Restricted(excluded = {Patient.class, Coordinator.class})
    public ResponseEntity<? extends User> editSpecialties(@ApiIgnore @RequestAttribute("User") User user,
                                                          @RequestBody RequestUpdateDoctorSpecialtyDto requestUpdateDoctorSpecialtyDto) {
        long[] specialtyIds = requestUpdateDoctorSpecialtyDto.getSpecialtyIds();
        switch (user.getRole()) {
            case PRACTITIONER:
                Practitioner practitioner = (Practitioner) doctorService.getById(user.getId());
                practitioner.setSpecialties(Arrays.stream(specialtyIds)
                        .mapToObj(id -> specialtyService.getById(id))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet()));
                practitioner = (Practitioner) doctorService.save(practitioner);
                return new ResponseEntity<>(practitioner, HttpStatus.OK);
            case DIETITIAN:
                if (specialtyIds.length > 1)
                    throw new BadRequestException(user.getId(), "Bác sĩ dinh dưỡng chỉ có thể chọn tối đa 1 chuyên môn");
                else if (specialtyIds.length == 0)
                    throw new BadRequestException(user.getId(), "Mảng chuyên môn rỗng");
                else {
                    Dietitian dietitian = (Dietitian) doctorService.getById(user.getId());
                    dietitian.setSpecialty(specialtyService.getById(specialtyIds[0]));
                    dietitian = (Dietitian) doctorService.save(dietitian);
                    return new ResponseEntity<>(dietitian, HttpStatus.OK);
                }
            case SPECIALIST:
                if (specialtyIds.length > 1)
                    throw new BadRequestException(user.getId(), "Bác sĩ chuyên khoa chỉ có thể chọn tối đa 1 chuyên môn");
                else if (specialtyIds.length == 0)
                    throw new BadRequestException(user.getId(), "Mảng chuyên môn rỗng");
                else {
                    Specialist specialist = (Specialist) doctorService.getById(user.getId());
                    specialist.setSpecialty(specialtyService.getById(specialtyIds[0]));
                    specialist = (Specialist) doctorService.save(specialist);
                    return new ResponseEntity<>(specialist, HttpStatus.OK);
                }
        }
        return null;
    }

    @PutMapping("/user/{id}/specialties")
    @Restricted(included = Coordinator.class)
    @ApiOperation(
            value = "Điều phối viên sửa chuyên môn của bác sĩ",
            notes = "Trả về 200 OK hoặc 400 BAD REQUEST hoặc 403 FORBIDDEN",
            tags = "Điều phối viên"
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "Sửa thành công"),
            @ApiResponse(code = 400, message = "Bác sĩ dinh dưỡng/chuyên khoa chỉ có thể chọn tối đa 1 chuyên môn"),
            @ApiResponse(code = 403, message = "Đây không phải bác sĩ")
    })
    @JsonView(Views.Public.class)
    public ResponseEntity<? extends User> editSpecialties(@ApiIgnore @RequestAttribute("User") User user,
                                                          @RequestBody RequestUpdateDoctorSpecialtyDto requestUpdateDoctorSpecialtyDto,
                                                          @PathVariable("id") long id) {
        User u = userService.getById(id);
        if (user.getRole() == Role.PATIENT || user.getRole() == Role.COORDINATOR)
            throw new ForbiddenException(user.getId(), "Đây không phải bác sĩ");
        else return editSpecialties(u, requestUpdateDoctorSpecialtyDto);
    }

    @PutMapping("/user/{id}/diseases")
    @ApiOperation(
            value = "Lập tiền sử bệnh án của bệnh nhân",
            notes = "Trả về 200 OK hoặc 400 BAD REQUEST",
            tags = "Điều phối viên"
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "Lập thành công"),
            @ApiResponse(code = 400, message = "Mã ID của bệnh không hợp lệ")
    })
    @Restricted(included = Coordinator.class)
    @JsonView(Views.Public.class)
    public ResponseEntity<Patient> editPatientDisease(@ApiIgnore @RequestAttribute("User") User user,
                                                      @RequestBody RequestUpdatePatientDiseaseDto requestUpdatePatientDiseaseDto,
                                                      @PathVariable("id") long id) {
        Patient patient = patientService.getById(id);
        try {
            requestUpdatePatientDiseaseDto.getDiseaseIds().forEach(diseaseId ->
                patient.addDisease(diseaseService.getById(diseaseId)));
            return new ResponseEntity<>(patientService.save(patient), HttpStatus.OK);
        } catch (NotFoundException e) {
            throw new BadRequestException(user.getId(), "Mã ID của bệnh không hợp lệ");
        }
    }

    @PostMapping("/user/{id}/results")
    @Restricted(included = Coordinator.class)
    @ApiOperation(
            value = "Điều phối viên khởi tạo thông tin sức khỏe bệnh nhân",
            notes = "Trả về 201 CREATED hoặc 403 FORBIDDEN",
            tags = "Điều phối viên"
    )
    @JsonView(Views.Public.class)
    public ResponseEntity<List<IndexResultResponseDto>> initHealthIndexes(@ApiIgnore @RequestAttribute("User") User user,
                                                                          @RequestBody RequestCreateOtherHealthIndexResultDto requestCreateOtherHealthIndexResultDto,
                                                                          @PathVariable("id") long id) {
        Patient p = patientService.getById(id);
        List<IndexResultResponseDto> result = new ArrayList<>();
        requestCreateOtherHealthIndexResultDto.getRequestDtos().forEach(dto -> {
            HealthIndex index = healthIndexService.getIndex(dto.getIndexId());
            RequestCreateHealthIndexResultDto request = new RequestCreateHealthIndexResultDto();
            request.setScheduleId(0);
            request.setResults(new ArrayList<>(dto.getRequestDtos()));
            result.add(healthIndexResourceController.createResult(p, index, request).getBody());
        });
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
