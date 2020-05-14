package com.abclinic.server.repository;

import com.abclinic.server.model.entity.user.QUser;
import com.abclinic.server.model.entity.user.User;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringPath;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.querydsl.binding.SingleValueBinding;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>,
        QuerydslPredicateExecutor<User>, QuerydslBinderCustomizer<QUser> {
    Optional<User> findById(long id);

    Optional<User> findByUid(String uid);

    Optional<User> findByEmailAndPassword(String email, String password);

    Optional<User> findByPhoneNumberAndPassword(String phoneNumber, String password);

    Optional<User> findByEmailOrPhoneNumberAndPassword(String email, String phoneNumber, String password);

    Optional<User> findByPhoneNumber(String phoneNumber);

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailOrPhoneNumber(String email, String phone);

    Optional<Page<User>> findAllByRoleIsLessThanAndStatus(int role, int status, Pageable pageable);

    Optional<Page<User>> findByRoleAndStatus(int role, int status, Pageable pageable);

    List<User> findByRoleAndStatus(int role, int status);

    Optional<Page<User>> findByStatus(int status, Pageable pageable);

    @Override
    default void customize(QuerydslBindings querydslBindings, QUser qUser) {
        querydslBindings.bind(String.class)
                .first((SingleValueBinding<StringPath, String>) StringExpression::containsIgnoreCase);
        querydslBindings.excluding(qUser.email);
    };
}
