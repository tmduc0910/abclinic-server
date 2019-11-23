package com.abclinic.server.model.entity.user;

import com.abclinic.server.base.Views;
import com.abclinic.server.constant.RoleValue;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "patient")
public class Patient extends User {
    @JsonView(Views.Private.class)
    private String address;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dietitian_id")
    @JsonView(Views.Private.class)
    private Dietitian dietitian;
    @JsonView(Views.Private.class)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "practitioner_id")
    private Practitioner practitioner;

    public Patient() {
    }

    public Patient(String name, String email, int gender, LocalDate dateOfBirth, String password, String phoneNumber) {
        super(RoleValue.PATIENT, name, email, gender, dateOfBirth, password, phoneNumber);
    }

    public Patient(String name, String email, int gender, LocalDate dateOfBirth, String password, String phoneNumber, String address) {
        super(RoleValue.PATIENT, name, email, gender, dateOfBirth, password, phoneNumber);
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Dietitian getDietitian() {
        return dietitian;
    }

    public void setDietitian(Dietitian dietitian) {
        this.dietitian = dietitian;
    }

    public Practitioner getPractitioner() {
        return practitioner;
    }

    public void setPractitioner(Practitioner practitioner) {
        this.practitioner = practitioner;
    }
}
