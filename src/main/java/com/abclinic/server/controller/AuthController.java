package com.abclinic.server.controller;

import com.abclinic.server.base.BaseController;
import com.abclinic.server.base.Views;
import com.abclinic.server.constant.Role;
import com.abclinic.server.constant.RoleValue;
import com.abclinic.server.exception.DuplicateValueException;
import com.abclinic.server.exception.ForbiddenException;
import com.abclinic.server.exception.WrongCredentialException;
import com.abclinic.server.model.entity.user.*;
import com.abclinic.server.repository.*;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.*;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.NotNull;
import java.util.Optional;
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

    public AuthController(UserRepository userRepository, PractitionerRepository practitionerRepository, PatientRepository patientRepository, CoordinatorRepository coordinatorRepository, DietitianRepository dietitianRepository, SpecialistRepository specialistRepository, AlbumRepository albumRepository, ImageRepository imageRepository, MedicalRecordRepository medicalRecordRepository, DietitianRecordRepository dietitianRecordRepository, QuestionRepository questionRepository, ReplyRepository replyRepository, SpecialtyRepository specialtyRepository, DiseaseRepository diseaseRepository) {
        super(userRepository, practitionerRepository, patientRepository, coordinatorRepository, dietitianRepository, specialistRepository, albumRepository, imageRepository, medicalRecordRepository, dietitianRecordRepository, questionRepository, replyRepository, specialtyRepository, diseaseRepository);
    }

    @Override
    public void init() {
        logger = LoggerFactory.getLogger(AuthController.class);
    }

    @PostMapping(value = "/login/phone")
    @ApiOperation(value = "Bệnh nhân đăng nhập qua SĐT", notes = "Trả về thông tin cá nhân hoặc 404 NOT FOUND.\n" +
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
    public ResponseEntity<Patient> processLoginByPhoneNumber(@RequestParam(name = "phone") String phoneNumber,
                                                             @RequestParam(name = "password") String password) {
        Optional<Patient> opt = patientRepository.findByPhoneNumberAndPassword(phoneNumber, password);
        //Gán giá trị session cho người dùng
        return opt.map(patient -> {
            patient.setUid(UUID.randomUUID().toString());
            save(patient);
            return new ResponseEntity<>(patient, HttpStatus.OK);
        }).orElseThrow(WrongCredentialException::new);
    }

    @PostMapping(value = "/login/email")
    @ApiOperation(value = "Bệnh nhân đăng nhập qua email", notes = "Trả về thông tin cá nhân hoặc 404 NOT FOUND\n" +
            "Trường UID được trả về sẽ được gán vào header Authorization ở tất cả các API sau đó.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "email", value = "Email của người dùng", required = true, dataType = "string"),
            @ApiImplicitParam(name = "password", value = "Mật khẩu của người dùng", required = true, dataType = "string")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Đăng nhập thành công", response = Patient.class),
            @ApiResponse(code = 404, message = "Đăng nhập thất bại")
    })
    @JsonView(Views.Private.class)
    public ResponseEntity<Patient> processLoginByEmail(@RequestParam(name = "email") String email,
                                                       @RequestParam(name = "password") String password) {
        return patientRepository.findByEmailAndPassword(email, password).map(patient -> {
            patient.setUid(UUID.randomUUID().toString());
            save(patient);
            return new ResponseEntity<>(patient, HttpStatus.OK);
        }).orElseThrow(WrongCredentialException::new);
    }

    @PostMapping(value = "/admin/login")
    @ApiOperation(value = "Bác sĩ đăng nhập qua email", notes = "Trả về thông tin cá nhân hoặc 404 NOT FOUND\n" +
            "Trường UID được trả về sẽ được gán vào header Authorization ở tất cả các API sau đó.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "email", value = "Email của người dùng", required = true, dataType = "string"),
            @ApiImplicitParam(name = "password", value = "Mật khẩu của người dùng", required = true, dataType = "string")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Đăng nhập thành công", response = Patient.class),
            @ApiResponse(code = 404, message = "Đăng nhập thất bại")
    })
    @JsonView(Views.Private.class)
    public ResponseEntity<? extends User> processDoctorLogin(@RequestParam(name = "email") String email,
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
            Class roleClass = user.getRole().getRoleClass();
            save(user);
            return new ResponseEntity(roleClass.cast(user), HttpStatus.OK);
        } else throw new WrongCredentialException();
    }

    @PostMapping(value = "/sign_up")
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
    }

    @PostMapping(value = "/admin/sign_up")
    @ApiOperation(value = "Đăng kí tài khoản cho bác sĩ", notes = "Trả về 201 CREATED hoặc 409 CONFLICT")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "role", value = "Kiểu bác sĩ (đa khoa, chuyên khoa, dinh dưỡng, điều phối)", required = true, allowableValues = "0, 1, 2, 3", dataType = "int", example = "0"),
            @ApiImplicitParam(name = "email", value = "Email của người dùng", required = true, dataType = "string"),
            @ApiImplicitParam(name = "password", value = "Mật khẩu của người dùng", required = true, dataType = "string"),
            @ApiImplicitParam(name = "name", value = "Họ tên người dùng", required = true, dataType = "string"),
            @ApiImplicitParam(name = "gender", value = "Giới tính người dùng (nam, nữ, khác)", allowableValues = "0, 1, 2", required = true, dataType = "int", example = "0"),
            @ApiImplicitParam(name = "dob", value = "Ngày tháng năm sinh của người dùng", format = "YYYY-MM-dd", required = true, dataType = "string"),
            @ApiImplicitParam(name = "phone", value = "SĐT của người dùng", required = true, dataType = "string")
    })
    @ApiResponses({
            @ApiResponse(code = 201, message = "Đăng kí thành công"),
            @ApiResponse(code = 404, message = "Chỉ được đăng ký cho bác sĩ"),
            @ApiResponse(code = 409, message = "Email hoặc SĐT này đã được sử dụng")
    })
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<? extends User> processDoctorSignUp(@NotNull @RequestParam(name = "role") int role,
                                                              @RequestParam(name = "email") String email,
                                                              @RequestParam(name = "password") String password,
                                                              @RequestParam(name = "name") String name,
                                                              @RequestParam(name = "gender") int gender,
                                                              @RequestParam(name = "dob") String dateOfBirth,
                                                              @RequestParam(name = "phone") String phoneNumber) {
        if (userRepository.findByEmail(email).isPresent() || userRepository.findByPhoneNumber(phoneNumber).isPresent())
            throw new DuplicateValueException();
        User doc;
        switch (role) {
            case RoleValue.PRACTITIONER:
                doc = new Practitioner(name, email, gender, tryParse(dateOfBirth), password, phoneNumber);
                break;
            case RoleValue.COORDINATOR:
                doc = new Coordinator(name, email, gender, tryParse(dateOfBirth), password, phoneNumber);
                break;
            case RoleValue.DIETITIAN:
                doc = new Dietitian(name, email, gender, tryParse(dateOfBirth), password, phoneNumber);
                break;
            case RoleValue.SPECIALIST:
                doc = new Specialist(name, email, gender, tryParse(dateOfBirth), password, phoneNumber);
                break;
            default:
                doc = null;
                break;
        }
        save(doc);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
