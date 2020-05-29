package com.abclinic.server.model.entity.payload.record;

import com.abclinic.server.common.base.Views;
import com.abclinic.server.common.constant.RecordType;
import com.abclinic.server.model.entity.Disease;
import com.abclinic.server.model.entity.payload.Inquiry;
import com.abclinic.server.model.entity.user.User;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;

@Entity
@Table(name = "medical_record")
public class MedicalRecord extends Record {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "disease_id")
    @JsonView(Views.Abridged.class)
    private Disease disease;

    @JsonView(Views.Abridged.class)
    private String diagnose;

    public MedicalRecord() {

    }

    public MedicalRecord(Inquiry inquiry, User doctor, String note, String prescription, String diagnose) {
        super(inquiry, doctor, RecordType.MEDICAL.getValue(), note, prescription);
        this.diagnose = diagnose;
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
