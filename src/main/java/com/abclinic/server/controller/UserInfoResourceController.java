package com.abclinic.server.controller;

import com.abclinic.server.annotation.authorized.Restricted;
import com.abclinic.server.common.base.CustomController;
import com.abclinic.server.common.base.Views;
import com.abclinic.server.common.constant.UserStatus;
import com.abclinic.server.exception.BadRequestException;
import com.abclinic.server.model.dto.request.delete.RequestDeleteDto;
import com.abclinic.server.model.dto.request.post.RequestReactivateUser;
import com.abclinic.server.model.dto.request.put.RequestUpdateDoctorSpecialtyDto;
import com.abclinic.server.model.dto.request.put.RequestUpdateUserInfoDto;
import com.abclinic.server.model.entity.user.*;
import com.abclinic.server.service.entity.DoctorService;
import com.abclinic.server.service.entity.PatientService;
import com.abclinic.server.service.entity.SpecialtyService;
import com.abclinic.server.service.entity.UserService;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Arrays;
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

    @Override
    public void init() {
        this.logger = LoggerFactory.getLogger(UserInfoResourceController.class);
    }

    @GetMapping("/user")
    @ApiOperation(
            value = "Xem thông tin cá nhân",
            notes = "Trả về chi tiết thông tin cá nhân"
    )
    @JsonView(Views.Public.class)
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
            notes = "Trả về 200 OK",
            tags = "Điều phối viên"
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "Chỉnh sửa thành công")
    })
    public ResponseEntity<? extends User> editUserInfo(@ApiIgnore @RequestAttribute("User") User user,
                                   @RequestBody RequestUpdateUserInfoDto requestUpdateUserInfoDto,
                                   @PathVariable("id") long id) {
        User u = userService.getById(id);
        return editUserInfo(u, requestUpdateUserInfoDto);
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
}
