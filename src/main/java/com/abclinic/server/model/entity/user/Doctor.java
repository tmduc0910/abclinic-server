package com.abclinic.server.model.entity.user;

import com.abclinic.server.base.Views;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.MappedSuperclass;
import java.time.LocalDate;

@MappedSuperclass
public abstract class Doctor extends User {
    @JsonView(Views.Public.class)
    private String description;

    public Doctor() {
    }

    public Doctor(int role, String name, String email, int gender, LocalDate dateOfBirth, String password, String phoneNumber) {
        super(role, name, email, gender, dateOfBirth, password, phoneNumber);
    }

    public Doctor(int role, String name, String email, int gender, LocalDate dateOfBirth, String password, String phoneNumber, String description) {
        super(role, name, email, gender, dateOfBirth, password, phoneNumber);
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
