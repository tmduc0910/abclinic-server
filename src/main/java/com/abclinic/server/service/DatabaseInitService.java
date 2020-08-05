package com.abclinic.server.service;

import com.abclinic.server.common.constant.Role;
import com.abclinic.server.controller.AuthController;
import com.abclinic.server.model.dto.request.post.RequestSignUpDto;
import com.abclinic.server.model.entity.Disease;
import com.abclinic.server.model.entity.user.User;
import com.abclinic.server.repository.DiseaseRepository;
import com.abclinic.server.repository.MedicalRecordRepository;
import com.abclinic.server.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

/**
 * @author tmduc
 * @package com.abclinic.server.service
 * @created 12/30/2019 7:41 PM
 */

@Component
public class DatabaseInitService implements CommandLineRunner {
    @Autowired
    private AuthController authController;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DiseaseRepository diseaseRepository;

    private Logger logger = LoggerFactory.getLogger(DatabaseInitService.class);

    @Value("${spring.liquibase.drop-first}")
    private boolean isDropFirst;

    @Override
    public void run(String... args) throws Exception {
        if (isDropFirst) {
            try {
                authController.processSignUp(null, new RequestSignUpDto(Role.COORDINATOR.getValue(), "coo01@mail.com", "123456", "Điều phối viên 01", 1, "27/05/1991", "0991138432"));
            } finally {
                logger.info("Database Init");
            }
        }
    }
}
