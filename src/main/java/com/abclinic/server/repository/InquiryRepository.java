package com.abclinic.server.repository;

import com.abclinic.server.model.entity.Inquiry;
import com.abclinic.server.model.entity.user.Patient;
import io.swagger.models.auth.In;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    Optional<Inquiry> findById(long id);
    Optional<List<Inquiry>> findByPatient(Patient patient);
}
