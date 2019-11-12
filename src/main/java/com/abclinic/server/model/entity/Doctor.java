package com.abclinic.server.model.entity;

import com.abclinic.server.constant.Role;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "User")
@Where(clause = ("role = " + Role.DOCTOR))
public class Doctor extends User {
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "doctor_patient",
            joinColumns = @JoinColumn(name = "doctor_id"),
            inverseJoinColumns = @JoinColumn(name = "patient_id")
    )
    private List<Patient> patients;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "doctor_specialist",
            joinColumns = @JoinColumn(name = "doctor_id"),
            inverseJoinColumns = @JoinColumn(name = "specialist_id")
    )
    private List<Specialist> specialists;

    public Doctor() {
    }

    public Doctor(int role, String name, String email, int gender, Date dateOfBirth, String password, String phoneNumber) {
        super(role, name, email, gender, dateOfBirth, password, phoneNumber);
    }

    public List<Patient> getPatients() {
        return patients;
    }

    public void setPatients(List<Patient> patients) {
        this.patients = patients;
    }

    public List<Specialist> getSpecialists() {
        return specialists;
    }

    public void setSpecialists(List<Specialist> specialists) {
        this.specialists = specialists;
    }
}
