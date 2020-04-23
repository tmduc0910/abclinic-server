package com.abclinic.server.service.entity;

import com.abclinic.server.common.constant.Constant;
import com.abclinic.server.common.constant.FilterConstant;
import com.abclinic.server.common.constant.Role;
import com.abclinic.server.common.criteria.DoctorPredicateBuilder;
import com.abclinic.server.common.criteria.UserPredicateBuilder;
import com.abclinic.server.common.utils.StringUtils;
import com.abclinic.server.model.entity.user.Doctor;
import com.abclinic.server.model.entity.user.Patient;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author tmduc
 * @package com.abclinic.server.service.entity
 * @created 4/23/2020 8:41 PM
 */
@Service
public class DoctorService {
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
    public Page<User> getDoctors(User user, String search, DoctorPredicateBuilder builder, Pageable pageable) {
        if (!StringUtils.endsWith(search, ","))
            search += ",";

        if (!StringUtils.isNull(search)) {
            Pattern pattern = Pattern.compile(Constant.FILTER_REGEX, Pattern.UNICODE_CHARACTER_CLASS);
            Matcher matcher = pattern.matcher(search + ",");
            while (matcher.find()) {
                builder.with(matcher.group(1), matcher.group(2), matcher.group(3));
            }
        }

        if (!StringUtils.contains(search, FilterConstant.ROLE.getValue())) {
            builder.with(FilterConstant.ROLE.getValue(), "!", Role.COORDINATOR.getValue())
                    .with(FilterConstant.ROLE.getValue(), "!", Role.PATIENT.getValue());
        }

        BooleanExpression expression = builder.build();
        if (expression != null)
            return userRepository.findAll(expression, pageable);
        return userRepository.findAll(pageable);
    }
}
