package com.abclinic.server.service.entity;

import com.abclinic.server.exception.WrongCredentialException;
import com.abclinic.server.model.entity.user.User;
import com.abclinic.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author tmduc
 * @package com.abclinic.server.service.entity
 * @created 5/3/2020 10:15 AM
 */
@Service
public class UserService {
    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> findByUsernamePassword(String username, String password) {
        return userRepository.findByEmailOrPhoneNumberAndPassword(username, username, password);
    }
}
