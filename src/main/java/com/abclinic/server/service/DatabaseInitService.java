package com.abclinic.server.service;

import com.abclinic.server.common.constant.Role;
import com.abclinic.server.controller.AuthController;
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

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    private Logger logger = LoggerFactory.getLogger(DatabaseInitService.class);

    @Value("${spring.liquibase.drop-first}")
    private boolean isDropFirst;

    @Override
    public void run(String... args) throws Exception {
        if (isDropFirst) {
            try {
                //INIT USERS
                authController.processSignUp(null, Role.PRACTITIONER.getValue(), "pra01@mail.com", "123456", "Bác sĩ đa khoa 01", 0, "01/01/1981", "01567135178");
                authController.processSignUp(null, Role.SPECIALIST.getValue(), "spe01@mail.com", "123456", "Bác sĩ chuyên khoa 01", 0, "04/04/1974", "0866215135");
                authController.processSignUp(null, Role.DIETITIAN.getValue(), "die01@mail.com", "123456", "Bác sĩ dinh dưỡng 01", 1, "10/03/1984", "0466881682");
                authController.processSignUp(null, Role.COORDINATOR.getValue(), "coo01@mail.com", "123456", "Điều phối viên 01", 1, "27/05/1991", "0991138432");

                User user = userRepository.findById(4).get();
                authController.processSignUp(user, Role.PATIENT.getValue(), "pat01@mail.com", "123456", "Bệnh nhân 01", 1, "03/05/1989", "0986135713");
                authController.processSignUp(user, Role.PATIENT.getValue(), "pat02@mail.com", "123456", "Bệnh nhân 02", 1, "22/12/1984", "01862135841");
                authController.processSignUp(user, Role.PATIENT.getValue(), "pat03@mail.com", "123456", "Bệnh nhân 03", 0, "15/03/1989", "0115618846");

                //INIT DISEASES
                diseaseRepository.save(new Disease("Viêm thận", "Thận bị viêm"));
                diseaseRepository.save(new Disease("Viêm gan", "Gan bị viêm"));
                diseaseRepository.save(new Disease("Viêm màng nhĩ", "Màng nhĩ bị viêm"));
                diseaseRepository.save(new Disease("Viêm màng túi", "Hết tiền"));
            } finally {
                logger.info("Database Init");
            }
        }
    }
}
