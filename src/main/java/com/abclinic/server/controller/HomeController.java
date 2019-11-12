package com.abclinic.server.controller;

import com.abclinic.server.model.entity.Doctor;
import com.abclinic.server.model.entity.Patient;
import com.abclinic.server.model.entity.User;
import com.abclinic.server.repository.DoctorRepository;
import com.abclinic.server.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class HomeController {
    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @GetMapping("/home")
    public ResponseEntity<List<Doctor>> getAll() {
        System.out.println("home");
        return new ResponseEntity<>(doctorRepository.findAll(), HttpStatus.ACCEPTED);
    }

    @PostMapping("/login")
    public ResponseEntity<Patient> login(@RequestParam(name = "phone_number") String phoneNumber, @RequestParam(name = "password") String password) {
        Optional optional = patientRepository.findByPhoneNumberAndPassword(phoneNumber, password);
        if (optional.isPresent()) {
            return new ResponseEntity<>((Patient) optional.get(), HttpStatus.OK);
        } else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
