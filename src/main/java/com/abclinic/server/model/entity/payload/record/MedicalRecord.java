package com.abclinic.server.model.entity.payload.record;

import com.abclinic.server.common.base.Views;
import com.abclinic.server.common.constant.RecordType;
import com.abclinic.server.model.entity.Disease;
import com.abclinic.server.model.entity.payload.HealthIndexSchedule;
import com.abclinic.server.model.entity.payload.Inquiry;
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

    public String getDiagnose() {
        return diagnose;
    }

    public void setDiagnose(String diagnose) {
        this.diagnose = diagnose;
    }
}
