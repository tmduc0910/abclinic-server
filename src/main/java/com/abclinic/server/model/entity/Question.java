package com.abclinic.server.model.entity;

import com.abclinic.server.base.Views;
import com.abclinic.server.constant.QuestionType;
import com.abclinic.server.model.entity.user.Patient;
import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "question")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Abridged.class)
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id")
    @JsonView(Views.Abridged.class)
    private Patient patient;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id")
    @JsonView(Views.Abridged.class)
    private ImageAlbum album;

    @OneToMany(fetch = FetchType.LAZY, targetEntity = Reply.class, mappedBy = "question")
    @JsonView(Views.Public.class)
    private List<Reply> replies;

    @JsonView(Views.Private.class)
    private int questionType;

    @JsonView(Views.Abridged.class)
    private String content;

    @JsonView(Views.Confidential.class)
    private int status;

    @JsonView(Views.Public.class)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @JsonView(Views.Public.class)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Question() {
    }

    public Question(Patient patient, String content) {
        this.patient = patient;
        this.questionType = QuestionType.INQUIRY.getValue();
        this.content = content;
    }

    public Question(Patient patient, ImageAlbum album, String content) {
        this.patient = patient;
        this.album = album;
        this.content = content;
        this.questionType = QuestionType.DIET.getValue();
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

    public ImageAlbum getAlbum() {
        return album;
    }

    public int getQuestionType() {
        return questionType;
    }

    public void setQuestionType(int questionType) {
        this.questionType = questionType;
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
