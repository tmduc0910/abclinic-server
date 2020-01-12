//package com.abclinic.server.controller.deprecated;
//
//import com.abclinic.server.base.BaseController;
//import com.abclinic.server.base.Views;
//import com.abclinic.server.constant.RecordType;
//import com.abclinic.server.exception.BadRequestException;
//import com.abclinic.server.exception.ForbiddenException;
//import com.abclinic.server.exception.NotFoundException;
//import com.abclinic.server.model.dto.RecordDto;
//import com.abclinic.server.model.entity.record.DietRecord;
//import com.abclinic.server.model.entity.Disease;
//import com.abclinic.server.model.entity.record.MedicalRecord;
//import com.abclinic.server.model.entity.user.*;
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
// * @created 1/6/2020 2:11 PM
// */
//@RestController
//@Api(tags = "Đa khoa")
//@RequestMapping("/admin/p")
//public class PractitionerController extends BaseController {
//
//    @Override
//    public void init() {
//        this.logger = LoggerFactory.getLogger(PractitionerController.class);
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
//    @JsonView({Views.Abridged.class})
//    public ResponseEntity<List<Patient>> handleGetPatientList(@ApiIgnore @RequestAttribute("User") User user,
//                                                              @RequestParam("status") int status,
//                                                              @RequestParam("page") int page,
//                                                              @RequestParam("size") int size) {
//        Pageable pageable = PageRequest.of(page-1, size, Sort.by("name").ascending());
//        Optional<List<Patient>> op = patientRepository.findAllByStatus(status, pageable);
//        if (op.isPresent())
//            return new ResponseEntity<>(op.get(), HttpStatus.OK);
//        else throw new NotFoundException(user.getId());
//    }
//
//    @GetMapping("/records")
//    @ApiOperation(value = "Lấy danh sách sổ y bạ do mình quản lý", notes = "Trả về danh sách sổ y bạ hoặc 404 NOT FOUND")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "type", value = "Kiểu sổ y bạ (Sổ khám bệnh, Sổ dinh dưỡng)", required = true, dataType = "int", paramType = "query", allowableValues = "0, 1", example = "0"),
//            @ApiImplicitParam(name = "status", value = "Trạng thái sổ (Đang hoạt động, Dừng hoạt động, Đã xóa, Đang chờ duyệt)", required = true, dataType = "int", paramType = "query", allowableValues = "0, 1, 2, 3", example = "3"),
//            @ApiImplicitParam(name = "patient-name", value = "Tên bệnh nhân", dataType = "string", paramType = "query", example = "Nguyễn Văn A"),
//            @ApiImplicitParam(name = "disease-id", value = "Mã ID căn bệnh", dataType = "int", paramType = "query", example = "0"),
//            @ApiImplicitParam(name = "page", value = "Số thứ tự trang", paramType = "query", dataType = "int", allowableValues = "range[1, infinity]", example = "1"),
//            @ApiImplicitParam(name = "size", value = "Kích thước trang", paramType = "query", dataType = "int", example = "10")
//    })
//    @ApiResponses({
//            @ApiResponse(code = 200, message = "Danh sách sổ y bạ theo yêu cầu"),
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
//        Practitioner practitioner = practitionerRepository.findById(user.getId());
//        Pageable pageable = PageRequest.of(page-1, size, Sort.by("createdAt").descending());
//        Disease disease = diseaseId == null ? null : diseaseRepository.findById(diseaseId);
//        Optional records = Optional.empty();
//        if (type == RecordType.MEDICAL.getValue()) {
//            records = medicalRecordRepository.findByPractitionerAndPatientAndDisease(practitioner, patientName, disease, status, pageable);
//        } else if (type == RecordType.DIET.getValue())
//            records = dietitianRecordRepository.findByPractitionerAndPatientAndStatus(practitioner, patientName, status, pageable);
//        if (records.isPresent())
//            return new ResponseEntity(records.get(), HttpStatus.OK);
//        else throw new NotFoundException(user.getId());
//    }
//
//    @GetMapping("/records/{record-id}")
//    @ApiOperation(value = "Lấy thông tin chi tiết của sổ y bạ", notes = "Trả về thông tin của sổ hoặc 403 FORBIDDEN hoặc 400 BAD REQUEST")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "record-id", value = "Mã ID của sổ", required = true, paramType = "path", dataType = "int", example = "1")
//    })
//    @ApiResponses({
//            @ApiResponse(code = 200, message = "Thông tin sổ y bạ theo yêu cầu"),
//            @ApiResponse(code = 400, message = "Sổ không tồn tại"),
//            @ApiResponse(code = 403, message = "Không thể xem thông tin sổ của bác sĩ đa khoa khác đảm nhiệm")
//    })
//    @JsonView(Views.Private.class)
//    public ResponseEntity handleGetRecordDetail(@ApiIgnore @RequestAttribute("User") User user,
//                                                @PathVariable("record-id") int recordId) {
//        Optional<MedicalRecord> medicalOptional = medicalRecordRepository.findById(recordId);
//        if (medicalOptional.isPresent()) {
//            MedicalRecord record = medicalOptional.get();
//            if (record.getPractitioner().getId() == user.getId() || record.getPractitioner() == null) {
//                return new ResponseEntity(record, HttpStatus.OK);
//            } else throw new ForbiddenException(user.getId(), "Không thể xem thông tin sổ của bác sĩ đa khoa khác đảm nhiệm");
//        } else {
//            Optional<DietRecord> dietitianOptional= dietitianRecordRepository.findById(recordId);
//            if (dietitianOptional.isPresent()) {
//                DietRecord record = dietitianOptional.get();
//                if (record.getPractitioner().getId() == user.getId() || record.getPractitioner() == null) {
//                    return new ResponseEntity(record, HttpStatus.OK);
//                } else throw new ForbiddenException(user.getId(), "Không thể xem thông tin sổ của bác sĩ đa khoa khác đảm nhiệm");
//            } else throw new BadRequestException(user.getId(), "Sổ không tồn tại");
//        }
//    }
//
//    @PutMapping("/records")
//    @JsonView(Views.Public.class)
//    public ResponseEntity handleUpdateRecord(@ApiIgnore @RequestAttribute("User") User user,
//                                             @RequestParam("record-id") int recordId,
//                                             @RequestParam(value = "doctor-id", defaultValue = "0") int doctorId,
//                                             @RequestParam("diagnose") String diagnose,
//                                             @RequestParam("prescription") String prescription,
//                                             @RequestParam("note") String note) {
//        //TODO: Need to confirm about can practitioner make diagnose v.v.
//        RecordDto recordDto = recordFactory.getRecord(recordId);
//        if (recordDto.getType() == RecordType.MEDICAL.getValue()) {
//            MedicalRecord medicalRecord = (MedicalRecord) recordDto.getRecord();
//            //TODO: Handle null params
//            Optional<Specialist> op = specialistRepository.findById(doctorId);
//            if (op.isPresent() || medicalRecord.getSpecialist() != null) {
//                medicalRecord.setSpecialist(op.get());
//                medicalRecord.setDiagnose(diagnose);
//                medicalRecord.setPrescription(prescription);
//                medicalRecord.setNote(note);
//                save(medicalRecord);
//                return new ResponseEntity(medicalRecord, HttpStatus.OK);
//            } else throw new BadRequestException(user.getId(), "Bác sĩ không tồn tại");
//        } else {
//            DietRecord dietRecord = (DietRecord) recordDto.getRecord();
//            Optional<Dietitian> op = dietitianRepository.findById(doctorId);
//            if (op.isPresent() || dietRecord.getDietitian() != null) {
//                dietRecord.setDietitian(op.get());
//                dietRecord.setPrescription(prescription);
//                dietRecord.setNote(note);
//                save(dietRecord);
//                return new ResponseEntity(dietRecord, HttpStatus.OK);
//            } else throw new BadRequestException(user.getId(), "Bác sĩ không tồn tại");
//        }
//    }
//}
