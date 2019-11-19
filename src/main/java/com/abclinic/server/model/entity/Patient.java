package com.abclinic.server.model.entity;

import com.abclinic.server.constant.Role;
import com.abclinic.server.constant.RoleValue;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Where;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Where(clause = "role = " + RoleValue.PATIENT)
@DiscriminatorValue("" + RoleValue.PATIENT)
public class Patient extends User {
    public Patient() {
    }

    public Patient(String name, String email, int gender, Date dateOfBirth, String password, String phoneNumber) {
        super(name, email, gender, dateOfBirth, password, phoneNumber);
    }
}
