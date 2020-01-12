//package com.abclinic.server.controller.deprecated;
//
//import com.abclinic.server.base.BaseController;
//import com.abclinic.server.base.Views;
//import com.abclinic.server.constant.RecordType;
//import com.abclinic.server.exception.BadRequestException;
//import com.abclinic.server.exception.NotFoundException;
//import com.abclinic.server.model.dto.RecordDto;
//import com.abclinic.server.model.entity.record.DietRecord;
//import com.abclinic.server.model.entity.Disease;
//import com.abclinic.server.model.entity.record.MedicalRecord;
//import com.abclinic.server.model.entity.user.Patient;
//import com.abclinic.server.model.entity.user.Practitioner;
//import com.abclinic.server.model.entity.user.User;
//import com.fasterxml.jackson.annotation.JsonView;
//import io.swagger.annotations.*;
//import org.slf4j.LoggerFactory;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import springfox.documentation.annotations.ApiIgnore;
//
//import javax.annotation.Nullable;
//import java.util.List;
//import java.util.Optional;
//
///**
// * @author tmduc
// * @package com.abclinic.server.controller
// * @created 1/5/2020 9:52 AM
// */
//@RestController
//@Api(tags = "Điều phối viên")
//@RequestMapping("/admin/c")
//public class CoordinatorController extends BaseController {
//
//    @Override
//    public void init() {
//        this.logger = LoggerFactory.getLogger(CoordinatorController.class);
//    }
//
//    @GetMapping("/patients")
//    @ApiOperation(value = "Lọc và lấy danh sách bệnh nhân", notes = "Trả về danh sách bệnh nhân hoặc 404 NOT FOUND")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "status", value = "Trạng thái bệnh nhân (đang hoạt động, dừng hoạt động, đã xóa)", required = true, allowableValues = "0, 1, 2", dataType = "int", paramType = "query", example = "0"),
//            @ApiImplicitParam(name = "page", value = "Số thứ tự trang", required = true, paramType = "query", dataType = "int", allowableValues = "range[1, infinity]", example = "1"),
//            @ApiImplicitParam(name = "size", value = "Kích thước trang", required = true, paramType = "query", dataType = "int", example = "10")
//    })
//    @ApiResponses({
//            @ApiResponse(code = 200, message = "Danh sách bệnh nhân theo yêu cầu"),
//            @ApiResponse(code = 404, message = "Không tìm thấy bệnh nhân nào đúng yêu cầu")
//    })
//    @JsonView(Views.Abridged.class)
//    public ResponseEntity<List<Patient>> handleGetPatientList(@ApiIgnore @RequestAttribute("User") User user,
//                                                              @RequestParam("status") int status,
//                                                              @RequestParam("page") int page,
//                                                              @RequestParam("size") int size) {
//        Pageable pageable = PageRequest.of(page - 1, size);
//        Optional<List<Patient>> op = patientRepository.findAllByStatus(status, pageable);
//        if (op.isPresent())
//            return new ResponseEntity<>(op.get(), HttpStatus.OK);
//        else throw new NotFoundException(user.getId());
//    }
//
//    @GetMapping("/diseases")
//    @ApiOperation(value = "Lấy danh sách bệnh", notes = "Trả về danh sách bệnh hoặc 404 NOT FOUND")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "page", value = "Số thứ tự trang", paramType = "query", dataType = "int", allowableValues = "range[1, infinity]", example = "1"),
//            @ApiImplicitParam(name = "size", value = "Kích thước trang", paramType = "query", dataType = "int", example = "10")
//    })
//    @ApiResponses({
//            @ApiResponse(code = 200, message = "Danh sách bệnh"),
//            @ApiResponse(code = 404, message = "Không tìm thấy bệnh nào")
//    })
//    @JsonView(Views.Public.class)
//    public ResponseEntity<List<Disease>> handleGetDiseaseList(@ApiIgnore @RequestAttribute("User") User user,
//                                                              @RequestParam("page") Optional<Integer> page,
//                                                              @RequestParam("size") Optional<Integer> size) {
//        List<Disease> list;
//        if (page.isPresent() && size.isPresent()) {
//            Pageable pageable = PageRequest.of(page.get() - 1, size.get(), Sort.by("name").ascending());
//            list = diseaseRepository.findAll(pageable).getContent();
//        } else list = diseaseRepository.findAll();
//        if (list.size() > 0)
//            return new ResponseEntity<>(list, HttpStatus.OK);
//        else throw new NotFoundException(user.getId());
//    }
//
//    @GetMapping("/records")
//    @ApiOperation(value = "Lấy danh sách sổ y bạ", notes = "Trả về danh sách sổ y bạ hoặc 404 NOT FOUND")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "type", value = "Kiểu sổ y bạ (Sổ khám bệnh, Sổ dinh dưỡng)", required = true, dataType = "int", paramType = "query", allowableValues = "0, 1", example = "0"),
//            @ApiImplicitParam(name = "status", value = "Trạng thái sổ (Đang hoạt động, Dừng hoạt động, Đã xóa, Đang chờ duyệt)", required = true, dataType = "int", paramType = "query", allowableValues = "0, 1, 2, 3", example = "3"),
//            @ApiImplicitParam(name = "patient-name", value = "Tên bệnh nhân", dataType = "string", paramType = "query", example = "Nguyễn Văn A"),
//            @ApiImplicitParam(name = "disease-id", value = "Mã ID căn bệnh", dataType = "int", paramType = "query", example = "1"),
//            @ApiImplicitParam(name = "page", value = "Số thứ tự trang", paramType = "query", dataType = "int", allowableValues = "range[1, infinity]", example = "1"),
//            @ApiImplicitParam(name = "size", value = "Kích thước trang", paramType = "query", dataType = "int", example = "10")
//    })
//    @ApiResponses({
//            @ApiResponse(code = 200, message = "Danh sách sổ y bạ theo yêu cầu"),
//            @ApiResponse(code = 400, message = "Kiểu y bạ phải là 0 hoặc 1"),
//            @ApiResponse(code = 404, message = "Không tìm thấy sổ y bạ nào theo yêu cầu")
//    })
//    @JsonView(Views.Abridged.class)
//    public ResponseEntity handleGetRecordList(@ApiIgnore @RequestAttribute("User") User user,
//                                              @RequestParam("type") int type,
//                                              @RequestParam("status") int status,
//                                              @Nullable @RequestParam("patient-name") String patientName,
//                                              @Nullable @RequestParam("disease-id") Integer diseaseId,
//                                              @RequestParam("page") int page,
//                                              @RequestParam("size") int size) {
//        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
//        Disease disease = diseaseId == null ? null : diseaseRepository.findById(diseaseId);
//        Optional records = Optional.empty();
//        if (type == RecordType.MEDICAL.getValue()) {
//            records = medicalRecordRepository.findByPatientAndDisease(patientName, disease, status, pageable);
//        } else if (type == RecordType.DIET.getValue())
//            records = dietitianRecordRepository.findByPatientAndStatus(patientName, status, pageable);
//        else throw new BadRequestException(user.getId(), "Kiểu y bạ phải là 0 hoặc 1");
//        if (records.isPresent())
//            return new ResponseEntity(records.get(), HttpStatus.OK);
//        else throw new NotFoundException(user.getId());
//    }
//
//    @PostMapping("/records")
//    @ApiOperation(value = "Tạo sổ y bạ mới", notes = "Trả về sổ y bạ vừa tạo hoặc 400 BAD REQUEST")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "type", value = "Kiểu sổ y bạ (Sổ khám bệnh, Sổ dinh dưỡng)", required = true, dataType = "int", allowableValues = "0, 1", example = "0"),
//            @ApiImplicitParam(name = "patient-id", value = "Mã ID bệnh nhân", required = true, dataType = "int", example = "1"),
//            @ApiImplicitParam(name = "disease-id", value = "Mã ID căn bệnh", dataType = "int", example = "1"),
//            @ApiImplicitParam(name = "doctor-id", value = "Mã ID bác sĩ đa khoa", defaultValue = "0", dataType = "int", example = "1")
//    })
//    @ApiResponses({
//            @ApiResponse(code = 201, message = "Tạo sổ y bạ thành công"),
//            @ApiResponse(code = 400, message = "Người dùng không tồn tại"),
//            @ApiResponse(code = 400, message = "Căn bệnh không tồn tại"),
//            @ApiResponse(code = 400, message = "Kiểu y bạ phải là 0 hoặc 1")
//    })
//    @JsonView(Views.Abridged.class)
//    public ResponseEntity handleCreateRecord(@ApiIgnore @RequestAttribute("User") User user,
//                                             @RequestParam("type") int type,
//                                             @RequestParam("patient-id") int patientId,
//                                             @RequestParam("disease-id") int diseaseId,
//                                             @RequestParam(value = "doctor-id", defaultValue = "0") int doctorId) {
//        //TODO: Need to confirm about who making record
//        Patient patient = patientRepository.findById(patientId);
//        Practitioner doctor = practitionerRepository.findById(doctorId);
//        if (patient == null)
//            throw new BadRequestException(user.getId(), "Người dùng không tồn tại");
//        Disease disease = diseaseRepository.findById(diseaseId);
//        if (type == RecordType.MEDICAL.getValue()) {
//            MedicalRecord record = new MedicalRecord(patient, doctor, disease);
//            return new ResponseEntity(record, HttpStatus.CREATED);
//        } else if (type == RecordType.DIET.getValue()) {
//            DietRecord record = new DietRecord(patient, doctor);
//            return new ResponseEntity(record, HttpStatus.CREATED);
//        } else throw new BadRequestException(user.getId(), "Kiểu y bạ phải là 0 hoặc 1");
//    }
//
//    @PutMapping("/records")
//    @ApiOperation(value = "Chỉnh sửa thông tin cơ bản của sổ y bạ", notes = "Trả về sổ y bạ vừa chỉnh sửa hoặc 400 BAD REQUEST")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "record-id", value = "Mã ID của sổ y bạ", required = true, dataType = "int", example = "1"),
//            @ApiImplicitParam(name = "patient-id", value = "Mã ID bệnh nhân", required = true, dataType = "int", example = "1"),
//            @ApiImplicitParam(name = "disease-id", value = "Mã ID căn bệnh", dataType = "int", example = "1"),
//            @ApiImplicitParam(name = "doctor-id", value = "Mã ID bác sĩ đa khoa", required = true, dataType = "int", example = "1")
//    })
//    @ApiResponses({
//            @ApiResponse(code = 200, message = "Sổ y bạ chỉnh sửa thành công"),
//            @ApiResponse(code = 400, message = "Người dùng không tồn tại"),
//            @ApiResponse(code = 400, message = "Sổ y bạ không tồn tại")
//    })
//    @JsonView(Views.Abridged.class)
//    public ResponseEntity handleUpdateRecord(@ApiIgnore @RequestAttribute("User") User user,
//                                           @RequestParam("record-id") int recordId,
//                                           @RequestParam("patient-id") int patientId,
//                                           @RequestParam("disease-id") int diseaseId,
//                                           @RequestParam("doctor-id") int doctorId) {
//        Patient patient = patientRepository.findById(patientId);
//        Practitioner doctor = practitionerRepository.findById(doctorId);
//        if (patient == null || doctor == null)
//            throw new BadRequestException(user.getId(), "Người dùng không tồn tại");
//        Disease disease = diseaseRepository.findById(diseaseId);
////        Optional<MedicalRecord> medicalOptional = medicalRecordRepository.findById(recordId);
////        if (medicalOptional.isPresent()) {
////            MedicalRecord medicalRecord = medicalOptional.get();
////            medicalRecord.setDisease(disease);
////            medicalRecord.setPatient(patient);
////            medicalRecord.setPractitioner(doctor);
////            save(medicalRecord);
////            return new ResponseEntity(medicalRecord, HttpStatus.OK);
////        } else {
////            Optional<DietRecord> dietOptional = dietitianRecordRepository.findById(recordId);
////            if (dietOptional.isPresent()) {
////                DietRecord dietRecord = dietOptional.get();
////                dietRecord.setPatient(patient);
////                dietRecord.setPractitioner(doctor);
////                save(dietRecord);
////                return new ResponseEntity(dietRecord, HttpStatus.OK);
////            } else throw new BadRequestException(user.getId(), "Sổ y bạ không tồn tại");
////        }
//
//        RecordDto recordDto = recordFactory.getRecord(recordId);
//        try {
//            if (recordDto.getType() == RecordType.MEDICAL.getValue()) {
//                MedicalRecord medicalRecord = (MedicalRecord) recordDto.getRecord();
//                medicalRecord.setDisease(disease);
//                medicalRecord.setPatient(patient);
//                medicalRecord.setPractitioner(doctor);
//                save(medicalRecord);
//                return new ResponseEntity(medicalRecord, HttpStatus.OK);
//            } else {
//                DietRecord dietRecord = (DietRecord) recordDto.getRecord();
//                dietRecord.setPatient(patient);
//                dietRecord.setPractitioner(doctor);
//                save(dietRecord);
//                return new ResponseEntity(dietRecord, HttpStatus.OK);
//            }
//        } catch (NullPointerException e) {
//            throw new BadRequestException(user.getId(), "Sổ y bạ không tồn tại");
//        }
//    }
//}
