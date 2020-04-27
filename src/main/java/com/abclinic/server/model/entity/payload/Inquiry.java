package com.abclinic.server.model.entity.payload;

import com.abclinic.server.common.base.Views;
import com.abclinic.server.model.entity.user.Patient;
import com.abclinic.server.model.entity.user.User;
import com.abclinic.server.service.GooglePhotosService;
import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "inquiry")
public class Inquiry extends Payload {

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

    @JsonView(Views.Abridged.class)
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

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public List<String> getAlbum() {
        return albumId == null ? null : GooglePhotosService.getAlbumImages(albumId);
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

    public boolean of(User user) {
        switch (user.getRole()) {
            case DIETITIAN:
                return patient.getDietitians().contains(user);
            case SPECIALIST:
                return patient.getSpecialists().contains(user);
            case PRACTITIONER:
                return patient.getPractitioner().equals(user);
            case PATIENT:
                return patient.equals(user);
        }
        return false;
    }
}
