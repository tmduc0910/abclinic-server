package com.abclinic.server.controller;

import com.abclinic.server.base.BaseController;
import com.abclinic.server.base.Views;
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

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController extends BaseController {

    @Autowired
    public AuthController(UserRepository userRepository, PractitionerRepository practitionerRepository, PatientRepository patientRepository, CoordinatorRepository coordinatorRepository, DietitianRepository dietitianRepository, SpecialistRepository specialistRepository, AlbumRepository albumRepository, ImageRepository imageRepository, MedicalRecordRepository medicalRecordRepository) {
        super(userRepository, practitionerRepository, patientRepository, coordinatorRepository, dietitianRepository, specialistRepository, albumRepository, imageRepository, medicalRecordRepository);
    }

    @Override
    public void init() {
        logger = LoggerFactory.getLogger(AuthController.class);
    }

    @PostMapping(value = "/login/phone")
    @JsonView(Views.Public.class)
    public ResponseEntity<Patient> processLoginByPhoneNumber(@RequestParam(name = "phone") String phoneNumber, @RequestParam(name = "password") String password) {
        Optional<Patient> opt = patientRepository.findByPhoneNumberAndPassword(phoneNumber, password);
        return opt.map(patient -> {
            patient.setUid(UUID.randomUUID().toString());
            save(patient);
            return new ResponseEntity<>(patient, HttpStatus.OK);
        }).orElseThrow(WrongCredentialException::new);
    }

    @PostMapping(value = "/login/email")
    @JsonView(Views.Public.class)
    public ResponseEntity<Patient> processLoginByEmail(@RequestParam(name = "email") String email, @RequestParam(name = "password") String password) {
        return patientRepository.findByEmailAndPassword(email, password).map(patient -> {
            patient.setUid(UUID.randomUUID().toString());
            save(patient);
            return new ResponseEntity<>(patient, HttpStatus.OK);
        }).orElseThrow(WrongCredentialException::new);
    }

    @PostMapping(value = "/admin/login")
    @JsonView(Views.Public.class)
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

    @PostMapping(value = "/sign_up")
    public ResponseEntity<Patient> processSignUp(@RequestParam(name = "email") String email, @RequestParam(name = "password") String password, @RequestParam(name = "name") String name, @RequestParam(name = "gender") int gender, @RequestParam(name = "dob") String dateOfBirth, @RequestParam(name = "phone") String phoneNumber) {
        if (userRepository.findByEmail(email).isPresent() || userRepository.findByPhoneNumber(phoneNumber).isPresent())
            throw new DuplicateValueException();
        Patient patient = new Patient(name, email, gender, tryParse(dateOfBirth), password, phoneNumber);
        patientRepository.save(patient);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/admin/sign_up")
    public ResponseEntity<? extends User> processDoctorSignUp(@RequestParam(name = "role") int role, @RequestParam(name = "email") String email, @RequestParam(name = "password") String password, @RequestParam(name = "name") String name, @RequestParam(name = "gender") int gender, @RequestParam(name = "dob") String dateOfBirth, @RequestParam(name = "phone") String phoneNumber) {
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
