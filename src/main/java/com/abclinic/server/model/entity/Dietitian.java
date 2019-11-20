package com.abclinic.server.model.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "dietitian")
public class Dietitian extends Doctor {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialty_id")
    private Specialty specialty;

    public Dietitian() {
    }

    public Dietitian(String name, String email, int gender, Date dateOfBirth, String password, String phoneNumber) {
        super(name, email, gender, dateOfBirth, password, phoneNumber);
    }

    public Dietitian(String name, String email, int gender, Date dateOfBirth, String password, String phoneNumber, String description, int experience) {
        super(name, email, gender, dateOfBirth, password, phoneNumber, description, experience);
    }

    public Specialty getSpecialty() {
        return specialty;
    }

    public void setSpecialty(Specialty specialty) {
        this.specialty = specialty;
    }
}
