package com.abclinic.server.model.entity.user;

import com.abclinic.server.base.Views;
import com.abclinic.server.constant.RoleValue;
import com.abclinic.server.model.entity.Specialty;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "dietitian")
public class Dietitian extends Doctor {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialty_id")
    @JsonView(Views.Public.class)
    private Specialty specialty;

    public Dietitian() {
    }

    public Dietitian(String name, String email, int gender, LocalDate dateOfBirth, String password, String phoneNumber) {
        super(RoleValue.DIETITIAN, name, email, gender, dateOfBirth, password, phoneNumber);
    }

    public Dietitian(String name, String email, int gender, LocalDate dateOfBirth, String password, String phoneNumber, String description, int experience) {
        super(RoleValue.DIETITIAN, name, email, gender, dateOfBirth, password, phoneNumber, description, experience);
    }

    public Specialty getSpecialty() {
        return specialty;
    }

    public void setSpecialty(Specialty specialty) {
        this.specialty = specialty;
    }
}
