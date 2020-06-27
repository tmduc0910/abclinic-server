package com.abclinic.server.service.entity;

import com.abclinic.server.common.criteria.EntityPredicateBuilder;
import com.abclinic.server.exception.NotFoundException;
import com.abclinic.server.model.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author tmduc
 * @package com.abclinic.server.service.entity
 * @created 4/30/2020 9:10 AM
 */
public interface IDataMapperService<T> {
    T getById(long id) throws NotFoundException;

    default Page<T> getList(User user, String search, EntityPredicateBuilder builder, Pageable pageable) {
        throw new UnsupportedOperationException();
    }

    default Page<T> getList(User user, Pageable pageable) {
        throw new UnsupportedOperationException();
    }

    T save(T obj);

}