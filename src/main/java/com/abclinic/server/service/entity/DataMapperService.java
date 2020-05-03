package com.abclinic.server.service.entity;

import com.abclinic.server.common.criteria.DoctorPredicateBuilder;
import com.abclinic.server.common.criteria.UserPredicate;
import com.abclinic.server.common.criteria.UserPredicateBuilder;
import com.abclinic.server.exception.NotFoundException;
import com.abclinic.server.model.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * @author tmduc
 * @package com.abclinic.server.service.entity
 * @created 4/30/2020 9:10 AM
 */
public interface DataMapperService<T> {
    T getById(long id) throws NotFoundException;

    default Page<T> getList(User user, String search, UserPredicateBuilder builder, Pageable pageable) {
        throw new UnsupportedOperationException();
    }

    default Page<T> getList(User user, boolean assigned, Pageable pageable) {
        throw new UnsupportedOperationException();
    }
}