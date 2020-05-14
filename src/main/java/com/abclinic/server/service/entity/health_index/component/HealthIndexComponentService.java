package com.abclinic.server.service.entity.health_index.component;

import com.abclinic.server.exception.NotFoundException;
import com.abclinic.server.model.entity.payload.health_index.HealthIndex;
import com.abclinic.server.model.entity.user.User;
import com.abclinic.server.repository.HealthIndexRepository;
import com.abclinic.server.service.entity.IDataMapperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

/**
 * @author tmduc
 * @package com.abclinic.server.service.entity.health_index.component
 * @created 5/6/2020 2:25 PM
 */
@Component
public class HealthIndexComponentService implements IDataMapperService<HealthIndex> {
    private HealthIndexRepository healthIndexRepository;

    @Autowired
    public HealthIndexComponentService(HealthIndexRepository healthIndexRepository) {
        this.healthIndexRepository = healthIndexRepository;
    }

    @Override
    @Transactional
    public HealthIndex getById(long id) throws NotFoundException {
        return healthIndexRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    public boolean isExist(String name) {
        return healthIndexRepository.findByNameIgnoreCase(name).isEmpty();
    }

    @Override
    @Transactional
    public Page<HealthIndex> getList(User user, Pageable pageable) {
        return healthIndexRepository.findAll(pageable);
    }

    @Override
    public HealthIndex save(HealthIndex obj) {
        return healthIndexRepository.save(obj);
    }
}
