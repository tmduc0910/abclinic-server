package com.abclinic.server.controller;

import com.abclinic.server.constant.Role;
import com.abclinic.server.constant.RoleValue;
import com.abclinic.server.model.entity.*;
import com.abclinic.server.repository.DoctorRepository;
import com.abclinic.server.repository.PatientRepository;
import com.abclinic.server.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.print.Doc;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Optional;

@RestController
public class AuthController {

    private PatientRepository patientRepository;
    private UserRepository userRepository;
    private SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
    private Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    public AuthController(PatientRepository patientRepository, UserRepository userRepository) {
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
    }


    @PostMapping(value = "/auth/login/phone")
    public ResponseEntity<Patient> processLoginByPhoneNumber(@RequestParam(name = "phone") String phoneNumber, @RequestParam(name = "password") String password) {
        Optional<Patient> opt = patientRepository.findByPhoneNumberAndPassword(phoneNumber, password);
        return opt.map(patient -> new ResponseEntity<>(patient, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping(value = "/auth/login/email")
    public ResponseEntity<Patient> processLoginByEmail(@RequestParam(name = "email") String email, @RequestParam(name = "password") String password) {
        return patientRepository.findByEmailAndPassword(email, password).map(patient -> new ResponseEntity<>(patient, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping(value = "/auth/admin/login")
    public ResponseEntity<? extends User> processDoctorLogin(@RequestParam(name = "email") String email, @RequestParam(name = "password") String password) {
        Optional<User> opt = userRepository.findByEmailAndPassword(email, password);
        if (opt.isPresent()) {
//            switch (opt.get().getRole()) {
//                case Role.DOCTOR:
//                    return new ResponseEntity<>((Doctor) opt.get(), HttpStatus.OK);
//                case Role.COORDINATOR:
//                        return new ResponseEntity<>((Coordinator) opt.get())
//            }
            Class roleClass = opt.get().getRole().getRoleClass();
            return new ResponseEntity(roleClass.cast(opt.get()), HttpStatus.OK);
        } else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping(value = "/auth/sign_up")
    public ResponseEntity<Patient> processSignUp(@RequestParam(name = "email") String email, @RequestParam(name = "password") String password, @RequestParam(name = "name") String name, @RequestParam(name = "gender") int gender, @RequestParam(name = "dob") String dateOfBirth, @RequestParam(name = "phone") String phoneNumber) {
        try {
            Patient patient = new Patient(name, email, gender, formatter.parse(dateOfBirth), password, phoneNumber);
            patientRepository.save(patient);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ParseException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.IM_USED);
        }
    }

    @PostMapping(value = "/auth/admin/sign_up")
    public ResponseEntity<? extends User> processDoctorSignUp(@RequestParam(name = "role") int role, @RequestParam(name = "email") String email, @RequestParam(name = "password") String password, @RequestParam(name = "name") String name, @RequestParam(name = "gender") int gender, @RequestParam(name = "dob") String dateOfBirth, @RequestParam(name = "phone") String phoneNumber) {
        try {
            User doc;// = new Patient(name, email, gender, formatter.parse(dateOfBirth), password, phoneNumber);
            switch (role) {
                case RoleValue.DOCTOR:
                    doc = new Doctor(name, email, gender, formatter.parse(dateOfBirth), password, phoneNumber);
                    break;
                case RoleValue.COORDINATOR:
                    doc = new Coordinator(name, email, gender, formatter.parse(dateOfBirth), password, phoneNumber);
                    break;
                case RoleValue.DIETITIAN:
                    doc = new Dietitian(name, email, gender, formatter.parse(dateOfBirth), password, phoneNumber);
                    break;
                case RoleValue.SPECIALIST:
                    doc = new Specialist(name, email, gender, formatter.parse(dateOfBirth), password, phoneNumber);
                    break;
                default:
                    doc = null;
                    break;
            }
            userRepository.save(doc);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ParseException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.IM_USED);
        }
    }
}
