package com.abclinic.server.model.factory;

import com.abclinic.server.constant.RecordType;
import com.abclinic.server.model.dto.RecordDto;
import com.abclinic.server.model.entity.record.Record;
import com.abclinic.server.repository.DietitianRecordRepository;
import com.abclinic.server.repository.MedicalRecordRepository;
import com.abclinic.server.repository.RecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author tmduc
 * @package com.abclinic.server.model.factory
 * @created 1/9/2020 3:39 PM
 */
@Component
public class RecordFactory {
    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    @Autowired
    private DietitianRecordRepository dietitianRecordRepository;

    @Autowired
    private RecordRepository recordRepository;

    public RecordDto<? extends Record> getRecord(int recordId) {
        Record record = recordRepository.findById(recordId);
        if (record.getRecordType() == RecordType.MEDICAL.getValue())
            return new RecordDto<>(record.getRecordType(), medicalRecordRepository.findById(recordId).get());
        else if (record.getRecordType() == RecordType.DIET.getValue())
            return new RecordDto<>(record.getRecordType(), dietitianRecordRepository.findById(recordId).get());
        return null;
    }
}
