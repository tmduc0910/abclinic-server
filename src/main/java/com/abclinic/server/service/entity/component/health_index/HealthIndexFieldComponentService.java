package com.abclinic.server.service.entity.component.health_index;

import com.abclinic.server.exception.NotFoundException;
import com.abclinic.server.model.entity.payload.health_index.HealthIndex;
import com.abclinic.server.model.entity.payload.health_index.HealthIndexField;
import com.abclinic.server.model.entity.user.User;
import com.abclinic.server.repository.HealthIndexFieldRepository;
import com.abclinic.server.service.entity.IDataMapperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @author tmduc
 * @package com.abclinic.server.service.entity.health_index.component
 * @created 5/6/2020 2:38 PM
 */
@Component
public class HealthIndexFieldComponentService implements IDataMapperService<HealthIndexField> {
    private HealthIndexFieldRepository healthIndexFieldRepository;

    @Autowired
    public HealthIndexFieldComponentService(HealthIndexFieldRepository healthIndexFieldRepository) {
        this.healthIndexFieldRepository = healthIndexFieldRepository;
    }

    @Override
    @Transactional
    public HealthIndexField getById(long id) throws NotFoundException {
        return healthIndexFieldRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    public boolean isExist(HealthIndex index, String name) {
        return !healthIndexFieldRepository.findByHealthIndexAndNameIgnoreCase(index, name).isEmpty();
    }

    public List<HealthIndexField> getByName(String name) {
        return healthIndexFieldRepository.findByName(name);
    }

    @Transactional
    public List<HealthIndexField> getList(HealthIndex healthIndex) {
        return healthIndexFieldRepository.findByHealthIndex(healthIndex).orElseThrow(NotFoundException::new);
    }

    @Override
    public HealthIndexField save(HealthIndexField obj) {
        return healthIndexFieldRepository.save(obj);
    }
}
