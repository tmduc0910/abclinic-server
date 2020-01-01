package com.abclinic.server.repository;

import com.abclinic.server.model.entity.user.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findById(long id);
    Optional<User> findByUid(String uid);
    Optional<User> findByEmailAndPassword(String email, String password);
    Optional<User> findByPhoneNumber(String phoneNumber);
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailOrPhoneNumber(String email, String phone);
    Optional<List<User>> findAllByRoleIsLessThanAndStatus(int role, int status, Pageable pageable);
    Optional<List<User>> findByRoleAndStatus(int role, int status, Pageable pageable);
    Optional<List<User>> findByStatus(int status, Pageable pageable);
}
