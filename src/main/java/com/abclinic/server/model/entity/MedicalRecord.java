package com.abclinic.server.model.entity;

import com.abclinic.server.base.Views;
import com.abclinic.server.constant.RecordType;
import com.abclinic.server.model.entity.user.Patient;
import com.abclinic.server.model.entity.user.Practitioner;
import com.abclinic.server.model.entity.user.Specialist;
import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "medical_record")
public class MedicalRecord extends Record {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "practitioner_id")
    @JsonView(Views.Public.class)
    private Practitioner practitioner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialist_id")
    @JsonView(Views.Private.class)
    private Specialist specialist;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "disease_id")
    @JsonView(Views.Public.class)
    private Disease disease;

    @OneToMany(fetch = FetchType.LAZY, targetEntity = HealthIndexSchedule.class, mappedBy = "record")
    @JsonView(Views.Public.class)
    private List<HealthIndexSchedule<MedicalRecord>> schedules;

    @JsonView(Views.Public.class)
    private String diagnose;

    @JsonView(Views.Public.class)
    private String prescription;

    @JsonView(Views.Public.class)
    private String note;

    public MedicalRecord() {

    }

    public MedicalRecord(Patient patient, Practitioner practitioner, Specialist specialist, Disease disease) {
        super(patient, RecordType.MEDICAL.getValue());
        this.practitioner = practitioner;
        this.specialist = specialist;
        this.disease = disease;
    }

    public Practitioner getPractitioner() {
        return practitioner;
    }

    public void setPractitioner(Practitioner practitioner) {
        this.practitioner = practitioner;
    }

    public Specialist getSpecialist() {
        return specialist;
    }

    public void setSpecialist(Specialist specialist) {
        this.specialist = specialist;
    }

    public Disease getDisease() {
        return disease;
    }

    public void setDisease(Disease disease) {
        this.disease = disease;
    }

    public List<HealthIndexSchedule<MedicalRecord>> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<HealthIndexSchedule<MedicalRecord>> schedules) {
        this.schedules = schedules;
    }

    public String getDiagnose() {
        return diagnose;
    }

    public void setDiagnose(String diagnose) {
        this.diagnose = diagnose;
    }

    public String getPrescription() {
        return prescription;
    }

    public void setPrescription(String prescription) {
        this.prescription = prescription;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
