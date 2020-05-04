package com.abclinic.server.service.entity;

import com.abclinic.server.exception.NotFoundException;
import com.abclinic.server.model.entity.Specialty;
import com.abclinic.server.repository.SpecialtyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @author tmduc
 * @package com.abclinic.server.service.entity
 * @created 5/4/2020 10:40 AM
 */
@Service
public class SpecialtyService implements DataMapperService<Specialty> {
    private SpecialtyRepository specialtyRepository;

    @Autowired
    public SpecialtyService(SpecialtyRepository specialtyRepository) {
        this.specialtyRepository = specialtyRepository;
    }

    @Transactional
    public List<Specialty> getAll() {
        return specialtyRepository.findAll();
    }

    @Override
    @Transactional
    public Specialty getById(long id) throws NotFoundException {
        return specialtyRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    @Override
    public Specialty save(Specialty obj) {
        return specialtyRepository.save(obj);
    }
}
