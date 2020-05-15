package com.abclinic.server.service.entity;

import com.abclinic.server.common.constant.Role;
import com.abclinic.server.common.constant.UserStatus;
import com.abclinic.server.exception.NotFoundException;
import com.abclinic.server.model.entity.user.*;
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
public class UserService implements IDataMapperService<User> {
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
    public List<User> findAllCoordinators() {
        return userRepository.findByRoleAndStatus(Role.COORDINATOR.getValue(), UserStatus.NEW.getValue());
    }

    @Transactional
    public Optional<User> findByUID(String uid) {
        return userRepository.findByUid(uid);
    }

    @Transactional
    public Optional<User> findByUsername(String username) {
        return userRepository.findByEmailOrPhoneNumber(username, username);
    }

    public void deactivateUser(User user) {
        user.setStatus(UserStatus.DEACTIVATED.getValue());
        switch (user.getRole()) {
            case PATIENT:
                Patient p = (Patient) user;
                p.setPractitioner(null);
                p.setSubDoctors(null);
                break;
            case PRACTITIONER:
                Practitioner pr = (Practitioner) user;
                pr.setPatients(null);
                break;
            case DIETITIAN:
                Dietitian d = (Dietitian) user;
                d.setPatients(null);
                break;
            case SPECIALIST:
                Specialist s = (Specialist) user;
                s.setPatients(null);
                break;
            default:
                return;
        }
        save(user);
    }

    @Override
    @Transactional
    public User getById(long id) throws NotFoundException {
        return userRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    @Override
    @Transactional
    public User save(User obj) {
        return userRepository.save(obj);
    }
}
