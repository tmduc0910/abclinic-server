package com.abclinic.server.base;

import com.abclinic.server.constant.RoleValue;
import com.abclinic.server.model.dto.ErrorDto;
import com.abclinic.server.model.entity.*;
import com.abclinic.server.model.entity.user.*;
import com.abclinic.server.repository.*;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@RestController
public abstract class BaseController {

    protected UserRepository userRepository;
    protected PractitionerRepository practitionerRepository;
    protected PatientRepository patientRepository;
    protected CoordinatorRepository coordinatorRepository;
    protected DietitianRepository dietitianRepository;
    protected SpecialistRepository specialistRepository;
    protected AlbumRepository albumRepository;
    protected ImageRepository imageRepository;
    protected MedicalRecordRepository medicalRecordRepository;
    protected DietitianRecordRepository dietitianRecordRepository;
    protected QuestionRepository questionRepository;
    protected ReplyRepository replyRepository;
    protected SpecialtyRepository specialtyRepository;
    protected DiseaseRepository diseaseRepository;
    protected Logger logger;

    @Autowired
    public BaseController(UserRepository userRepository, PractitionerRepository practitionerRepository, PatientRepository patientRepository, CoordinatorRepository coordinatorRepository, DietitianRepository dietitianRepository, SpecialistRepository specialistRepository, AlbumRepository albumRepository, ImageRepository imageRepository, MedicalRecordRepository medicalRecordRepository, DietitianRecordRepository dietitianRecordRepository, QuestionRepository questionRepository, ReplyRepository replyRepository, SpecialtyRepository specialtyRepository, DiseaseRepository diseaseRepository) {
        this.userRepository = userRepository;
        this.practitionerRepository = practitionerRepository;
        this.patientRepository = patientRepository;
        this.coordinatorRepository = coordinatorRepository;
        this.dietitianRepository = dietitianRepository;
        this.specialistRepository = specialistRepository;
        this.albumRepository = albumRepository;
        this.imageRepository = imageRepository;
        this.medicalRecordRepository = medicalRecordRepository;
        this.dietitianRecordRepository = dietitianRecordRepository;
        this.questionRepository = questionRepository;
        this.replyRepository = replyRepository;
        this.specialtyRepository = specialtyRepository;
        this.diseaseRepository = diseaseRepository;
    }

    @PostConstruct
    public abstract void init();

    protected LocalDate tryParse(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return LocalDate.parse(date, formatter);
    }

    public void save(Object o) {
        if (o instanceof Practitioner)
            practitionerRepository.save((Practitioner) o);
        else if (o instanceof Patient)
            patientRepository.save((Patient) o);
        else if (o instanceof Coordinator)
            coordinatorRepository.save((Coordinator) o);
        else if (o instanceof Dietitian)
            dietitianRepository.save((Dietitian) o);
        else if (o instanceof Specialist)
            specialistRepository.save((Specialist) o);
        else if (o instanceof ImageAlbum)
            albumRepository.save((ImageAlbum) o);
        else if (o instanceof Image)
            imageRepository.save((Image) o);
        else if (o instanceof MedicalRecord)
            medicalRecordRepository.save((MedicalRecord) o);
        else if (o instanceof Question)
            questionRepository.save((Question) o);
        else if (o instanceof Reply)
            replyRepository.save((Reply) o);
    }



    @ExceptionHandler(value = BaseRuntimeException.class)
    public ResponseEntity<ErrorDto> handleException(BaseRuntimeException e) {
        logger.error(e.getMessage());
        return new ResponseEntity<>(new ErrorDto(e.getMessage()), e.getStatus());
    }
}
