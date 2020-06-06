package com.abclinic.server.service.entity;

import com.abclinic.server.common.constant.PayloadStatus;
import com.abclinic.server.common.constant.RecordType;
import com.abclinic.server.exception.ForbiddenException;
import com.abclinic.server.exception.NotFoundException;
import com.abclinic.server.model.entity.payload.record.DietRecord;
import com.abclinic.server.model.entity.payload.record.MedicalRecord;
import com.abclinic.server.model.entity.payload.record.Record;
import com.abclinic.server.model.entity.user.Dietitian;
import com.abclinic.server.model.entity.user.Specialist;
import com.abclinic.server.model.entity.user.User;
import com.abclinic.server.repository.DietitianRecordRepository;
import com.abclinic.server.repository.MedicalRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

/**
 * @author tmduc
 * @package com.abclinic.server.service
 * @created 2/21/2020 3:19 PM
 */
@Service
public class RecordService implements IDataMapperService<Record> {
    private MedicalRecordRepository medicalRecordRepository;
    private DietitianRecordRepository dietitianRecordRepository;
    private DoctorService doctorService;

    @Autowired
    public RecordService(MedicalRecordRepository medicalRecordRepository, DietitianRecordRepository dietitianRecordRepository, DoctorService doctorService) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.dietitianRecordRepository = dietitianRecordRepository;
        this.doctorService = doctorService;
    }

    @Override
    public Record getById(long id) throws NotFoundException {
        throw new UnsupportedOperationException();
    }

    @Transactional
    public Record getByTypeAndId(int type, long id) throws NotFoundException {
        switch (RecordType.getType(type)) {
            case MEDICAL:
                return medicalRecordRepository.findById(id)
                        .orElseThrow(NotFoundException::new);
            case DIET:
                return dietitianRecordRepository.findById(id)
                        .orElseThrow(NotFoundException::new);
        }
        throw new NotFoundException();
    }

    @Transactional
    public Page<MedicalRecord> getMedicalRecordsByUser(User user, Pageable pageable) {
        Optional<Page<MedicalRecord>> op;
        switch (user.getRole()) {
            case PRACTITIONER:
                op = medicalRecordRepository.findByInquiryPatientPractitionerId(user.getId(), pageable);
                break;
            case SPECIALIST:
                Specialist sp = (Specialist) doctorService.getById(user.getId());
                op = medicalRecordRepository.findByInquiryPatientIn(sp.getPatients(), pageable);
                break;
            case PATIENT:
                op = medicalRecordRepository.findByInquiryPatientIdAndStatus(user.getId(), PayloadStatus.PROCESSED, pageable);
                break;
            default:
                throw new ForbiddenException(user.getId(), "Bạn không thể truy cập vào kiểu tư vấn này");
        }
        return op.orElseThrow(NotFoundException::new);
    }

    @Transactional
    public Page<DietRecord> getDietitianRecordsByUser(User user, Pageable pageable) {
        Optional<Page<DietRecord>> op;
        switch (user.getRole()) {
            case PRACTITIONER:
                op = dietitianRecordRepository.findByInquiryPatientPractitionerId(user.getId(), pageable);
                break;
            case DIETITIAN:
                Dietitian di = (Dietitian) doctorService.getById(user.getId());
                op = dietitianRecordRepository.findByInquiryPatientIn(di.getPatients(), pageable);
                break;
            case PATIENT:
                op = dietitianRecordRepository.findByInquiryPatientIdAndStatus(user.getId(), PayloadStatus.PROCESSED, pageable);
                break;
            default:
                throw new ForbiddenException(user.getId(), "Bạn không thể truy cập vào kiểu tư vấn này");
        }
        return op.orElseThrow(NotFoundException::new);
    }

    @Override
    public Record save(Record obj) {
        if (obj instanceof MedicalRecord)
            return medicalRecordRepository.save((MedicalRecord) obj);
        else
            return dietitianRecordRepository.save((DietRecord) obj);
    }
}
