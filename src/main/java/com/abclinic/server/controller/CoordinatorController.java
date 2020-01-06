package com.abclinic.server.controller;

import com.abclinic.server.base.BaseController;
import com.abclinic.server.base.Views;
import com.abclinic.server.constant.RecordType;
import com.abclinic.server.exception.NotFoundException;
import com.abclinic.server.model.entity.Disease;
import com.abclinic.server.model.entity.user.Patient;
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

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

/**
 * @author tmduc
 * @package com.abclinic.server.controller
 * @created 1/5/2020 9:52 AM
 */
@RestController
@Api(tags = "Điều phối viên")
@RequestMapping("/admin/c")
public class CoordinatorController extends BaseController {

    public CoordinatorController(UserRepository userRepository, PractitionerRepository practitionerRepository, PatientRepository patientRepository, CoordinatorRepository coordinatorRepository, DietitianRepository dietitianRepository, SpecialistRepository specialistRepository, AlbumRepository albumRepository, ImageRepository imageRepository, MedicalRecordRepository medicalRecordRepository, DietitianRecordRepository dietitianRecordRepository, QuestionRepository questionRepository, ReplyRepository replyRepository, SpecialtyRepository specialtyRepository, DiseaseRepository diseaseRepository) {
        super(userRepository, practitionerRepository, patientRepository, coordinatorRepository, dietitianRepository, specialistRepository, albumRepository, imageRepository, medicalRecordRepository, dietitianRecordRepository, questionRepository, replyRepository, specialtyRepository, diseaseRepository);
    }

    @Override
    public void init() {
        this.logger = LoggerFactory.getLogger(CoordinatorController.class);
    }

    @GetMapping("/patients")
    @ApiOperation(value = "Lọc và lấy danh sách bệnh nhân", notes = "Trả về danh sách bệnh nhân hoặc 404 NOT FOUND")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "status", value = "Trạng thái bệnh nhân (đang hoạt động, dừng hoạt động, đã xóa)", required = true, allowableValues = "0, 1, 2", dataType = "int", paramType = "query", example = "0"),
            @ApiImplicitParam(name = "page", value = "Số thứ tự trang", required = true, paramType = "query", dataType = "int", allowableValues = "range[1, infinity]", example = "1"),
            @ApiImplicitParam(name = "size", value = "Kích thước trang", required = true, paramType = "query", dataType = "int", example = "10")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Danh sách bệnh nhân theo yêu cầu"),
            @ApiResponse(code = 404, message = "Không tìm thấy bệnh nhân nào đúng yêu cầu")
    })
    @JsonView(Views.Public.class)
    public ResponseEntity<List<Patient>> handleGetPatientList(@ApiIgnore @RequestAttribute("User") User user,
                                                              @RequestParam("status") int status,
                                                              @RequestParam("page") int page,
                                                              @RequestParam("size") int size) {
        Pageable pageable = PageRequest.of(page-1, size);
        Optional<List<Patient>> op = patientRepository.findAllByStatus(status, pageable);
        if (op.isPresent())
            return new ResponseEntity<>(op.get(), HttpStatus.OK);
        else throw new NotFoundException(user.getId());
    }

    @GetMapping("/diseases")
    @ApiOperation(value = "Lấy danh sách bệnh", notes = "Trả về danh sách bệnh hoặc 404 NOT FOUND")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "Số thứ tự trang", paramType = "query", dataType = "int", allowableValues = "range[1, infinity]", example = "1"),
            @ApiImplicitParam(name = "size", value = "Kích thước trang", paramType = "query", dataType = "int", example = "10")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Danh sách bệnh"),
            @ApiResponse(code = 404, message = "Không tìm thấy bệnh nào")
    })
    @JsonView(Views.Public.class)
    public ResponseEntity<List<Disease>> handleGetDiseaseList(@ApiIgnore @RequestAttribute("User") User user,
                                                              @RequestParam("page") Optional<Integer> page,
                                                              @RequestParam("size") Optional<Integer> size) {
        List<Disease> list;
        if (page.isPresent() && size.isPresent()) {
            Pageable pageable = PageRequest.of(page.get()-1, size.get(), Sort.by("name").ascending());
            list = diseaseRepository.findAll(pageable).getContent();
        } else list = diseaseRepository.findAll();
        if (list.size() > 0)
            return new ResponseEntity<>(list, HttpStatus.OK);
        else throw new NotFoundException(user.getId());
    }

    @GetMapping("/records")
    @ApiOperation(value = "Lấy danh sách sổ y bạ", notes = "Trả về danh sách sổ y bạ hoặc 404 NOT FOUND")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "Kiểu sổ y bạ (Sổ khám bệnh, Sổ dinh dưỡng)", required = true, dataType = "int", paramType = "query", allowableValues = "0, 1", example = "0"),
            @ApiImplicitParam(name = "status", value = "Trạng thái sổ (Đang hoạt động, Dừng hoạt động, Đã xóa, Đang chờ duyệt)", required = true, dataType = "int", paramType = "query", allowableValues = "0, 1, 2, 3", example = "3"),
            @ApiImplicitParam(name = "patient-name", value = "Tên bệnh nhân", dataType = "string", paramType = "query", example = "Nguyễn Văn A"),
            @ApiImplicitParam(name = "disease-id", value = "Mã ID căn bệnh", dataType = "int", paramType = "query", example = "0"),
            @ApiImplicitParam(name = "page", value = "Số thứ tự trang", paramType = "query", dataType = "int", allowableValues = "range[1, infinity]", example = "1"),
            @ApiImplicitParam(name = "size", value = "Kích thước trang", paramType = "query", dataType = "int", example = "10")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Danh sách sổ y bạ theo yêu cầu"),
            @ApiResponse(code = 404, message = "Không tìm thấy sổ y bạ nào theo yêu cầu")
    })
    @JsonView(Views.Public.class)
    public ResponseEntity handleGetRecordList(@ApiIgnore @RequestAttribute("User") User user,
                                              @RequestParam("type") int type,
                                              @RequestParam("status") int status,
                                              @Nullable @RequestParam("patient-name") String patientName,
                                              @Nullable @RequestParam("disease-id") Integer diseaseId,
                                              @RequestParam("page") int page,
                                              @RequestParam("size") int size) {
        Pageable pageable = PageRequest.of(page-1, size, Sort.by("createdAt").descending());
        Disease disease = diseaseId == null ? null : diseaseRepository.findById(diseaseId);
        Optional records = Optional.empty();
        if (type == RecordType.MEDICAL.getValue()) {
             records = medicalRecordRepository.findByPatientAndDisease(patientName, disease, status, pageable);
        } else if (type == RecordType.DIET.getValue())
            records = dietitianRecordRepository.findByPatientAndStatus(patientName, status, pageable);
        if (records.isPresent())
            return new ResponseEntity(records.get(), HttpStatus.OK);
        else throw new NotFoundException(user.getId());
    }

    @PostMapping("/records")
    @JsonView(Views.Public.class)
    public ResponseEntity handleCreateRecord(@ApiIgnore @RequestAttribute("User") User user,
                                             @RequestParam("type") int type,
                                             @RequestParam("patient-id") int patientId,
                                             @RequestParam("disease-id") int diseaseId,
                                             @RequestParam("doctor-id") int doctorId) {
        //TODO: Need to confirm about who making record
        return null;
    }
}
