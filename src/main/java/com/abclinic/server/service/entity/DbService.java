package com.abclinic.server.service.entity;

import com.abclinic.server.exception.NotFoundException;
import com.abclinic.server.model.entity.user.User;

import java.util.Optional;

/**
 * @author tmduc
 * @package com.abclinic.server.service.entity
 * @created 4/30/2020 9:10 AM
 */
public interface DbService<T> {
    T getById(long id) throws NotFoundException;
}