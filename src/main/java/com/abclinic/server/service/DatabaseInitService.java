package com.abclinic.server.service;

import com.abclinic.server.constant.Role;
import com.abclinic.server.controller.AuthController;
import com.abclinic.server.model.entity.user.User;
import com.abclinic.server.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

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

    private Logger logger = LoggerFactory.getLogger(DatabaseInitService.class);

    @Value("${spring.liquibase.drop-first}")
    private boolean isDropFirst;

    @Override
    public void run(String... args) throws Exception {
        if (isDropFirst) {
            authController.processDoctorSignUp(Role.PRACTITIONER.ordinal(), "pra01@mail.com", "123456", "Bác sĩ đa khoa 01", 0, "01/01/1981", "01567135178");
            authController.processDoctorSignUp(Role.SPECIALIST.ordinal(), "spe01@mail.com", "123456", "Bác sĩ chuyên khoa 01", 0, "04/04/1974", "0866215135");
            authController.processDoctorSignUp(Role.DIETITIAN.ordinal(), "die01@mail.com", "123456", "Bác sĩ dinh dưỡng 01", 1, "10/03/1984", "0466881682");
            authController.processDoctorSignUp(Role.COORDINATOR.ordinal(), "coo01@mail.com", "123456", "Điều phối viên 01", 1, "27/05/1991", "0991138432");

            User user = userRepository.findById(4).get();
            authController.processSignUp(user, "pat01@mail.com", "123456", "Bệnh nhân 01", 1, "03/05/1989", "0986135713");
            authController.processSignUp(user, "pat02@mail.com", "123456", "Bệnh nhân 02", 1, "22/12/1984", "01862135841");
            authController.processSignUp(user, "pat03@mail.com", "123456", "Bệnh nhân 03", 0, "15/03/1989", "0115618846");
            logger.info("Database init");
        }
    }
}
