package com.abclinic.server.model.entity.payload.record;

import com.abclinic.server.base.Views;
import com.abclinic.server.constant.RecordType;
import com.abclinic.server.model.entity.payload.HealthIndexSchedule;
import com.abclinic.server.model.entity.payload.Inquiry;
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

    public DietRecord() {

    }

    public DietRecord(Inquiry inquiry, String note, String prescription, Dietitian dietitian) {
        super(inquiry, RecordType.DIET.getValue(), note, prescription);
        this.dietitian = dietitian;
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

}