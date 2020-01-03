package com.abclinic.server.model.entity;

import com.abclinic.server.base.Views;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;

@Entity
@Table(name = "image")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Private.class)
    private long id;

    @JsonView(Views.Public.class)
    private String uid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id")
    @JsonIgnore
    private ImageAlbum imageAlbum;

    @JsonView(Views.Private.class)
    private String fileName;

    @JsonView(Views.Private.class)
    private String fileType;

    public Image() {
    }

    public Image(String uid, ImageAlbum imageAlbum, String fileName, String fileType) {
        this.uid = uid;
        this.imageAlbum = imageAlbum;
        this.fileName = fileName;
        this.fileType = fileType;
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

    public ImageAlbum getImageAlbum() {
        return imageAlbum;
    }

    public void setImageAlbum(ImageAlbum imageAlbum) {
        this.imageAlbum = imageAlbum;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
}
