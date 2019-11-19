package com.abclinic.server.model.entity;

import com.abclinic.server.constant.Role;
import com.abclinic.server.constant.RoleValue;
import org.hibernate.annotations.Where;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Where(clause = ("role = " + RoleValue.SPECIALIST))
@DiscriminatorValue("" + RoleValue.SPECIALIST)
public class Specialist extends User {
    public Specialist() {
    }

    public Specialist(String name, String email, int gender, Date dateOfBirth, String password, String phoneNumber) {
        super(name, email, gender, dateOfBirth, password, phoneNumber);
    }
}
