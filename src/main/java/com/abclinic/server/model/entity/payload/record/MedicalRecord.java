package com.abclinic.server.model.entity.payload.record;

import com.abclinic.server.base.Views;
import com.abclinic.server.constant.RecordType;
import com.abclinic.server.model.entity.Disease;
import com.abclinic.server.model.entity.payload.HealthIndexSchedule;
import com.abclinic.server.model.entity.payload.Inquiry;
import com.abclinic.server.model.entity.user.Patient;
import com.abclinic.server.model.entity.user.Practitioner;
import com.abclinic.server.model.entity.user.Specialist;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "medical_record")
public class MedicalRecord extends Record {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "specialist_id")
    @JsonView(Views.Public.class)
    private Specialist specialist;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "disease_id")
    @JsonView(Views.Abridged.class)
    private Disease disease;

    @OneToMany(fetch = FetchType.LAZY, targetEntity = HealthIndexSchedule.class, mappedBy = "record")
    @JsonView(Views.Public.class)
    private List<HealthIndexSchedule<MedicalRecord>> schedules;

    @JsonView(Views.Abridged.class)
    private String diagnose;

    public MedicalRecord() {

    }

    public MedicalRecord(Inquiry inquiry, String note, String prescription, Specialist specialist, String diagnose) {
        super(inquiry, RecordType.MEDICAL.getValue(), note, prescription);
        this.specialist = specialist;
        this.diagnose = diagnose;
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
}