package com.abclinic.server.model.entity;

import com.abclinic.server.common.base.Views;
import com.abclinic.server.service.GooglePhotosService;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;

@Entity
@Table(name = "image")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Abridged.class)
    private long id;

    @Column(name = "uid")
    @JsonView(Views.Abridged.class)
    private String path;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "album_id")
//    @JsonView(Views.Private.class)
//    private ImageAlbum imageAlbum;

    @JsonView(Views.Public.class)
    private String fileName;

    @JsonView(Views.Public.class)
    private String fileType;

    public Image() {
    }

    public Image(String path, String fileName, String fileType) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.path = path;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

//    public ImageAlbum getImageAlbum() {
//        return imageAlbum;
//    }
//
//    public void setImageAlbum(ImageAlbum imageAlbum) {
//        this.imageAlbum = imageAlbum;
//    }

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
        return GooglePhotosService.getImage(path);
    }

    public void setPath(String path) {
        this.path = path;
    }
}
