package com.abclinic.server.controller;

import com.abclinic.server.annotation.authorized.Restricted;
import com.abclinic.server.common.base.CustomController;
import com.abclinic.server.common.base.Views;
import com.abclinic.server.common.constant.UserStatus;
import com.abclinic.server.exception.BadRequestException;
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

import javax.annotation.Nullable;
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
    @ApiImplicitParams({
            @ApiImplicitParam(name = "email", value = "Email của người dùng", required = true, dataType = "string"),
            @ApiImplicitParam(name = "phone", value = "SĐT của người dùng", required = true, dataType = "string"),
            @ApiImplicitParam(name = "address", value = "Địa chỉ cư trú của bệnh nhân", required = true, dataType = "string"),
            @ApiImplicitParam(name = "description", value = "Mô tả lí lịch của bác sĩ", required = true, dataType = "string")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Chỉnh sửa thành công")
    })
    public ResponseEntity<? extends User> editUserInfo(@ApiIgnore @RequestAttribute("User") User user,
                                       @Nullable @RequestParam("email") String email,
                                       @Nullable @RequestParam("phone") String phone,
                                       @Nullable @RequestParam("address") String address,
                                       @Nullable @RequestParam("description") String description) {
        switch (user.getRole()) {
            case PATIENT:
                Patient patient = patientService.getById(user.getId());
                patient.setAddress(address);
                patientService.save(patient);
            case COORDINATOR:
            case SPECIALIST:
                Specialist specialist = (Specialist) doctorService.getById(user.getId());
                specialist.setDescription(description);
                doctorService.save(specialist);
            case PRACTITIONER:
                Practitioner practitioner = (Practitioner) doctorService.getById(user.getId());
                practitioner.setDescription(description);
                doctorService.save(practitioner);
            case DIETITIAN:
                Dietitian dietitian = (Dietitian) doctorService.getById(user.getId());
                dietitian.setDescription(description);
                doctorService.save(dietitian);
            default:
                user.setEmail(email);
                user.setPhoneNumber(phone);
                user = userService.save(user);
                break;
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @DeleteMapping("/user")
    @Restricted(included = Coordinator.class)
    @ApiOperation(
            value = "Hủy kích hoạt tài khoản",
            notes = "Trả về 200 OK",
            tags = "Điều phối viên"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "ID của người dùng cần hủy", required = true, dataType = "long", example = "1")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Hủy thành công")
    })
    public ResponseEntity deleteUser(@ApiIgnore @RequestAttribute("User") User user,
                                     @RequestParam("id") long userId) {
        User u = userService.getById(userId);
        userService.deactivateUser(u);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PutMapping("/user/specialties")
    @ApiOperation(
            value = "Sửa chuyên môn của bác sĩ",
            notes = "Trả về 200 OK hoặc 400 BAD REQUEST",
            tags = {"Đa khoa", "Chuyên khoa", "Dinh dưỡng"}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "specialties", required = true, value = "Danh sách Mã ID của chuyên môn", dataType = "array")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Sửa thành công"),
            @ApiResponse(code = 400, message = "Bác sĩ dinh dưỡng/chuyên khoa chỉ có thể chọn tối đa 1 chuyên môn")
    })
    @Restricted(excluded = {Patient.class, Coordinator.class})
    public ResponseEntity<? extends User> editSpecialties(@ApiIgnore @RequestAttribute("User") User user,
                                          @RequestParam("specialties") long[] specialtyIds) {
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
