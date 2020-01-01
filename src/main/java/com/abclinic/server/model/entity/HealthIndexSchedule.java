package com.abclinic.server.model.entity;

import com.abclinic.server.base.Views;
import com.abclinic.server.model.entity.user.Doctor;
import com.abclinic.server.model.entity.user.User;
import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @author tmduc
 * @package com.abclinic.server.model.entity
 * @created 12/30/2019 3:13 PM
 */

@Entity
@Table(name = "patient_health_index")
public class HealthIndexSchedule<T extends Record> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Public.class)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Record.class)
    @JoinColumn(name = "record_id")
    @JsonView(Views.Public.class)
    private T record;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = User.class)
    @JoinColumn(name = "doctor_id")
    @JsonView(Views.Public.class)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "index_id")
    @JsonView(Views.Public.class)
    private HealthIndex index;

    @CreationTimestamp
    @JsonView(Views.Public.class)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @JsonView(Views.Confidential.class)
    private LocalDateTime updatedAt;

    public HealthIndexSchedule() {

    }

    public HealthIndexSchedule(T record, Doctor doctor, HealthIndex index) {
        this.record = record;
        this.doctor = doctor;
        this.index = index;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
}
