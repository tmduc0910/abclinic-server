package com.abclinic.server.base;

import com.abclinic.server.model.entity.*;
import com.abclinic.server.repository.*;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RestController
public abstract class BaseController {
    private final List<String> formats = Arrays.asList("dd-MM-yyyy", "dd/MM/yyyy");

    protected UserRepository userRepository;
    protected PractitionerRepository practitionerRepository;
    protected PatientRepository patientRepository;
    protected CoordinatorRepository coordinatorRepository;
    protected DietitianRepository dietitianRepository;
    protected SpecialistRepository specialistRepository;
    protected Logger logger;

    @PostConstruct
    public abstract void init();

    protected Date tryParse(String date) {
        for (String format : formats) {
            try {
                return new SimpleDateFormat(format).parse(date);
            } catch (ParseException ignored) {

            }
        }
        return null;
    }

    public <T extends User> void save(T t) {
        if (t instanceof Practitioner)
            practitionerRepository.save((Practitioner) t);
        else if (t instanceof Patient)
            patientRepository.save((Patient) t);
        else if (t instanceof Coordinator)
            coordinatorRepository.save((Coordinator) t);
        else if (t instanceof Dietitian)
            dietitianRepository.save((Dietitian) t);
        else if (t instanceof Specialist)
            specialistRepository.save((Specialist) t);
    }

    @ExceptionHandler(value = BaseRuntimeException.class)
    public ResponseEntity<Object> handleException(BaseRuntimeException e) {
        logger.error(e.getMessage());
        return new ResponseEntity<>(e.getStatus());
    }
}
