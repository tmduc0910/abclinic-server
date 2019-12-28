package com.abclinic.server.model.entity.user;

import com.abclinic.server.base.Views;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.MappedSuperclass;
import java.time.LocalDate;

@MappedSuperclass
public abstract class Doctor extends User {
    @JsonView(Views.Public.class)
    private String description;

    @JsonView(Views.Public.class)
    private int experience;

    public Doctor() {
    }

    public Doctor(int role, String name, String email, int gender, LocalDate dateOfBirth, String password, String phoneNumber) {
        super(role, name, email, gender, dateOfBirth, password, phoneNumber);
    }

    public Doctor(int role, String name, String email, int gender, LocalDate dateOfBirth, String password, String phoneNumber, String description, int experience) {
        super(role, name, email, gender, dateOfBirth, password, phoneNumber);
        this.description = description;
        this.experience = experience;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }
}
