package com.abclinic.server.service.entity;

import com.abclinic.server.common.constant.FilterConstant;
import com.abclinic.server.common.constant.Role;
import com.abclinic.server.common.criteria.DiseasePredicateBuilder;
import com.abclinic.server.common.criteria.DoctorPredicateBuilder;
import com.abclinic.server.common.criteria.EntityPredicateBuilder;
import com.abclinic.server.common.utils.StringUtils;
import com.abclinic.server.exception.NotFoundException;
import com.abclinic.server.model.entity.Disease;
import com.abclinic.server.model.entity.user.User;
import com.abclinic.server.repository.DiseaseRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * @author tmduc
 * @package com.abclinic.server.service.entity
 * @created 5/13/2020 3:25 PM
 */
@Service
public class DiseaseService implements IDataMapperService<Disease> {
    private DiseaseRepository diseaseRepository;

    @Autowired
    public DiseaseService(DiseaseRepository diseaseRepository) {
        this.diseaseRepository = diseaseRepository;
    }

    public boolean isExist(String name) {
        return !diseaseRepository.findByName(name).isEmpty();
    }

    @Override
    @Transactional
    public Disease getById(long id) throws NotFoundException {
        return diseaseRepository.findById(id);
    }

    @Override
    public Page<Disease> getList(User user, String search, EntityPredicateBuilder builder, Pageable pageable) {
        DiseasePredicateBuilder predBuilder = (DiseasePredicateBuilder) builder.init(search);
        BooleanExpression expression = predBuilder.build();
        if (expression != null)
            return diseaseRepository.findAll(expression, pageable);
        return diseaseRepository.findAll(pageable);
    }

    @Override
    public Disease save(Disease obj) {
        return diseaseRepository.save(obj);
    }
}
