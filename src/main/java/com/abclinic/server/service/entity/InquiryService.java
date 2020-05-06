package com.abclinic.server.service.entity;

import com.abclinic.server.exception.NotFoundException;
import com.abclinic.server.model.entity.payload.Inquiry;
import com.abclinic.server.model.entity.user.*;
import com.abclinic.server.repository.InquiryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * @author tmduc
 * @package com.abclinic.server.service.entity
 * @created 4/22/2020 10:33 AM
 */
@Service
public class InquiryService implements IDataMapperService<Inquiry> {
    private InquiryRepository inquiryRepository;
    private PatientService patientService;
    private DoctorService doctorService;

    @Autowired
    public InquiryService(InquiryRepository inquiryRepository, PatientService patientService, DoctorService doctorService) {
        this.inquiryRepository = inquiryRepository;
        this.patientService = patientService;
        this.doctorService = doctorService;
    }

    @Transactional
    public List<Inquiry> getByPatient(Patient patient) {
        return inquiryRepository.findByPatient(patient).orElseThrow(NotFoundException::new);
    }

    @Override
    @Transactional
    public Inquiry getById(long id) throws NotFoundException {
        return inquiryRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    @Override
    @Transactional
    public Page<Inquiry> getList(User user, boolean assigned, Pageable pageable) {
        Optional<Page<Inquiry>> inquiries = Optional.empty();
        switch (user.getRole()) {
            case COORDINATOR:
                inquiries = inquiryRepository.findByPatientPractitioner(null, pageable);
                break;
            case PRACTITIONER:
                Practitioner practitioner = (Practitioner) doctorService.getById(user.getId());
                if (!assigned)
                    inquiries = inquiryRepository.findByPatientPractitionerAndPatientSpecialistsIsNullOrPatientPractitionerAndPatientDietitiansIsNull(practitioner, practitioner, pageable);
                else inquiries = inquiryRepository.findByPatientPractitioner(practitioner, pageable);
                break;
            case SPECIALIST:
                Specialist specialist = (Specialist) doctorService.getById(user.getId());
                inquiries = inquiryRepository.findByPatientIn(specialist.getPatients(), pageable);
                break;
            case DIETITIAN:
                Dietitian dietitian = (Dietitian) doctorService.getById(user.getId());
                inquiries = inquiryRepository.findByPatientIn(dietitian.getPatients(), pageable);
                break;
            case PATIENT:
                Patient patient = patientService.getById(user.getId());
                inquiries = inquiryRepository.findByPatient(patient, pageable);
                break;
        }
        return inquiries.orElseThrow(NotFoundException::new);
    }

    @Override
    public Inquiry save(Inquiry obj) {
        return inquiryRepository.save(obj);
    }
}
