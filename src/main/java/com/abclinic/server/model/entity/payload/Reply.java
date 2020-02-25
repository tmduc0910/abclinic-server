package com.abclinic.server.model.entity.payload;

import com.abclinic.server.base.Views;
import com.abclinic.server.model.entity.user.User;
import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reply")
public class Reply extends Payload {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inquiry_id")
    @JsonView(Views.Private.class)
    private Inquiry inquiry;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonView(Views.Abridged.class)
    private User user;

    @JsonView(Views.Abridged.class)
    private String content;

    @JsonView(Views.Confidential.class)
    private int status;

    @CreationTimestamp
    @JsonView(Views.Abridged.class)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @JsonView(Views.Public.class)
    private LocalDateTime updatedAt;

    public Reply() {
    }

    public Reply(Inquiry inquiry, User user, String content) {
        this.inquiry = inquiry;
        this.user = user;
        this.content = content;
    }

    public Inquiry getInquiry() {
        return inquiry;
    }

    public void setInquiry(Inquiry inquiry) {
        this.inquiry = inquiry;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
}
