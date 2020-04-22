package com.abclinic.server.model.entity;

import com.abclinic.server.common.base.Views;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;

@Entity
@Table(name = "specialty")
public class Specialty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Abridged.class)
    private long id;

    @JsonView(Views.Abridged.class)
    private String name;

    @JsonView(Views.Abridged.class)
    private String detail;

    public Specialty() {
    }

    public Specialty(String name, String detail) {
        this.name = name;
        this.detail = detail;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    @Override
    public int hashCode() {
        return (int) this.id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Specialty)
            return this.id == ((Specialty) obj).id;
        return false;
    }
}
