package com.abclinic.server.service.entity;

import com.abclinic.server.common.constant.FilterConstant;
import com.abclinic.server.common.constant.Role;
import com.abclinic.server.common.criteria.DoctorPredicateBuilder;
import com.abclinic.server.common.criteria.EntityPredicateBuilder;
import com.abclinic.server.common.utils.StringUtils;
import com.abclinic.server.exception.NotFoundException;
import com.abclinic.server.model.entity.user.*;
import com.abclinic.server.repository.DietitianRepository;
import com.abclinic.server.repository.PractitionerRepository;
import com.abclinic.server.repository.SpecialistRepository;
import com.abclinic.server.repository.UserRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @author tmduc
 * @package com.abclinic.server.service.entity
 * @created 4/23/2020 8:41 PM
 */
@Service
public class DoctorService implements DataMapperService<User> {
    private PractitionerRepository practitionerRepository;
    private SpecialistRepository specialistRepository;
    private DietitianRepository dietitianRepository;
    private UserRepository userRepository;

    @Autowired
    public DoctorService(PractitionerRepository practitionerRepository, SpecialistRepository specialistRepository, DietitianRepository dietitianRepository, UserRepository userRepository) {
        this.practitionerRepository = practitionerRepository;
        this.specialistRepository = specialistRepository;
        this.dietitianRepository = dietitianRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Page<User> getList(User user, String search, EntityPredicateBuilder builder, Pageable pageable) {
        DoctorPredicateBuilder predBuilder = (DoctorPredicateBuilder) builder.init(search);
        if (!StringUtils.contains(search, FilterConstant.ROLE.getValue())) {
            predBuilder.with(FilterConstant.ROLE.getValue(), "!", Role.COORDINATOR.getValue())
                    .with(FilterConstant.ROLE.getValue(), "!", Role.PATIENT.getValue());
        }

        BooleanExpression expression = predBuilder.build();
        if (expression != null)
            return userRepository.findAll(expression, pageable);
        return userRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public User getById(long id) {
        return userRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    @Override
    public User save(User obj) {
        if (obj instanceof Practitioner)
            return practitionerRepository.save((Practitioner) obj);
        else if (obj instanceof Dietitian)
            return dietitianRepository.save((Dietitian) obj);
        else if (obj instanceof Specialist)
            return specialistRepository.save((Specialist) obj);
        else throw new IllegalArgumentException();
    }
}
