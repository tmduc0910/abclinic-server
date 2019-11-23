package com.abclinic.server.model.entity.user;

import com.abclinic.server.constant.RoleValue;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "coordinator")
public class Coordinator extends User {
    public Coordinator() {
    }

    public Coordinator(String name, String email, int gender, LocalDate dateOfBirth, String password, String phoneNumber) {
        super(RoleValue.COORDINATOR, name, email, gender, dateOfBirth, password, phoneNumber);
    }
}
