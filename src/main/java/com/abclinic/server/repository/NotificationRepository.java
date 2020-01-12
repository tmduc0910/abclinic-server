package com.abclinic.server.repository;

import com.abclinic.server.model.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author tmduc
 * @package com.abclinic.server.repository
 * @created 1/11/2020 4:07 PM
 */
public interface NotificationRepository extends JpaRepository<Notification, Long> {
}