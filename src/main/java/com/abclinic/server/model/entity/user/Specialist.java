package com.abclinic.server.model.entity.user;

import com.abclinic.server.base.Views;
import com.abclinic.server.constant.RoleValue;
import com.abclinic.server.model.entity.Specialty;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "specialist")
public class Specialist extends Doctor {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "practitioner_id")
    @JsonView(Views.Private.class)
    private Practitioner practitioner;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialty_id")
    @JsonView(Views.Public.class)
    private Specialty specialty;

    public Specialist() {
    }

    public Specialist(String name, String email, int gender, Date dateOfBirth, String password, String phoneNumber) {
        super(RoleValue.SPECIALIST, name, email, gender, dateOfBirth, password, phoneNumber);
    }

    public Specialist(String name, String email, int gender, Date dateOfBirth, String password, String phoneNumber, String description, int experience) {
        super(RoleValue.SPECIALIST, name, email, gender, dateOfBirth, password, phoneNumber, description, experience);
    }

    public Specialist(String name, String email, int gender, Date dateOfBirth, String password, String phoneNumber, String description, int experience, Practitioner practitioner, Specialty specialty) {
        super(RoleValue.SPECIALIST, name, email, gender, dateOfBirth, password, phoneNumber, description, experience);
        this.practitioner = practitioner;
        this.specialty = specialty;
    }

    public Practitioner getPractitioner() {
        return practitioner;
    }

    public void setPractitioner(Practitioner practitioner) {
        this.practitioner = practitioner;
    }

    public Specialty getSpecialty() {
        return specialty;
    }

    public void setSpecialty(Specialty specialty) {
        this.specialty = specialty;
    }
}
