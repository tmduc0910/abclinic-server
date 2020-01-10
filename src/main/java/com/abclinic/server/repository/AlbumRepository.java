package com.abclinic.server.repository;

import com.abclinic.server.model.entity.user.Patient;
import com.abclinic.server.model.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlbumRepository extends UserRepository {
//    Optional<ImageAlbum> findById(long id);
//    Optional<List<ImageAlbum>> findByPatient(Patient patient);
}
