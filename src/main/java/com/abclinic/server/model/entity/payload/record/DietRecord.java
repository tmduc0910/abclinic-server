package com.abclinic.server.model.entity.payload.record;

import com.abclinic.server.common.constant.RecordType;
import com.abclinic.server.model.entity.payload.Inquiry;
import com.abclinic.server.model.entity.user.User;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author tmduc
 * @package com.abclinic.server.model.entity
 * @created 12/30/2019 3:03 PM
 */

@Entity
@Table(name = "dietitian_record")
public class DietRecord extends Record {

    public DietRecord() {

    }

    public DietRecord(Inquiry inquiry, User doctor, String note, String prescription) {
        super(inquiry, doctor, RecordType.DIET.getValue(), note, prescription);
    }
}
