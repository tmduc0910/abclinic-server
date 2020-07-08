package com.abclinic.server.service.entity;

import com.abclinic.server.common.constant.Constant;
import com.abclinic.server.common.constant.FilterConstant;
import com.abclinic.server.common.constant.PayloadStatus;
import com.abclinic.server.common.constant.RecordType;
import com.abclinic.server.common.criteria.DietRecordPredicateBuilder;
import com.abclinic.server.common.criteria.EntityPredicateBuilder;
import com.abclinic.server.common.criteria.MedicalRecordPredicateBuilder;
import com.abclinic.server.exception.BadRequestException;
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
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.StringJoiner;

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
    public Page<MedicalRecord> getMedicalRecordsByUser(User user, long inquiryId, Pageable pageable) {
        Optional<Page<MedicalRecord>> op;
        switch (user.getRole()) {
            case PRACTITIONER:
                op = medicalRecordRepository.findByInquiryPatientPractitionerIdAndInquiryId(user.getId(), inquiryId, pageable);
                break;
            case SPECIALIST:
                Specialist sp = (Specialist) doctorService.getById(user.getId());
                op = medicalRecordRepository.findByInquiryPatientInAndInquiryId(sp.getPatients(), inquiryId, pageable);
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
    public Page getList(User user, String search, Pageable pageable) {
        if (search.contains(FilterConstant.TYPE.getValue() + Constant.EQUAL_SBL + RecordType.MEDICAL.getValue())) {
            search = search.replace("id", FilterConstant.INQUIRY_ID.getValue());
            return getMedicalRecordsByUser(user, (MedicalRecordPredicateBuilder) new MedicalRecordPredicateBuilder().init(search), pageable);
        }
        else if (search.contains(FilterConstant.TYPE.getValue() +
                Constant.EQUAL_SBL +
                RecordType.DIET.getValue())) {
            search = search.replace("id", FilterConstant.INQUIRY_ID.getValue());
            return getDietRecordsByUser(user, (DietRecordPredicateBuilder) new DietRecordPredicateBuilder().init(search), pageable);
        }
        else throw new BadRequestException(user.getId(), "Kiểu chỉ có thể là Khám bệnh hoặc Dinh dưỡng");
    }

    private Page<MedicalRecord> getMedicalRecordsByUser(User user, MedicalRecordPredicateBuilder builder, Pageable pageable) {
        switch (user.getRole()) {
            case PATIENT:
                builder.with(FilterConstant.MED_INQUIRY_PAT.getValue(), Constant.EQUAL_SBL, user.getId())
                        .with(FilterConstant.STATUS.getValue(), Constant.EQUAL_SBL, PayloadStatus.PROCESSED);
                break;
            case PRACTITIONER:
                builder.with(FilterConstant.INQUIRY_PAT_PRAC_ID.getValue(), Constant.EQUAL_SBL, user.getId());
                break;
            case SPECIALIST:
                builder.with(FilterConstant.MED_INQUIRY_PAT.getValue(), Constant.CONTAIN_SBL, user);
                break;
        }
        BooleanExpression expression = builder.build();
        if (expression != null)
            return medicalRecordRepository.findAll(expression, pageable);
        return medicalRecordRepository.findAll(pageable);
    }

    private Page<DietRecord> getDietRecordsByUser(User user, DietRecordPredicateBuilder builder, Pageable pageable) {
        switch (user.getRole()) {
            case PATIENT:
                builder.with(FilterConstant.DIET_INQUIRY_PAT.getValue(), Constant.EQUAL_SBL, user.getId())
                        .with(FilterConstant.STATUS.getValue(), Constant.EQUAL_SBL, PayloadStatus.PROCESSED);
                break;
            case PRACTITIONER:
                builder.with(FilterConstant.INQUIRY_PAT_PRAC_ID.getValue(), Constant.EQUAL_SBL, user.getId());
                break;
            case DIETITIAN:
                builder.with(FilterConstant.DIET_INQUIRY_PAT.getValue(), Constant.CONTAIN_SBL, user);
                break;
        }
        BooleanExpression expression = builder.build();
        if (expression != null)
            return dietitianRecordRepository.findAll(expression, pageable);
        return dietitianRecordRepository.findAll(pageable);
    }

    @Transactional
    public Page<DietRecord> getDietitianRecordsByUser(User user, long inquiryId, Pageable pageable) {
        Optional<Page<DietRecord>> op;
        switch (user.getRole()) {
            case PRACTITIONER:
                op = dietitianRecordRepository.findByInquiryPatientPractitionerIdAndInquiryId(user.getId(), inquiryId, pageable);
                break;
            case DIETITIAN:
                Dietitian di = (Dietitian) doctorService.getById(user.getId());
                op = dietitianRecordRepository.findByInquiryPatientInAndInquiryId(di.getPatients(), inquiryId, pageable);
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
