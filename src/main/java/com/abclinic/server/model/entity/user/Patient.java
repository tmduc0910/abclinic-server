package com.abclinic.server.model.entity.user;

import com.abclinic.server.base.Views;
import com.abclinic.server.constant.RoleValue;
import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.annotations.Where;
import org.hibernate.annotations.WhereJoinTable;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "patient")
public class Patient extends User {
    @JsonView(Views.Private.class)
    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "practitioner_id")
    @JsonView(Views.Private.class)
    private Practitioner practitioner;

//    @ManyToMany(fetch = FetchType.LAZY)
//    @WhereJoinTable(clause = "type = 0")
//    @JoinTable(
//            name = "doctor_patient",
//            joinColumns = @JoinColumn(name = "patient_id"),
//            inverseJoinColumns = @JoinColumn(name = "doctor_id")
//    )
//    @JsonView(Views.Private.class)
//    private List<Specialist> specialists;
//
//    @ManyToMany(fetch = FetchType.LAZY)
//    @WhereJoinTable(clause = "type = 1")
//    @JoinTable(
//            name = "doctor_patient",
//            joinColumns = @JoinColumn(name = "patient_id"),
//            inverseJoinColumns = @JoinColumn(name = "doctor_id")
//    )
//    @JsonView(Views.Private.class)
//    private List<Dietitian> dietitians;

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

    public Practitioner getPractitioner() {
        return practitioner;
    }

    public void setPractitioner(Practitioner practitioner) {
        this.practitioner = practitioner;
    }
//
//    public List<Specialist> getSpecialists() {
//        return specialists;
//    }
//
//    public void setSpecialists(List<Specialist> specialists) {
//        this.specialists = specialists;
//    }
//
//    public List<Dietitian> getDietitians() {
//        return dietitians;
//    }
//
//    public void setDietitians(List<Dietitian> dietitians) {
//        this.dietitians = dietitians;
//    }
}
