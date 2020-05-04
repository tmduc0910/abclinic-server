package com.abclinic.server.repository;

import com.abclinic.server.model.entity.notification.Notification;
import com.abclinic.server.model.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author tmduc
 * @package com.abclinic.server.repository
 * @created 1/11/2020 4:07 PM
 */
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Optional<Notification> findById(long id);
    Optional<Page<Notification>> findByReceiver(User receiver, Pageable pageable);
}