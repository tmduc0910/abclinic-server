package com.abclinic.server.model.entity.payload.record;

import com.abclinic.server.base.Views;
import com.abclinic.server.constant.RecordType;
import com.abclinic.server.model.entity.payload.HealthIndexSchedule;
import com.abclinic.server.model.entity.user.Dietitian;
import com.abclinic.server.model.entity.user.Patient;
import com.abclinic.server.model.entity.user.Practitioner;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import java.util.List;

/**
 * @author tmduc
 * @package com.abclinic.server.model.entity
 * @created 12/30/2019 3:03 PM
 */

@Entity
@Table(name = "dietitian_record")
public class DietRecord extends Record {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dietitian_id")
    @JsonView(Views.Abridged.class)
    private Dietitian dietitian;

    @OneToMany(fetch = FetchType.LAZY, targetEntity = HealthIndexSchedule.class, mappedBy = "record")
    @JsonView(Views.Public.class)
    private List<HealthIndexSchedule<DietRecord>> schedules;

    @JsonView(Views.Abridged.class)
    private String note;

    @JsonView(Views.Abridged.class)
    private String prescription;

    public DietRecord() {

    }

    public DietRecord(Patient patient, Practitioner practitioner) {
        super(patient, practitioner, RecordType.DIET.getValue());
    }

    public Dietitian getDietitian() {
        return dietitian;
    }

    public void setDietitian(Dietitian dietitian) {
        this.dietitian = dietitian;
    }

    public List<HealthIndexSchedule<DietRecord>> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<HealthIndexSchedule<DietRecord>> schedules) {
        this.schedules = schedules;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPrescription() {
        return prescription;
    }

    public void setPrescription(String prescription) {
        this.prescription = prescription;
    }
}
