package com.abclinic.server.controller;

import com.abclinic.server.base.BaseController;
import com.abclinic.server.base.Views;
import com.abclinic.server.constant.Role;
import com.abclinic.server.constant.RoleValue;
import com.abclinic.server.constant.Status;
import com.abclinic.server.exception.BadRequestException;
import com.abclinic.server.exception.NotFoundException;
import com.abclinic.server.model.entity.Specialty;
import com.abclinic.server.model.entity.user.*;
import com.abclinic.server.repository.*;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * @author tmduc
 * @package com.abclinic.server.controller
 * @created 12/3/2019 2:59 PM
 */
@RestController
@RequestMapping("/admin")
public class DoctorController extends BaseController {

    public DoctorController(UserRepository userRepository, PractitionerRepository practitionerRepository, PatientRepository patientRepository, CoordinatorRepository coordinatorRepository, DietitianRepository dietitianRepository, SpecialistRepository specialistRepository, AlbumRepository albumRepository, ImageRepository imageRepository, MedicalRecordRepository medicalRecordRepository, QuestionRepository questionRepository, ReplyRepository replyRepository, SpecialtyRepository specialtyRepository) {
        super(userRepository, practitionerRepository, patientRepository, coordinatorRepository, dietitianRepository, specialistRepository, albumRepository, imageRepository, medicalRecordRepository, questionRepository, replyRepository, specialtyRepository);
    }

    @Override
    public void init() {
        this.logger = LoggerFactory.getLogger(DoctorController.class);
    }

//    @GetMapping("/doctors/dietitian")
//    public ResponseEntity<List<Dietitian>> getAllDietitian() {
//        return new ResponseEntity<>(dietitianRepository.findAll(), HttpStatus.OK);
//    }
//
//    @GetMapping("/doctors/dietitian/{id}")
//    public ResponseEntity<Dietitian> getDietitian(@RequestHeader("user-id") int userId, @PathVariable int id) {
//        return dietitianRepository.findById(id).map(dietitian -> new ResponseEntity<>(dietitian, HttpStatus.OK)).orElseThrow(() -> new BadRequestException(userId));
//    }
//
//    @GetMapping("/doctors/practitioner")
//    public ResponseEntity<List<Practitioner>> getAllPractitioner() {
//        return new ResponseEntity<>(practitionerRepository.findAll(), HttpStatus.OK);
//    }
//
//    @GetMapping("/doctors/practitioner/{id}")
//    public ResponseEntity<Practitioner> getPractitioner(@RequestHeader("user-id") int userId, @PathVariable int id) {
//        return practitionerRepository.findById(id).map(practitioner -> new ResponseEntity<>(practitioner, HttpStatus.OK)).orElseThrow(() -> new BadRequestException(userId));
//    }
//
//    @GetMapping("/doctors/specialist")
//    public ResponseEntity<List<Specialist>> getAllSpecialist() {
//        return new ResponseEntity<>(specialistRepository.findAll(), HttpStatus.OK);
//    }
//
//    @GetMapping("/doctors/specialist/{id}")
//    public ResponseEntity<Specialist> getSpecialist(@RequestHeader("user-id") int userId, @PathVariable int id) {
//        return specialistRepository.findById(id).map(specialist -> new ResponseEntity<>(specialist, HttpStatus.OK)).orElseThrow(() -> new BadRequestException(userId));
//    }
//
//    @GetMapping("/specialties")
//    public ResponseEntity<List<Specialty>> getSpecialties() {
//        return new ResponseEntity<>(specialtyRepository.findAll(), HttpStatus.OK);
//    }

    @ApiOperation(value = "Lọc và lấy danh sách bác sĩ",  notes = "Trả về danh sách các bác sĩ còn đang hoạt động hoặc nếu không tồn tại thì trả về 404 NOT FOUND")
    @ApiParam(name = "type", value = "Loại bác sĩ, rỗng là lấy tất", allowEmptyValue = true, required = false, allowableValues = "0 (đa khoa), 1 (chuyên khoa), 2 (dinh dưỡng), 3 (điều phối)")
    @GetMapping("/doctors")
    @JsonView(Views.Public.class)
    public ResponseEntity getDoctorList(@RequestHeader("Authentication") long userId, @RequestParam(value = "type", required = false) String type) {
        Optional result;
        try {
            result = userRepository.findByRoleAndStatus(Integer.parseInt(type), Status.ACTIVATED);
        } catch (NumberFormatException e) {
            result = userRepository.findByStatus(Status.ACTIVATED);
        }
        if (result.isPresent())
            return new ResponseEntity(result.get(), HttpStatus.OK);
        else throw new NotFoundException(userId);
    }

    @ApiOperation(value = "Lấy thông tin chi tiết bác sĩ", notes = "Trả về thông tin chi tiết của một bác sĩ hoặc 404 NOT FOUND")
    @ApiParam(name = "id", value = "ID của bác sĩ", required = true, allowEmptyValue = false)
    @GetMapping("/doctors/{id}")
    @JsonView(Views.Private.class)
    public ResponseEntity getDoctorDetail(@RequestHeader("Authentication") long userId, @PathVariable(value = "id") long id) {
        Optional result = userRepository.findById(id);
        if (result.isPresent())
            return new ResponseEntity(result.get(), HttpStatus.OK);
        else throw new NotFoundException(userId);
    }
}
