//package com.abclinic.server.controller;
//
//import com.abclinic.server.model.entity.Patient;
//import com.abclinic.server.repository.PatientRepository;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//public class AuthController {
//    @Autowired
//    private PatientRepository patientRepository;
//
//    private Logger logger = LoggerFactory.getLogger(AuthController.class);
//
//    @RequestMapping(value = "/login", method = RequestMethod.POST)
//    public ResponseEntity<Patient> login(@RequestParam(name = "phone_number") String phoneNumber, @RequestParam(name = "password") String password) {
//        logger.info(phoneNumber + " : " + password);
//        return new ResponseEntity<>(patientRepository.findByPhoneNumberAndPassword(phoneNumber, password).orElse(new Patient()), HttpStatus.ACCEPTED);
//    }
//}
