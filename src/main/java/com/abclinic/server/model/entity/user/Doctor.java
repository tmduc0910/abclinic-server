package com.abclinic.server.model.entity.user;

import javax.persistence.MappedSuperclass;
import java.util.Date;

@MappedSuperclass
public abstract class Doctor extends User {
    private String description;
    private int experience;

    public Doctor() {
    }

    public Doctor(int role, String name, String email, int gender, Date dateOfBirth, String password, String phoneNumber) {
        super(role, name, email, gender, dateOfBirth, password, phoneNumber);
    }

    public Doctor(int role, String name, String email, int gender, Date dateOfBirth, String password, String phoneNumber, String description, int experience) {
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
