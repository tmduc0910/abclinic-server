package com.abclinic.server.controller;

import com.abclinic.server.base.BaseController;
import com.abclinic.server.model.dto.AccountDto;
import com.abclinic.server.repository.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author tmduc
 * @package com.abclinic.server.controller
 * @created 1/9/2020 3:13 PM
 */
@RestController
@RequestMapping("/misc")
@Api(tags = "Khác")
public class MiscController extends BaseController {
    public MiscController(UserRepository userRepository, PractitionerRepository practitionerRepository, PatientRepository patientRepository, CoordinatorRepository coordinatorRepository, DietitianRepository dietitianRepository, SpecialistRepository specialistRepository, AlbumRepository albumRepository, ImageRepository imageRepository, MedicalRecordRepository medicalRecordRepository, DietitianRecordRepository dietitianRecordRepository, QuestionRepository questionRepository, ReplyRepository replyRepository, SpecialtyRepository specialtyRepository, DiseaseRepository diseaseRepository, HealthIndexRepository healthIndexRepository, HealthIndexScheduleRepository healthIndexScheduleRepository) {
        super(userRepository, practitionerRepository, patientRepository, coordinatorRepository, dietitianRepository, specialistRepository, albumRepository, imageRepository, medicalRecordRepository, dietitianRecordRepository, questionRepository, replyRepository, specialtyRepository, diseaseRepository, healthIndexRepository, healthIndexScheduleRepository);
    }

    @Override
    public void init() {
        this.logger = LoggerFactory.getLogger(MiscController.class);
    }

    @GetMapping("/wipe")
    @ApiOperation(value = "Xóa sạch UID của tất cả tài khoản")
    public ResponseEntity handleLogoutAll() {
        userRepository.findAll().forEach(u -> {
            u.setUid(null);
            save(u);
        });
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/accounts")
    @ApiOperation(value = "Trả về danh sách tài khoản đang lưu trong hệ thống")
    public ResponseEntity<List<AccountDto>> handleGetAllAccount() {
        return new ResponseEntity<>(userRepository.findAll().stream()
                .map(u -> new AccountDto(u.getEmail(), u.getPhoneNumber(), u.getPassword(), u.getName()))
                .collect(Collectors.toList()), HttpStatus.OK);
    }
}
