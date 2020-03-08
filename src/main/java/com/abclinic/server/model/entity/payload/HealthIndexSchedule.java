package com.abclinic.server.model.entity.payload;

import com.abclinic.server.base.Views;
import com.abclinic.server.model.entity.payload.HealthIndex;
import com.abclinic.server.model.entity.payload.record.Record;
import com.abclinic.server.model.entity.user.Doctor;
import com.abclinic.server.model.entity.user.User;
import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * @author tmduc
 * @package com.abclinic.server.model.entity
 * @created 12/30/2019 3:13 PM
 */

@Entity
@Table(name = "health_index_schedule")
public class HealthIndexSchedule<T extends Record> extends Payload {

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Record.class)
    @JoinColumn(name = "record_id")
    @JsonView(Views.Abridged.class)
    private T record;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = User.class)
    @JoinColumn(name = "doctor_id")
    @JsonView(Views.Abridged.class)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "index_id")
    @JsonView(Views.Abridged.class)
    private HealthIndex index;

    @Column(name = "scheduled")
    @JsonView(Views.Abridged.class)
    private long scheduledTime;

    @JsonView(Views.Private.class)
    private int status;

    @CreationTimestamp
    @JsonView(Views.Public.class)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @JsonView(Views.Confidential.class)
    private LocalDateTime updatedAt;

    @JsonView(Views.Public.class)
    private LocalDateTime startedAt;

    @JsonView(Views.Public.class)
    private LocalDateTime endedAt;

    public HealthIndexSchedule() {

    }

    public HealthIndexSchedule(T record, Doctor doctor, HealthIndex index) {
        this.record = record;
        this.doctor = doctor;
        this.index = index;
    }

    public T getRecord() {
        return record;
    }

    public void setRecord(T record) {
        this.record = record;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public HealthIndex getIndex() {
        return index;
    }

    public void setIndex(HealthIndex index) {
        this.index = index;
    }

    public long getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(long scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(LocalDateTime endedAt) {
        this.endedAt = endedAt;
    }
}
