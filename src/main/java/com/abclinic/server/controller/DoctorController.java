package com.abclinic.server.controller;

import com.abclinic.server.base.BaseController;
import com.abclinic.server.base.Views;
import com.abclinic.server.constant.Role;
import com.abclinic.server.constant.Status;
import com.abclinic.server.exception.NotFoundException;
import com.abclinic.server.model.entity.user.User;
import com.abclinic.server.repository.*;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.*;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Optional;

/**
 * @author tmduc
 * @package com.abclinic.server.controller
 * @created 12/3/2019 2:59 PM
 */
@RestController
@Api(tags = "Nhân viên phòng khám")
@RequestMapping("/admin")
public class DoctorController extends BaseController {

    public DoctorController(UserRepository userRepository, PractitionerRepository practitionerRepository, PatientRepository patientRepository, CoordinatorRepository coordinatorRepository, DietitianRepository dietitianRepository, SpecialistRepository specialistRepository, AlbumRepository albumRepository, ImageRepository imageRepository, MedicalRecordRepository medicalRecordRepository, DietitianRecordRepository dietitianRecordRepository, QuestionRepository questionRepository, ReplyRepository replyRepository, SpecialtyRepository specialtyRepository, DiseaseRepository diseaseRepository) {
        super(userRepository, practitionerRepository, patientRepository, coordinatorRepository, dietitianRepository, specialistRepository, albumRepository, imageRepository, medicalRecordRepository, dietitianRecordRepository, questionRepository, replyRepository, specialtyRepository, diseaseRepository);
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

    @GetMapping("/doctors")
    @ApiOperation(value = "Lọc và lấy danh sách bác sĩ", notes = "Trả về danh sách các bác sĩ còn đang hoạt động hoặc nếu không tồn tại thì trả về 404 NOT FOUND")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "Loại bác sĩ, rỗng là lấy tất (đa khoa, chuyên khoa, dinh dưỡng, điều phối)", allowEmptyValue = true, required = false, allowableValues = "0, 1, 2, 3", dataType = "int", paramType = "query", example = "0"),
            @ApiImplicitParam(name = "page", value = "Số thứ tự trang", required = true, paramType = "query", allowableValues = "range[1, infinity]", example = "1"),
            @ApiImplicitParam(name = "size", value = "Kích thước trang", required = true, paramType = "query", example = "4")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Danh sách bác sĩ theo yêu cầu"),
            @ApiResponse(code = 403, message = "Bệnh nhân không được phép xem danh sách bác sĩ"),
            @ApiResponse(code = 404, message = "Không tìm thấy bác sĩ nào đúng yêu cầu")
    })
    @JsonView(Views.Public.class)
    public ResponseEntity getDoctorList(@ApiIgnore @RequestAttribute("User") User user,
                                        @RequestParam(value = "type", required = false) String type,
                                        @RequestParam("page") int page,
                                        @RequestParam("size") int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("role"));
        Optional result;
        try {
            result = userRepository.findByRoleAndStatus(Integer.parseInt(type), Status.ACTIVATED, pageable);
        } catch (NumberFormatException e) {
            result = userRepository.findAllByRoleIsLessThanAndStatus(Role.PATIENT.getValue(), Status.ACTIVATED, pageable);
        }
        if (result.isPresent())
            return new ResponseEntity(result.get(), HttpStatus.OK);
        else throw new NotFoundException(user.getId());
    }

    @GetMapping("/doctors/{id}")
    @ApiOperation(value = "Lấy thông tin chi tiết bác sĩ", notes = "Trả về thông tin chi tiết của một bác sĩ hoặc 404 NOT FOUND")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "ID của bác sĩ", required = true, allowEmptyValue = false, dataType = "long", paramType = "path", example = "1")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Thông tin chi tiết bác sĩ"),
            @ApiResponse(code = 404, message = "ID của bác sĩ không tồn tại")
    })
    @JsonView(Views.Public.class)
    public ResponseEntity getDoctorDetail(@ApiIgnore @RequestAttribute("User") User user,
                                          @PathVariable(value = "id") long id) {
        Optional result = userRepository.findById(id);
        if (result.isPresent())
            return new ResponseEntity(result.get(), HttpStatus.OK);
        else throw new NotFoundException(user.getId(), "Bác sĩ không tồn tại");
    }
}
