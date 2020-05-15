package com.abclinic.server.model.entity.user;

import com.abclinic.server.common.base.Views;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;
import java.time.LocalDate;

@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
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
