package com.abclinic.server.model.entity.payload.health_index;

import com.abclinic.server.common.base.Views;
import com.abclinic.server.common.utils.DateTimeUtils;
import com.abclinic.server.model.entity.payload.IPayloadIpml;
import com.abclinic.server.model.entity.user.Doctor;
import com.abclinic.server.model.entity.user.Patient;
import com.abclinic.server.model.entity.user.User;
import com.abclinic.server.serializer.AbridgedViewSerializer;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author tmduc
 * @package com.abclinic.server.model.entity
 * @created 12/30/2019 3:13 PM
 */

@Entity
@DynamicUpdate
@Table(name = "health_index_schedule")
public class HealthIndexSchedule extends IPayloadIpml {

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Patient.class)
    @JoinColumn(name = "patient_id")
    @JsonView(Views.Abridged.class)
    @JsonSerialize(using = AbridgedViewSerializer.class)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = User.class)
    @JoinColumn(name = "doctor_id")
    @JsonView(Views.Public.class)
    @JsonSerialize(using = AbridgedViewSerializer.class)
    private User doctor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "index_id")
    @JsonView(Views.Public.class)
    @JsonSerialize(using = AbridgedViewSerializer.class)
    private HealthIndex index;

    @OneToMany(fetch = FetchType.LAZY, targetEntity = PatientHealthIndexField.class, mappedBy = "schedule")
    @JsonView(Views.Private.class)
    @JsonSerialize(using = AbridgedViewSerializer.class)
    private List<PatientHealthIndexField> patientValues;

    @JsonView(Views.Abridged.class)
    private String description;

    @Column(name = "scheduled")
    @JsonView(Views.Abridged.class)
    private long scheduledTime;

    @JsonView(Views.Abridged.class)
    private int status;

    @CreationTimestamp
    @JsonView(Views.Public.class)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @JsonView(Views.Confidential.class)
    private LocalDateTime updatedAt;

    @Column(name = "started_at", columnDefinition = "TIMESTAMP")
    @JsonView(Views.Public.class)
    private LocalDateTime startedAt;

    @Column(name = "ended_at", columnDefinition = "TIMESTAMP")
    @JsonView(Views.Public.class)
    private LocalDateTime endedAt;

    public HealthIndexSchedule() {

    }

    public HealthIndexSchedule(Patient patient, Doctor doctor, HealthIndex index, String description, long scheduledTime, LocalDateTime startedAt) {
        this.patient = patient;
        this.doctor = doctor;
        this.index = index;
        this.description = description;
        this.scheduledTime = scheduledTime;
        this.startedAt = startedAt;
        this.endedAt = DateTimeUtils.plusSeconds(startedAt, scheduledTime - 1);
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public User getDoctor() {
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

    public List<PatientHealthIndexField> getPatientValues() {
        return patientValues;
    }

    public void setPatientValues(List<PatientHealthIndexField> patientValues) {
        this.patientValues = patientValues;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
