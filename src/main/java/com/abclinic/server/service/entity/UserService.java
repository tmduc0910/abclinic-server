package com.abclinic.server.service.entity;

import com.abclinic.server.exception.NotFoundException;
import com.abclinic.server.exception.WrongCredentialException;
import com.abclinic.server.model.entity.user.User;
import com.abclinic.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * @author tmduc
 * @package com.abclinic.server.service.entity
 * @created 5/3/2020 10:15 AM
 */
@Service
public class UserService implements DataMapperService<User> {
    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public Optional<User> findByUsernamePassword(String username, String password) {
        return userRepository.findByEmailOrPhoneNumberAndPassword(username, username, password);
    }

    @Transactional
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public Optional<User> findByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }

    @Transactional
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional
    public Optional<User> findByUID(String uid) {
        return userRepository.findByUid(uid);
    }

    @Transactional
    public Optional<User> findByUsername(String username) {
        return userRepository.findByEmailOrPhoneNumber(username, username);
    }

    @Override
    @Transactional
    public User getById(long id) throws NotFoundException {
        return userRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    @Override
    public User save(User obj) {
        return userRepository.save(obj);
    }
}
