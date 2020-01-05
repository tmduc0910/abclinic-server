package com.abclinic.server.model.entity;

import com.abclinic.server.base.Views;
import com.abclinic.server.model.entity.user.Patient;
import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "album")
public class ImageAlbum {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Public.class)
    private long id;

    @JsonView(Views.Confidential.class)
    private String uid;

    @OneToMany(fetch = FetchType.EAGER, targetEntity = Image.class, mappedBy = "imageAlbum")
    @JsonView(Views.Public.class)
    private List<Image> images;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    @JsonView(Views.Private.class)
    private Patient patient;

    @JsonView(Views.Public.class)
    private String content;

    @JsonView(Views.Public.class)
    private int type;

    @JsonView(Views.Confidential.class)
    private int status;

    @JsonView(Views.Private.class)
    private int type;

    @CreationTimestamp
    @JsonView(Views.Public.class)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @JsonView(Views.Public.class)
    private LocalDateTime updatedAt;

    public ImageAlbum() {
    }

    public ImageAlbum(String uid, Patient patient, String content, int type) {
        this.uid = uid;
        this.patient = patient;
        this.content = content;
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
