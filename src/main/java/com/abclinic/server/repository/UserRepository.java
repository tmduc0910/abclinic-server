package com.abclinic.server.repository;

import com.abclinic.server.model.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findById(int id);
    Optional<User> findByEmailAndPassword(String email, String password);
    Optional<User> findByPhoneNumber(String phoneNumber);
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailOrPhoneNumber(String email, String phone);
    Optional<List<User>> findAllByRoleIsLessThan(int role);
}
