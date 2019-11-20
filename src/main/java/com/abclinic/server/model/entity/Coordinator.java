package com.abclinic.server.model.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "coordinator")
public class Coordinator extends User {
    public Coordinator() {
    }

    public Coordinator(String name, String email, int gender, Date dateOfBirth, String password, String phoneNumber) {
        super(name, email, gender, dateOfBirth, password, phoneNumber);
    }
}
