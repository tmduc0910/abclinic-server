package com.abclinic.server.model.entity;

import com.abclinic.server.base.Views;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;

@Entity
@Table(name = "image")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Public.class)
    private int id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id")
    @JsonView(Views.Public.class)
    private ImageAlbum imageAlbum;
    @JsonView(Views.Public.class)
    private String fileName;
    @JsonView(Views.Public.class)
    private String fileType;
    @Column(name = "link")
    @JsonView(Views.Public.class)
    private String path;

    public Image() {
    }

    public Image(ImageAlbum imageAlbum, String fileName, String fileType, String path) {
        this.imageAlbum = imageAlbum;
        this.fileName = fileName;
        this.fileType = fileType;
        this.path = path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
