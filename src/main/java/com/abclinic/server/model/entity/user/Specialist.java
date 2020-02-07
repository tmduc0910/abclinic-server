package com.abclinic.server.model.entity.user;

import com.abclinic.server.base.Views;
import com.abclinic.server.constant.RoleValue;
import com.abclinic.server.model.entity.Specialty;
import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.annotations.WhereJoinTable;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "specialist")
public class Specialist extends Doctor {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialty_id")
    @JsonView(Views.Public.class)
    private Specialty specialty;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "doctor_patient",
            joinColumns = @JoinColumn(name = "doctor_id"),
            inverseJoinColumns = @JoinColumn(name = "patient_id")
    )
    @JsonView(Views.Private.class)
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
}
