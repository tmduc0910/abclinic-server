package com.abclinic.server.service;

import com.abclinic.server.constant.Status;
import com.abclinic.server.exception.BadRequestException;
import com.abclinic.server.exception.ForbiddenException;
import com.abclinic.server.model.entity.payload.record.DietRecord;
import com.abclinic.server.model.entity.payload.record.MedicalRecord;
import com.abclinic.server.model.entity.payload.record.Record;
import com.abclinic.server.model.entity.user.Practitioner;
import com.abclinic.server.model.entity.user.User;
import com.abclinic.server.repository.DietitianRecordRepository;
import com.abclinic.server.repository.MedicalRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.naming.directory.NoSuchAttributeException;
import java.util.List;
import java.util.Optional;

/**
 * @author tmduc
 * @package com.abclinic.server.service
 * @created 2/21/2020 3:19 PM
 */
@Service
public class RecordService {
    private MedicalRecordRepository medicalRecordRepository;
    private DietitianRecordRepository dietitianRecordRepository;

    @Autowired
    public RecordService(MedicalRecordRepository medicalRecordRepository, DietitianRecordRepository dietitianRecordRepository) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.dietitianRecordRepository = dietitianRecordRepository;
    }

    public <T extends Record> T getRecord(long recordId) throws NoSuchAttributeException {
        Optional<? extends Record> op = medicalRecordRepository.findById(recordId);
        if (!op.isPresent()) {
            op = dietitianRecordRepository.findById(recordId);
            if (!op.isPresent())
                throw new NoSuchAttributeException();
        }
        return (T) op.get();
    }

    public Optional<List<MedicalRecord>> getMedicalRecordsByUser(User user, Pageable pageable) {
        switch (user.getRole()) {
            case PRACTITIONER:
                return medicalRecordRepository.findByInquiryPatientPractitionerId(user.getId(), pageable);
            case SPECIALIST:
                return medicalRecordRepository.findBySpecialistId(user.getId(), pageable);
            case PATIENT:
                return medicalRecordRepository.findByInquiryPatientIdAndStatus(user.getId(), Status.Payload.PROCESSED, pageable);
            default:
                throw new ForbiddenException(user.getId(), "Bạn không thể truy cập vào kiểu tư vấn này");
        }
    }

    public Optional<List<DietRecord>> getDietitianRecordsByUser(User user, Pageable pageable) {
        switch (user.getRole()) {
            case PRACTITIONER:
                return dietitianRecordRepository.findByInquiryPatientPractitionerId(user.getId(), pageable);
            case DIETITIAN:
                return dietitianRecordRepository.findByDietitianId(user.getId(), pageable);
            case PATIENT:
                return dietitianRecordRepository.findByInquiryPatientIdAndStatus(user.getId(), Status.Payload.PROCESSED, pageable);
            default:
                throw new ForbiddenException(user.getId(), "Bạn không thể truy cập vào kiểu tư vấn này");
        }
    }
}
