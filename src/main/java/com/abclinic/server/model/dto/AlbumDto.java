package com.abclinic.server.model.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tmduc
 * @package com.abclinic.server.model.dto
 * @created 2/4/2020 2:30 PM
 */
public class AlbumDto {
    private String albumId;
    private List<String> images;

    public AlbumDto() {
        images = new ArrayList<>();
    }

    public AlbumDto(String albumId, List<String> images) {
        this.albumId = albumId;
        this.images = images;
    }

    public String getAlbumId() {
        return albumId;
    }

    public List<String> getImages() {
        return images;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public void addImage(String url) {
        images.add(url);
    }

    public void setImages(List<String> images) {
        this.images = images;
    }
}
