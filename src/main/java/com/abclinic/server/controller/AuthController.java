package com.abclinic.server.controller;

import com.abclinic.server.base.BaseController;
import com.abclinic.server.constant.RoleValue;
import com.abclinic.server.exception.DuplicateValueException;
import com.abclinic.server.exception.UnauthorizedActionException;
import com.abclinic.server.exception.WrongCredentialException;
import com.abclinic.server.model.entity.user.*;
import com.abclinic.server.repository.*;
import com.fasterxml.jackson.annotation.JsonView;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.UUID;

/**
 * @package com.abclinic.server.controller
 * @author tmduc
 * @created 11/23/2019 3:38 PM
 */
@RestController
@RequestMapping("/auth")
public class AuthController extends BaseController {

    public AuthController(UserRepository userRepository, PractitionerRepository practitionerRepository, PatientRepository patientRepository, CoordinatorRepository coordinatorRepository, DietitianRepository dietitianRepository, SpecialistRepository specialistRepository, AlbumRepository albumRepository, ImageRepository imageRepository, MedicalRecordRepository medicalRecordRepository, QuestionRepository questionRepository, ReplyRepository replyRepository, SpecialtyRepository specialtyRepository) {
        super(userRepository, practitionerRepository, patientRepository, coordinatorRepository, dietitianRepository, specialistRepository, albumRepository, imageRepository, medicalRecordRepository, questionRepository, replyRepository, specialtyRepository);
    }

    @Override
    public void init() {
        logger = LoggerFactory.getLogger(AuthController.class);
    }

    /**
     * @apiNote hàm xử lý đăng nhập của bệnh nhân bằng số điện thoại
     * @param phoneNumber SĐT của người dùng. VD: "01234567"
     * @param password mật khẩu cá nhân của người dùng. VD: "abcxyz"
     * @return object chứa thông tin cá nhân của bệnh nhân và HTTPStatus 200 OK
     * @throws WrongCredentialException HTTPStatus 404 NOT FOUND
     */
    @PostMapping(value = "/login/phone")
    public ResponseEntity<Patient> processLoginByPhoneNumber(@RequestParam(name = "phone") String phoneNumber, @RequestParam(name = "password") String password) {
        Optional<Patient> opt = patientRepository.findByPhoneNumberAndPassword(phoneNumber, password);
        /**
         * Gán giá trị session cho người dùng
         */
        return opt.map(patient -> {
            patient.setUid(UUID.randomUUID().toString());
            save(patient);
            return new ResponseEntity<>(patient, HttpStatus.OK);
        }).orElseThrow(WrongCredentialException::new);
    }

    /**
     * @apiNote hàm xử lý đăng nhập của bệnh nhân bằng email
     * @uri /auth/login/email
     * @httpMethod POST
     * @param email email của người dùng. VD: "mail@example.com"
     * @param password mật khẩu cá nhân của người dùng. VD: "abcxyz"
     * @return object chứa thông tin cá nhân của bệnh nhân và HTTPStatus 200 OK
     * @throws WrongCredentialException HTTPStatus 404 NOT FOUND
     */
    @PostMapping(value = "/login/email")
    public ResponseEntity<Patient> processLoginByEmail(@RequestParam(name = "email") String email, @RequestParam(name = "password") String password) {
        return patientRepository.findByEmailAndPassword(email, password).map(patient -> {
            patient.setUid(UUID.randomUUID().toString());
            save(patient);
            return new ResponseEntity<>(patient, HttpStatus.OK);
        }).orElseThrow(WrongCredentialException::new);
    }

    /**
     * @apiNote hàm xử lý đăng nhập của bác sĩ bằng email
     * @uri /auth/admin/login
     * @httpMethod POST
     * @param email email của người dùng. VD: "mail@example.com"
     * @param password mật khẩu cá nhân của người dùng. VD: "abcxyz"
     * @return object chứa thông tin cá nhân của bác sĩ và HTTPStatus 200 OK
     * @throws WrongCredentialException HTTPStatus 404 NOT FOUND
     */
    @PostMapping(value = "/admin/login")
    public ResponseEntity<? extends User> processDoctorLogin(@RequestParam(name = "email") String email, @RequestParam(name = "password") String password) {
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

    /**
     * @apiNote hàm xử lý đăng ký tài khoản của bệnh nhân
     * @uri /auth/sigh_up
     * @httpMethod POST
     * @param email email của người dùng. VD: "mail@example.com"
     * @param password mật khẩu cá nhân của người dùng. VD: "abcxyz"
     * @param name họ và tên đầy đủ của bệnh nhân. VD: "Trần Văn A"
     * @param gender giới tính của bệnh nhân. Nam(1), Nữ(2), Khác(3)
     * @param dateOfBirth ngày, tháng, năm sinh của bệnh nhân, dưới dạng "dd/MM/yyyy"
     * @param phoneNumber số điện thoại của bệnh nhân. VD: "01234567"
     * @return HTTPStatus 200 OK
     * @throws DuplicateValueException HTTPStatus 409 CONFLICT
     */
    @PostMapping(value = "/sign_up")
    public ResponseEntity<Patient> processSignUp(@RequestParam(name = "email") String email, @RequestParam(name = "password") String password, @RequestParam(name = "name") String name, @RequestParam(name = "gender") int gender, @RequestParam(name = "dob") String dateOfBirth, @RequestParam(name = "phone") String phoneNumber) {
        if (userRepository.findByEmail(email).isPresent() || userRepository.findByPhoneNumber(phoneNumber).isPresent())
            throw new DuplicateValueException();
        Patient patient = new Patient(name, email, gender, tryParse(dateOfBirth), password, phoneNumber);
        patientRepository.save(patient);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * @apiNote hàm xử lý đăng ký tài khoản của bác sĩ
     * @uri /auth/admin/sigh_up
     * @httpMethod POST
     * @param role phân loại của bác sĩ. Đa khoa (0), Chuyên khoa (1), Dinh dưỡng(2), Điều phối (3)
     * @param email email của người dùng. VD: "mail@example.com"
     * @param password mật khẩu cá nhân của người dùng. VD: "abcxyz"
     * @param name họ và tên đầy đủ của bệnh nhân. VD: "Trần Văn A"
     * @param gender giới tính của bệnh nhân. Nam(1), Nữ(2), Khác(3)
     * @param dateOfBirth ngày, tháng, năm sinh của bệnh nhân, dưới dạng "dd/MM/yyyy"
     * @param phoneNumber số điện thoại của bệnh nhân. VD: "01234567"
     * @return HTTPStatus 200 OK
     * @throws DuplicateValueException HTTPStatus 409 CONFLICT
     * @throws UnauthorizedActionException HTTPStatus 401 UNAUTHORIZED
     */
    @PostMapping(value = "/admin/sign_up")
    public ResponseEntity<? extends User> processDoctorSignUp(@NotNull @RequestParam(name = "role") int role, @RequestParam(name = "email") String email, @RequestParam(name = "password") String password, @RequestParam(name = "name") String name, @RequestParam(name = "gender") int gender, @RequestParam(name = "dob") String dateOfBirth, @RequestParam(name = "phone") String phoneNumber) {
        if (role > RoleValue.COORDINATOR)
            throw new UnauthorizedActionException();
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
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
