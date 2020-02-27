package com.abclinic.server.controller;

import com.abclinic.server.base.BaseController;
import com.abclinic.server.base.Views;
import com.abclinic.server.constant.Role;
import com.abclinic.server.constant.RoleValue;
import com.abclinic.server.constant.Status;
import com.abclinic.server.exception.DuplicateValueException;
import com.abclinic.server.exception.ForbiddenException;
import com.abclinic.server.exception.WrongCredentialException;
import com.abclinic.server.model.entity.user.*;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.*;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * @author tmduc
 * @package com.abclinic.server.controller
 * @created 11/23/2019 3:38 PM
 */
@RestController
@Api(tags = "Xác thực")
@RequestMapping("/auth")
public class AuthController extends BaseController {
    //TODO: Implements Spring Security + AWT

    @Override
    public void init() {
        logger = LoggerFactory.getLogger(AuthController.class);
    }

    @PostMapping(value = "/login/phone")
    @ApiOperation(value = "Người dùng đăng nhập qua SĐT", notes = "Trả về chuỗi UID hoặc 404 NOT FOUND.\n" +
            "Trường UID được trả về sẽ được gán vào header Authorization ở tất cả các API sau đó.")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "phone", value = "SĐT của người dùng", required = true, dataType = "string"),
            @ApiImplicitParam(name = "password", value = "Mật khẩu của người dùng", required = true, dataType = "string")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Đăng nhập thành công"),
            @ApiResponse(code = 404, message = "Đăng nhập thất bại")
    })
    @JsonView(Views.Private.class)
    public ResponseEntity<String> processLoginByPhoneNumber(@RequestParam(name = "phone") String phoneNumber,
                                                            @RequestParam(name = "password") String password) {
        return userRepository.findByPhoneNumberAndPassword(phoneNumber, password).map(u -> {
            u.setUid(UUID.randomUUID().toString());
            save(u);
            return new ResponseEntity<>(u.getUid(), HttpStatus.OK);
        }).orElseThrow(WrongCredentialException::new);
    }

    @PostMapping(value = "/login/email")
    @ApiOperation(value = "Người dùng đăng nhập qua email", notes = "Trả về chuỗi UID hoặc 404 NOT FOUND\n" +
            "Trường UID được trả về sẽ được gán vào header Authorization ở tất cả các API sau đó.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "email", value = "Email của người dùng", required = true, dataType = "string"),
            @ApiImplicitParam(name = "password", value = "Mật khẩu của người dùng", required = true, dataType = "string")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Đăng nhập thành công"),
            @ApiResponse(code = 404, message = "Đăng nhập thất bại")
    })
    @JsonView(Views.Private.class)
    public ResponseEntity<String> processLoginByEmail(@RequestParam(name = "email") String email,
                                                      @RequestParam(name = "password") String password) {
        return userRepository.findByEmailAndPassword(email, password).map(u -> {
            u.setUid(UUID.randomUUID().toString());
            save(u);
            return new ResponseEntity<>(u.getUid(), HttpStatus.OK);
        }).orElseThrow(WrongCredentialException::new);
    }

    /*@PostMapping(value = "/admin/login")
    @ApiOperation(value = "Bác sĩ đăng nhập qua email", notes = "Trả về thông tin cá nhân hoặc 404 NOT FOUND\n" +
            "Trường UID được trả về sẽ được gán vào header Authorization ở tất cả các API sau đó.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "email", value = "Email của người dùng", required = true, dataType = "string"),
            @ApiImplicitParam(name = "password", value = "Mật khẩu của người dùng", required = true, dataType = "string")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Đăng nhập thành công"),
            @ApiResponse(code = 404, message = "Đăng nhập thất bại")
    })
    @JsonView(Views.Private.class)
    public ResponseEntity processDoctorLogin(@RequestParam(name = "email") String email,
                                             @RequestParam(name = "password") String password) {
        Optional<User> opt = userRepository.findByEmailAndPassword(email, password);
        if (opt.isPresent()) {
//            switch (opt.get().getRole()) {
//                case Role.DOCTOR:
//                    return new ResponseEntity<>((Doctor) opt.get(), HttpStatus.OK);
//                case Role.COORDINATOR:
//                        return new ResponseEntity<>((Coordinator) opt.get())
//            }
            User user = opt.get();
            user.setUid(UUID.randomUUID().toString());
//            Class roleClass = user.getRole().getRoleClass();
            save(user);
            return new ResponseEntity(HttpStatus.OK);
        } else throw new WrongCredentialException();
    }

    @PostMapping(value = "/sign_up")
    @Authorized(included = Coordinator.class)
    @ApiOperation(value = "Đăng kí tài khoản cho bệnh nhân", notes = "Trả về 201 CREATED hoặc 409 CONFLICT")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "email", value = "Email của người dùng", required = true, dataType = "string"),
            @ApiImplicitParam(name = "password", value = "Mật khẩu của người dùng", required = true, dataType = "string"),
            @ApiImplicitParam(name = "name", value = "Họ tên người dùng", required = true, dataType = "string"),
            @ApiImplicitParam(name = "gender", value = "Giới tính người dùng (nam, nữ, khác)", allowableValues = "0, 1, 2", required = true, dataType = "int", example = "0"),
            @ApiImplicitParam(name = "dob", value = "Ngày tháng năm sinh của người dùng", format = "YYYY-MM-dd", required = true, dataType = "string"),
            @ApiImplicitParam(name = "phone", value = "SĐT của người dùng", required = true, dataType = "string")
    })
    @ApiResponses({
            @ApiResponse(code = 201, message = "Đăng kí thành công"),
            @ApiResponse(code = 404, message = "Chỉ có điều phối viên mới có thể đăng kí cho bệnh nhân"),
            @ApiResponse(code = 409, message = "Email hoặc SĐT này đã được sử dụng")
    })
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Patient> processSignUp(@ApiIgnore @RequestAttribute(name = "User") User user,
                                                 @RequestParam(name = "email") String email,
                                                 @RequestParam(name = "password") String password,
                                                 @RequestParam(name = "name") String name,
                                                 @RequestParam(name = "gender") int gender,
                                                 @RequestParam(name = "dob") String dateOfBirth,
                                                 @RequestParam(name = "phone") String phoneNumber) {
        if (user.getRole() != Role.COORDINATOR)
            throw new ForbiddenException(user.getId());
        if (userRepository.findByEmail(email).isPresent() || userRepository.findByPhoneNumber(phoneNumber).isPresent())
            throw new DuplicateValueException();
        Patient patient = new Patient(name, email, gender, tryParse(dateOfBirth), password, phoneNumber);
        patientRepository.save(patient);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }*/

    @PostMapping(value = "/sign_up")
    @ApiOperation(value = "Đăng kí tài khoản", notes = "Trả về 201 CREATED hoặc 409 CONFLICT")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "role", value = "Kiểu người dùng (đa khoa, chuyên khoa, dinh dưỡng, điều phối, bệnh nhân)", required = true, allowableValues = "0, 1, 2, 3, 4", dataType = "int", example = "0"),
            @ApiImplicitParam(name = "email", value = "Email của người dùng", required = true, dataType = "string"),
            @ApiImplicitParam(name = "password", value = "Mật khẩu của người dùng", required = true, dataType = "string"),
            @ApiImplicitParam(name = "name", value = "Họ tên người dùng", required = true, dataType = "string"),
            @ApiImplicitParam(name = "gender", value = "Giới tính người dùng (nam, nữ, khác)", allowableValues = "0, 1, 2", required = true, dataType = "int", example = "0"),
            @ApiImplicitParam(name = "dob", value = "Ngày tháng năm sinh của người dùng (dd/MM/yyyy)", format = "dd/MM/yyyy", required = true, dataType = "string"),
            @ApiImplicitParam(name = "phone", value = "SĐT của người dùng", required = true, dataType = "string")
    })
    @ApiResponses({
            @ApiResponse(code = 201, message = "Đăng kí thành công"),
            @ApiResponse(code = 400, message = "Chỉ có điều phối viên mới được đăng ký cho bệnh nhân"),
            @ApiResponse(code = 409, message = "Email hoặc SĐT này đã được sử dụng")
    })
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity processSignUp(@Nullable @ApiIgnore @RequestAttribute(name = "User") User user,
                                        @NotNull @RequestParam(name = "role") int role,
                                        @RequestParam(name = "email") String email,
                                        @RequestParam(name = "password") String password,
                                        @RequestParam(name = "name") String name,
                                        @RequestParam(name = "gender") int gender,
                                        @RequestParam(name = "dob") String dateOfBirth,
                                        @RequestParam(name = "phone") String phoneNumber) {
        if (userRepository.findByEmail(email).isPresent() || userRepository.findByPhoneNumber(phoneNumber).isPresent())
            throw new DuplicateValueException();
        if (user != null) {
            if (user.getRole() != Role.COORDINATOR)
                throw new ForbiddenException(user.getId(), "Chỉ có điều phối viên mới được đăng ký cho bệnh nhân");
            else role = Role.PATIENT.getValue();
        }
        User u = null;
        switch (role) {
            case RoleValue.PRACTITIONER:
                u = new Practitioner(name, email, gender, tryParse(dateOfBirth), password, phoneNumber);
                break;
            case RoleValue.COORDINATOR:
                u = new Coordinator(name, email, gender, tryParse(dateOfBirth), password, phoneNumber);
                break;
            case RoleValue.DIETITIAN:
                u = new Dietitian(name, email, gender, tryParse(dateOfBirth), password, phoneNumber);
                break;
            case RoleValue.SPECIALIST:
                u = new Specialist(name, email, gender, tryParse(dateOfBirth), password, phoneNumber);
                break;
            case RoleValue.PATIENT:
                u = new Patient(name, email, gender, tryParse(dateOfBirth), password, phoneNumber);
                u.setStatus(Status.User.UNASSIGNED);
                break;
        }
        save(u);
        return new ResponseEntity(HttpStatus.CREATED);
    }
}
