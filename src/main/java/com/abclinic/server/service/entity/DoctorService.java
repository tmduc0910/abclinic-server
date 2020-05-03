package com.abclinic.server.service.entity;

import com.abclinic.server.common.constant.FilterConstant;
import com.abclinic.server.common.constant.Role;
import com.abclinic.server.common.criteria.DoctorPredicateBuilder;
import com.abclinic.server.common.criteria.UserPredicateBuilder;
import com.abclinic.server.common.utils.StringUtils;
import com.abclinic.server.exception.NotFoundException;
import com.abclinic.server.model.entity.user.User;
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

    @Transactional
    @Override
    public Page<User> getList(User user, String search, UserPredicateBuilder builder, Pageable pageable) {
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
    public User getById(long id) {
        return userRepository.findById(id).orElseThrow(NotFoundException::new);
    }
}
