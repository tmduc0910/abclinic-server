package com.abclinic.server.base;

import com.abclinic.server.repository.DoctorRepository;
import com.abclinic.server.repository.PatientRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

@RestController
public abstract class BaseController {
    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    private Logger logger;

    @PostConstruct
    abstract void init();
}
