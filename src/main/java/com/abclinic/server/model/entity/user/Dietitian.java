package com.abclinic.server.model.entity.user;

import com.abclinic.server.common.base.Views;
import com.abclinic.server.common.constant.RoleValue;
import com.abclinic.server.model.entity.Specialty;
import com.abclinic.server.serializer.ViewSerializer;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "dietitian")
public class Dietitian extends Doctor {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialty_id")
    @JsonView(Views.Public.class)
    @JsonSerialize(using = ViewSerializer.class)
    private Specialty specialty;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "doctor_patient",
            joinColumns = @JoinColumn(name = "doctor_id"),
            inverseJoinColumns = @JoinColumn(name = "patient_id")
    )
    @JsonView(Views.Private.class)
    @JsonSerialize(using = ViewSerializer.class)
    private List<Patient> patients;

    public Dietitian() {
    }

    public Dietitian(String name, String email, int gender, LocalDate dateOfBirth, String password, String phoneNumber) {
        super(RoleValue.DIETITIAN, name, email, gender, dateOfBirth, password, phoneNumber);
    }

    public Dietitian(String name, String email, int gender, LocalDate dateOfBirth, String password, String phoneNumber, String description) {
        super(RoleValue.DIETITIAN, name, email, gender, dateOfBirth, password, phoneNumber, description);
    }

    public Specialty getSpecialty() {
        return specialty;
    }

    public void setSpecialty(Specialty specialty) {
        this.specialty = specialty;
    }

    public List<Patient> getPatients() {
        return patients;
    }

    public void setPatients(List<Patient> patients) {
        this.patients = patients;
    }

    @Override
    public String toString() {
        return "Dinh dưỡng " + getId();
    }
}
