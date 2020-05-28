package com.abclinic.server.model.dto;

import com.abclinic.server.common.base.Views;
import com.abclinic.server.model.entity.payload.health_index.HealthIndexField;
import com.abclinic.server.model.entity.payload.health_index.HealthIndexSchedule;
import com.abclinic.server.model.entity.payload.health_index.PatientHealthIndexField;
import com.abclinic.server.serializer.ViewSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.LocalDateTime;

/**
 * @author tmduc
 * @package com.abclinic.server.model.dto
 * @created 5/28/2020 9:29 AM
 */
public class ResultDto {
    private long id;
    private long tagId;
    private String value;
    @JsonIgnore
    private HealthIndexSchedule schedule;

    @JsonView(Views.Abridged.class)
    @JsonSerialize(using = ViewSerializer.class)
    private HealthIndexField field;
    private LocalDateTime createdAt;

    public ResultDto(PatientHealthIndexField field) {
        this.id = field.getId();
        this.tagId = field.getTagId();
        this.value = field.getValue();
        this.field = field.getField();
        this.createdAt = field.getCreatedAt();
        this.schedule = field.getSchedule();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTagId() {
        return tagId;
    }

    public void setTagId(long tagId) {
        this.tagId = tagId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public HealthIndexField getField() {
        return field;
    }

    public void setField(HealthIndexField field) {
        this.field = field;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public HealthIndexSchedule getSchedule() {
        return schedule;
    }

    public void setSchedule(HealthIndexSchedule schedule) {
        this.schedule = schedule;
    }
}
