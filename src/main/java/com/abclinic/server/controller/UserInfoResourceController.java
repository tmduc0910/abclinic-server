package com.abclinic.server.controller;

import com.abclinic.server.annotation.authorized.Restricted;
import com.abclinic.server.common.base.BaseController;
import com.abclinic.server.common.base.Views;
import com.abclinic.server.exception.BadRequestException;
import com.abclinic.server.model.entity.user.*;
import com.abclinic.server.service.entity.PatientService;
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
public class UserInfoResourceController extends BaseController {

    @Autowired
    private PatientService patientService;

    @Override
    public void init() {
        this.logger = LoggerFactory.getLogger(UserInfoResourceController.class);
    }

    @GetMapping("/user")
    @ApiOperation(
            value = "Xem thông tin cá nhân",
            notes = "Trả về chi tiết thông tin cá nhân"
    )
    @JsonView(Views.Private.class)
    public ResponseEntity getUserInfo(@ApiIgnore @RequestAttribute("User") User user) {
        return new ResponseEntity(user, HttpStatus.OK);
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
    public ResponseEntity editUserInfo(@ApiIgnore @RequestAttribute("User") User user,
                                       @Nullable @RequestParam("email") String email,
                                       @Nullable @RequestParam("phone") String phone,
                                       @Nullable @RequestParam("address") String address,
                                       @Nullable @RequestParam("description") String description) {
        switch (user.getRole()) {
            case PATIENT:
                Patient patient = patientService.getById(user.getId());
                patient.setAddress(address);
                save(patient);
            case COORDINATOR:
            case SPECIALIST:
                Specialist specialist = specialistRepository.findById(user.getId());
                specialist.setDescription(description);
                save(specialist);
            case PRACTITIONER:
                Practitioner practitioner = practitionerRepository.findById(user.getId());
                practitioner.setDescription(description);
                save(practitioner);
            case DIETITIAN:
                Dietitian dietitian = dietitianRepository.findById(user.getId());
                dietitian.setDescription(description);
                save(dietitian);
            default:
                user.setEmail(email);
                user.setPhoneNumber(phone);
                save(user);
                break;
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @PutMapping("/user/specialties")
    @ApiOperation(
            value = "Sửa chuyên môn của bác sĩ",
            notes = "Trả về 200 OK hoặc 400 BAD REQUEST"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "specialties", required = true, value = "Danh sách Mã ID của chuyên môn", dataType = "array")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Sửa thành công"),
            @ApiResponse(code = 400, message = "Bác sĩ dinh dưỡng/chuyên khoa chỉ có thể chọn tối đa 1 chuyên môn")
    })
    @Restricted(excluded = {Patient.class, Coordinator.class})
    public ResponseEntity editSpecialties(@ApiIgnore @RequestAttribute("User") User user,
                                          @RequestParam("specialties") long[] specialtyIds) {
        switch (user.getRole()) {
            case PRACTITIONER:
                Practitioner practitioner = practitionerRepository.findById(user.getId());
                practitioner.setSpecialties(Arrays.stream(specialtyIds)
                        .mapToObj(id -> specialtyRepository.findById(id))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet()));
                save(practitioner);
                break;
            case DIETITIAN:
                if (specialtyIds.length > 1)
                    throw new BadRequestException(user.getId(), "Bác sĩ dinh dưỡng chỉ có thể chọn tối đa 1 chuyên môn");
                else {
                    Dietitian dietitian = dietitianRepository.findById(user.getId());
                    dietitian.setSpecialty(specialtyRepository.findById(specialtyIds[0]));
                    save(dietitian);
                }
                break;
            case SPECIALIST:
                if (specialtyIds.length > 1)
                    throw new BadRequestException(user.getId(), "Bác sĩ chuyên khoa chỉ có thể chọn tối đa 1 chuyên môn");
                else {
                    Specialist specialist = specialistRepository.findById(user.getId());
                    specialist.setSpecialty(specialtyRepository.findById(specialtyIds[0]));
                    save(specialist);
                }
        }
        return new ResponseEntity(HttpStatus.OK);
    }
}
