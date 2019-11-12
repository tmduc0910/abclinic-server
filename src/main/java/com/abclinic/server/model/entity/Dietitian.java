package com.abclinic.server.model.entity;

import com.abclinic.server.constant.Role;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "User")
@Where(clause = ("role = " + Role.DIETITIAN))
public class Dietitian extends User {
}
