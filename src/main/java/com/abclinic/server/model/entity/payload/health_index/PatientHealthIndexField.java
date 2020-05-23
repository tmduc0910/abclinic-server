package com.abclinic.server.model.entity.payload.health_index;

import com.abclinic.server.common.base.Views;
import com.abclinic.server.model.entity.payload.IPayloadIpml;
import com.abclinic.server.serializer.ViewSerializer;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @author tmduc
 * @package com.abclinic.server.model.entity.payload.health_index
 * @created 5/6/2020 2:15 PM
 */
@Entity
@Table(name = "patient_health_index_field")
public class PatientHealthIndexField extends IPayloadIpml {
    @JsonView(Views.Abridged.class)
    @Column(name = "tag_id")
    private long tagId;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = HealthIndexSchedule.class)
    @JoinColumn(name = "schedule_id")
    @JsonView(Views.Abridged.class)
    @JsonSerialize(using = ViewSerializer.class)
    private HealthIndexSchedule schedule;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = HealthIndexField.class)
    @JoinColumn(name = "field_id")
    @JsonView(Views.Public.class)
    @JsonSerialize(using = ViewSerializer.class)
    private HealthIndexField field;

    @JsonView(Views.Abridged.class)
    private String value;

    @CreationTimestamp
    @JsonView(Views.Abridged.class)
    private LocalDateTime createdAt;

    public PatientHealthIndexField() {
    }

    public PatientHealthIndexField(HealthIndexSchedule schedule, HealthIndexField field, String value) {
        this.schedule = schedule;
        this.field = field;
        this.value = value;
    }

    public long getTagId() {
        return tagId;
    }

    public void setTagId(long tagId) {
        this.tagId = tagId;
    }

    public HealthIndexSchedule getSchedule() {
        return schedule;
    }

    public void setSchedule(HealthIndexSchedule schedule) {
        this.schedule = schedule;
    }

    public HealthIndexField getField() {
        return field;
    }

    public void setField(HealthIndexField field) {
        this.field = field;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
