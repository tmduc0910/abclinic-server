package com.abclinic.server.repository;

import com.abclinic.server.model.entity.ImageAlbum;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlbumRepository extends JpaRepository<ImageAlbum, Integer> {

}
