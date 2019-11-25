package com.abclinic.server.controller;

import com.abclinic.server.base.BaseController;
import com.abclinic.server.repository.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * @package com.abclinic.server.controller
 * @author tmduc
 * @created 11/23/2019 3:38 PM
 */
@RestController
public class AdminController extends BaseController {

    public AdminController(UserRepository userRepository, PractitionerRepository practitionerRepository, PatientRepository patientRepository, CoordinatorRepository coordinatorRepository, DietitianRepository dietitianRepository, SpecialistRepository specialistRepository, AlbumRepository albumRepository, ImageRepository imageRepository, MedicalRecordRepository medicalRecordRepository, QuestionRepository questionRepository, ReplyRepository replyRepository) {
        super(userRepository, practitionerRepository, patientRepository, coordinatorRepository, dietitianRepository, specialistRepository, albumRepository, imageRepository, medicalRecordRepository, questionRepository, replyRepository);
    }

    @Override
    public void init() {
        logger = LoggerFactory.getLogger(AdminController.class);
    }

    
}