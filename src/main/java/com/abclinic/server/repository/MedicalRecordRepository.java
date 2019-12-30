package com.abclinic.server.repository;

import com.abclinic.server.model.entity.Disease;
import com.abclinic.server.model.entity.MedicalRecord;
import com.abclinic.server.model.entity.user.Patient;
import com.abclinic.server.model.entity.user.Practitioner;
import com.abclinic.server.model.entity.user.Specialist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    Optional<MedicalRecord> findById(long id);
    Optional<List<MedicalRecord>> findByPatient(Patient patient);
    Optional<List<MedicalRecord>> findByDisease(Disease disease);
    Optional<List<MedicalRecord>> findByPractitioner(Practitioner practitioner);
    Optional<List<MedicalRecord>> findBySpecialist(Specialist specialist);
}
