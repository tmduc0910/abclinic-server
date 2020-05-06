package com.abclinic.server.service.entity;

import com.abclinic.server.common.criteria.EntityPredicateBuilder;
import com.abclinic.server.exception.NotFoundException;
import com.abclinic.server.model.entity.payload.health_index.HealthIndex;
import com.abclinic.server.model.entity.user.User;
import com.abclinic.server.repository.HealthIndexRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.transaction.Transactional;

/**
 * @author tmduc
 * @package com.abclinic.server.service.entity
 * @created 5/4/2020 2:08 PM
 */
public class HealthIndexService implements IDataMapperService<HealthIndex> {
    private HealthIndexRepository healthIndexRepository;

    @Autowired
    public HealthIndexService(HealthIndexRepository healthIndexRepository) {
        this.healthIndexRepository = healthIndexRepository;
    }

    @Override
    @Transactional
    public HealthIndex getById(long id) throws NotFoundException {
        return healthIndexRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    @Override
    @Transactional
    public Page<HealthIndex> getList(User user, String search, EntityPredicateBuilder builder, Pageable pageable) {
        return null;
    }

    @Override
    public HealthIndex save(HealthIndex obj) {
        return healthIndexRepository.save(obj);
    }
}
