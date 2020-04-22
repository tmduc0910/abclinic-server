package com.abclinic.server.common.base;

import com.abclinic.server.model.dto.ErrorDto;
import com.abclinic.server.model.entity.*;
import com.abclinic.server.model.entity.notification.Notification;
import com.abclinic.server.model.entity.payload.HealthIndex;
import com.abclinic.server.model.entity.payload.HealthIndexSchedule;
import com.abclinic.server.model.entity.payload.Inquiry;
import com.abclinic.server.model.entity.payload.Reply;
import com.abclinic.server.model.entity.payload.record.DietRecord;
import com.abclinic.server.model.entity.payload.record.MedicalRecord;
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

@RestController
public abstract class BaseController {

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected PractitionerRepository practitionerRepository;

    @Autowired
    protected PatientRepository patientRepository;

    @Autowired
    protected CoordinatorRepository coordinatorRepository;

    @Autowired
    protected DietitianRepository dietitianRepository;

    @Autowired
    protected SpecialistRepository specialistRepository;

    @Autowired
    protected AlbumRepository albumRepository;

    @Autowired
    protected ImageRepository imageRepository;

    @Autowired
    protected MedicalRecordRepository medicalRecordRepository;

    @Autowired
    protected DietitianRecordRepository dietitianRecordRepository;

    @Autowired
    protected InquiryRepository inquiryRepository;

    @Autowired
    protected ReplyRepository replyRepository;

    @Autowired
    protected SpecialtyRepository specialtyRepository;

    @Autowired
    protected DiseaseRepository diseaseRepository;

    @Autowired
    protected HealthIndexRepository healthIndexRepository;

    @Autowired
    protected HealthIndexScheduleRepository healthIndexScheduleRepository;

    @Autowired
    protected NotificationRepository notificationRepository;

    protected Logger logger;

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
//        else if (o instanceof ImageAlbum)
//            albumRepository.save((ImageAlbum) o);
        else if (o instanceof Image)
            imageRepository.save((Image) o);
        else if (o instanceof Disease)
            diseaseRepository.save((Disease) o);
        else if (o instanceof MedicalRecord)
            medicalRecordRepository.save((MedicalRecord) o);
        else if (o instanceof DietRecord)
            dietitianRecordRepository.save((DietRecord) o);
        else if (o instanceof Inquiry)
            inquiryRepository.save((Inquiry) o);
        else if (o instanceof Reply)
            replyRepository.save((Reply) o);
        else if (o instanceof HealthIndex)
            healthIndexRepository.save((HealthIndex) o);
        else if (o instanceof HealthIndexSchedule)
            healthIndexScheduleRepository.save((HealthIndexSchedule) o);
        else if (o instanceof Notification)
            notificationRepository.save((Notification) o);
    }

    @ExceptionHandler(value = BaseRuntimeException.class)
    public ResponseEntity<ErrorDto> handleException(BaseRuntimeException e) {
        logger.error(e.getMessage());
        return new ResponseEntity<>(new ErrorDto(e.getMessage()), e.getStatus());
    }
}
