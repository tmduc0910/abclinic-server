package com.abclinic.server.model.dto;

import com.abclinic.server.model.entity.payload.health_index.HealthIndexSchedule;
import com.abclinic.server.model.entity.payload.health_index.PatientHealthIndexField;
import com.abclinic.server.model.entity.user.Patient;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author tmduc
 * @package com.abclinic.server.model.dto
 * @created 5/10/2020 3:06 PM
 */
public class IndexResultResponseDto {
    private final HealthIndexSchedule schedule;
    private final List<PatientHealthIndexField> fields;
    private final LocalDateTime createdAt;

    public IndexResultResponseDto(HealthIndexSchedule schedule, List<PatientHealthIndexField> fields, LocalDateTime createdAt) {
        this.schedule = schedule;
        this.fields = fields;
        this.createdAt = createdAt;
    }

    public HealthIndexSchedule getSchedule() {
        return schedule;
    }

    public List<PatientHealthIndexField> getFields() {
        return fields;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
