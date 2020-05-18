package com.abclinic.server.model.entity.payload.record;

import com.abclinic.server.common.base.Views;
import com.abclinic.server.common.constant.RecordType;
import com.abclinic.server.model.entity.payload.Inquiry;
import com.abclinic.server.model.entity.user.Dietitian;
import com.abclinic.server.serializer.ViewSerializer;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.persistence.*;

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
    @JsonSerialize(using = ViewSerializer.class)
    private Dietitian dietitian;

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
}
