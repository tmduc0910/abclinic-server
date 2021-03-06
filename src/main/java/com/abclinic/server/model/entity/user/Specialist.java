package com.abclinic.server.model.entity.user;

import com.abclinic.server.common.base.Views;
import com.abclinic.server.common.constant.RoleValue;
import com.abclinic.server.model.entity.Specialty;
import com.abclinic.server.serializer.AbridgedViewSerializer;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "specialist")
public class Specialist extends Doctor {
    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialty_id")
    @JsonView(Views.Public.class)
    @JsonSerialize(using = AbridgedViewSerializer.class)
    private Specialty specialty;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "doctor_patient",
            joinColumns = @JoinColumn(name = "doctor_id"),
            inverseJoinColumns = @JoinColumn(name = "patient_id")
    )
    @JsonView(Views.Private.class)
    @JsonSerialize(using = AbridgedViewSerializer.class)
    private List<Patient> patients;

    public Specialist() {
    }

    public Specialist(String name, String email, int gender, LocalDate dateOfBirth, String password, String phoneNumber) {
        super(RoleValue.SPECIALIST, name, email, gender, dateOfBirth, password, phoneNumber);
    }

    public Specialist(String name, String email, int gender, LocalDate dateOfBirth, String password, String phoneNumber, String description) {
        super(RoleValue.SPECIALIST, name, email, gender, dateOfBirth, password, phoneNumber, description);
    }

    public Specialist(String name, String email, int gender, LocalDate dateOfBirth, String password, String phoneNumber, String description, Specialty specialty) {
        super(RoleValue.SPECIALIST, name, email, gender, dateOfBirth, password, phoneNumber, description);
        this.specialty = specialty;
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
        return "Chuyên khoa " + getId();
    }
}
