package com.abclinic.server.repository;

import com.abclinic.server.model.entity.ImageAlbum;
import com.abclinic.server.model.entity.user.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlbumRepository extends JpaRepository<ImageAlbum, Integer> {
    Optional<ImageAlbum> findById(int id);
    Optional<List<ImageAlbum>> findByPatient(Patient patient);
}
