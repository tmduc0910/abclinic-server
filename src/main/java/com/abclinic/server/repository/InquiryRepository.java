package com.abclinic.server.repository;

import com.abclinic.server.model.entity.payload.Inquiry;
import com.abclinic.server.model.entity.user.Patient;
import com.abclinic.server.model.entity.user.Practitioner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    Optional<Inquiry> findById(long id);

    Optional<List<Inquiry>> findByPatient(Patient patient);

    Optional<Page<Inquiry>> findByPatient(Patient patient, Pageable pageable);

    Optional<Page<Inquiry>> findByPatientAndStatus(Patient patient, int status, Pageable pageable);

    Optional<Page<Inquiry>> findByPatientPractitioner(Practitioner practitioner, Pageable pageable);
    Optional<Page<Inquiry>> findByPatientPractitionerAndType(Practitioner practitioner, int type, Pageable pageable);

    Optional<Page<Inquiry>> findByPatientInAndType(List<Patient> patients, int type, Pageable pageable);

    @Query(nativeQuery = true,
            countQuery = "select count(inquiry.id) from inquiry " +
                    "join doctor_patient dp on inquiry.patient_id = dp.patient_id and inquiry.type != dp.type " +
                    "join patient p on dp.patient_id = p.id " +
                    "join practitioner p2 on p.practitioner_id = p2.id " +
                    "where p2.id = :practitionerId " +
                    "and 2 > (" +
                        "select count(distinct type) " +
                        "from doctor_patient dp " +
                        "where dp.patient_id = inquiry.patient_id)",
            value = "select inquiry.patient_id, inquiry.type, inquiry.id, inquiry.album_id, inquiry.created_at, inquiry.content, inquiry.date, inquiry.status, inquiry.updated_at " +
                    "from inquiry " +
                    "join doctor_patient dp on inquiry.patient_id = dp.patient_id and inquiry.type != dp.type " +
                    "join patient p on dp.patient_id = p.id " +
                    "join practitioner p2 on p.practitioner_id = p2.id " +
                    "where p2.id = :practitionerId " +
                    "and 2 > (" +
                        "select count(distinct type) " +
                        "from doctor_patient dp " +
                        "where dp.patient_id = inquiry.patient_id)" +
                    "order by inquiry.created_at desc")
    Optional<Page<Inquiry>> findByPractitionerIdAndPatientSubDoctorsIsNull(long practitionerId, Pageable pageable);

    Optional<List<Inquiry>> findByPatientIdAndCreatedAtBetweenOrderByCreatedAtDesc(long id, LocalDateTime from, LocalDateTime to);
}
