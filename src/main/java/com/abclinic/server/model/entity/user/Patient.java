package com.abclinic.server.model.entity.user;

import com.abclinic.server.common.base.Views;
import com.abclinic.server.common.constant.RoleValue;
import com.abclinic.server.model.entity.payload.Inquiry;
import com.abclinic.server.serializer.ViewSerializer;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.annotations.WhereJoinTable;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "patient")
public class Patient extends User {
    @JsonView(Views.Private.class)
    private String address;

    @OneToMany(fetch = FetchType.LAZY, targetEntity = Inquiry.class, mappedBy = "patient")
    @JsonView(Views.Confidential.class)
    @JsonSerialize(using = ViewSerializer.class)
    private List<Inquiry> inquiries;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "practitioner_id")
    @JsonView(Views.Public.class)
    @JsonSerialize(using = ViewSerializer.class)
    private Practitioner practitioner;

    @ManyToMany(fetch = FetchType.LAZY)
    @WhereJoinTable(clause = "type = 0")
    @JoinTable(
            name = "doctor_patient",
            joinColumns = @JoinColumn(name = "patient_id"),
            inverseJoinColumns = @JoinColumn(name = "doctor_id")
    )
    @JsonView(Views.Confidential.class)
    @JsonSerialize(using = ViewSerializer.class)
    private List<Specialist> specialists;

    @ManyToMany(fetch = FetchType.LAZY)
    @WhereJoinTable(clause = "type = 1")
    @JoinTable(
            name = "doctor_patient",
            joinColumns = @JoinColumn(name = "patient_id"),
            inverseJoinColumns = @JoinColumn(name = "doctor_id")
    )
    @JsonView(Views.Confidential.class)
    @JsonSerialize(using = ViewSerializer.class)
    private List<Dietitian> dietitians;

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

    public List<Inquiry> getInquiries() {
        return inquiries;
    }

    public void setInquiries(List<Inquiry> inquiries) {
        this.inquiries = inquiries;
    }

    public Practitioner getPractitioner() {
        return practitioner;
    }

    public void setPractitioner(Practitioner practitioner) {
        this.practitioner = practitioner;
    }

    public List<Specialist> getSpecialists() {
        return specialists;
    }

    public void setSpecialists(List<Specialist> specialists) {
        this.specialists = specialists;
    }

    public void addSpecialist(Specialist specialist) {
        this.specialists.add(specialist);
    }

    public void removeSpecialist(Specialist specialist) {
        this.specialists.remove(specialist);
    }

    public List<Dietitian> getDietitians() {
        return dietitians;
    }

    public void setDietitians(List<Dietitian> dietitians) {
        this.dietitians = dietitians;
    }

    public void addDietitian(Dietitian dietitian) {
        this.dietitians.add(dietitian);
    }

    public void removeDietitian(Dietitian dietitian) {
        this.dietitians.remove(dietitian);
    }
}
