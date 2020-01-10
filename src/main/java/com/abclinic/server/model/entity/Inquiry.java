package com.abclinic.server.model.entity;

import com.abclinic.server.base.Views;
import com.abclinic.server.model.entity.user.Patient;
import com.abclinic.server.service.GooglePhotosService;
import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "inquiry")
public class Inquiry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Abridged.class)
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id")
    @JsonView(Views.Abridged.class)
    private Patient patient;

    @JsonView(Views.Public.class)
    private String albumId;

    @OneToMany(fetch = FetchType.LAZY, targetEntity = Reply.class, mappedBy = "inquiry")
    @JsonView(Views.Public.class)
    private List<Reply> replies;

    @JsonView(Views.Abridged.class)
    private String content;

    @JsonView(Views.Private.class)
    private int type;

    @JsonView(Views.Confidential.class)
    private int status;

    @JsonView(Views.Public.class)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @JsonView(Views.Public.class)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Inquiry() {
    }

    public Inquiry(Patient patient, String albumId, String content, int type) {
        this.patient = patient;
        this.albumId = albumId;
        this.type = type;
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public List<String> getAlbum() {
        return GooglePhotosService.getAlbumImages(albumId);
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    public List<Reply> getReplies() {
        return replies;
    }

    public void setReplies(List<Reply> replies) {
        this.replies = replies;
    }
}
