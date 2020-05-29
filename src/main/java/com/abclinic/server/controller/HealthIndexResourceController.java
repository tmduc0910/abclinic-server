package com.abclinic.server.controller;

import com.abclinic.server.annotation.authorized.Restricted;
import com.abclinic.server.common.base.CustomController;
import com.abclinic.server.common.base.Views;
import com.abclinic.server.common.constant.PayloadStatus;
import com.abclinic.server.common.utils.DateTimeUtils;
import com.abclinic.server.common.utils.StringUtils;
import com.abclinic.server.exception.BadRequestException;
import com.abclinic.server.exception.ForbiddenException;
import com.abclinic.server.model.dto.GetIndexResultResponseDto;
import com.abclinic.server.model.dto.IndexResultRequestDto;
import com.abclinic.server.model.dto.IndexResultResponseDto;
import com.abclinic.server.model.dto.PageDto;
import com.abclinic.server.model.dto.request.delete.RequestDeleteDto;
import com.abclinic.server.model.dto.request.post.RequestCreateHealthIndexDto;
import com.abclinic.server.model.dto.request.post.RequestCreateHealthIndexFieldDto;
import com.abclinic.server.model.dto.request.post.RequestCreateHealthIndexResultDto;
import com.abclinic.server.model.dto.request.post.RequestCreateHealthIndexScheduleDto;
import com.abclinic.server.model.dto.request.put.RequestUpdateHealthIndexDto;
import com.abclinic.server.model.entity.payload.health_index.HealthIndex;
import com.abclinic.server.model.entity.payload.health_index.HealthIndexField;
import com.abclinic.server.model.entity.payload.health_index.HealthIndexSchedule;
import com.abclinic.server.model.entity.payload.health_index.PatientHealthIndexField;
import com.abclinic.server.model.entity.user.*;
import com.abclinic.server.serializer.ViewSerializer;
import com.abclinic.server.service.entity.HealthIndexService;
import com.abclinic.server.service.entity.PatientService;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author tmduc
 * @package com.abclinic.server.controller
 * @created 5/8/2020 2:35 PM
 */
@RestController
@RequestMapping("/health_indexes")
public class HealthIndexResourceController extends CustomController {
    @Autowired
    private HealthIndexService healthIndexService;

    @Autowired
    private PatientService patientService;

    @Override
    public void init() {
        this.logger = LoggerFactory.getLogger(HealthIndexResourceController.class);
    }

    @GetMapping("")
    @Restricted(excluded = Patient.class)
    @ApiOperation(
            value = "Lấy danh sách chỉ số sức khỏe",
            notes = "Trả về 200 OK hoặc 404 NOT FOUND",
            tags = "Nhân viên phòng khám"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "Số thứ tự trang", required = true, paramType = "query", allowableValues = "range[1, infinity]", example = "1"),
            @ApiImplicitParam(name = "size", value = "Kích thước trang", required = true, paramType = "query", example = "4")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Danh sách chỉ số sức khỏe"),
            @ApiResponse(code = 404, message = "Không tìm thấy chỉ số nào")
    })
    @JsonView(Views.Abridged.class)
    public ResponseEntity<Page<HealthIndex>> getIndexList(@ApiIgnore @RequestAttribute("User") User user,
                                                          @RequestParam("page") int page,
                                                          @RequestParam("size") int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("name").ascending());
        return new ResponseEntity<>(healthIndexService.getIndexList(pageable), HttpStatus.OK);
    }

    @PostMapping("")
    @Restricted(excluded = Patient.class)
    @ApiOperation(
            value = "Tạo chỉ số sức khỏe mới",
            notes = "Trả về 201 CREATED hoặc 400 BAD REQUEST",
            tags = "Nhân viên phòng khám"
    )
    @ApiResponses({
            @ApiResponse(code = 201, message = "Tạo mới thành công"),
            @ApiResponse(code = 400, message = "Chỉ số sức khỏe này đã tồn tại")
    })
    @JsonView(Views.Public.class)
    public ResponseEntity<HealthIndex> createIndex(@ApiIgnore @RequestAttribute("User") User user,
                                                   @RequestBody RequestCreateHealthIndexDto requestCreateHealthIndexDto) {
        if (!healthIndexService.isIndexExist(requestCreateHealthIndexDto.getName())) {
            return new ResponseEntity<>(healthIndexService.createIndex(
                    requestCreateHealthIndexDto.getName(),
                    requestCreateHealthIndexDto.getDescription(),
                    requestCreateHealthIndexDto.getFields()), HttpStatus.CREATED);
        } else throw new BadRequestException(user.getId(), "Chỉ số sức khỏe này đã tồn tại");
    }

    @PutMapping("")
    @Restricted(excluded = Patient.class)
    @ApiOperation(
            value = "Chỉnh sửa chỉ số sức khỏe",
            notes = "Trả về 200 OK hoặc 404 NOT FOUND",
            tags = "Nhân viên phòng khám"
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "Chỉnh sửa thành công"),
            @ApiResponse(code = 404, message = "Mã ID không tồn tại")
    })
    @JsonView(Views.Public.class)
    public ResponseEntity<HealthIndex> editIndex(@ApiIgnore @RequestAttribute("User") User user,
                                                 @RequestBody RequestUpdateHealthIndexDto requestUpdateHealthIndexDto) {
        HealthIndex index = healthIndexService.getIndex(requestUpdateHealthIndexDto.getId());
        index.setName(requestUpdateHealthIndexDto.getName());
        index.setDescription(requestUpdateHealthIndexDto.getDescription());
        return new ResponseEntity<>((HealthIndex) healthIndexService.save(index), HttpStatus.OK);
    }

    @PostMapping("/{id}/field")
    @Restricted(excluded = Patient.class)
    @ApiOperation(
            value = "Thêm trường cho chỉ số sức khỏe",
            notes = "Trả về 201 CREATED hoặc 400 BAD REQUEST hoặc 404 NOT FOUND",
            tags = "Nhân viên phòng khám"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Mã ID của chỉ số", required = true, dataType = "long", paramType = "path", example = "1"),
    })
    @ApiResponses({
            @ApiResponse(code = 201, message = "Thêm thành công"),
            @ApiResponse(code = 400, message = "Trường này đã tồn tại"),
            @ApiResponse(code = 404, message = "Mã ID không tồn tại")
    })
    @JsonView(Views.Public.class)
    public ResponseEntity<HealthIndex> addField(@ApiIgnore @RequestAttribute("User") User user,
                                                @RequestBody RequestCreateHealthIndexFieldDto requestCreateHealthIndexFieldDto,
                                                @PathVariable("id") long indexId) {
        HealthIndex index = healthIndexService.getIndex(indexId);
        String fieldName = requestCreateHealthIndexFieldDto.getField();
        if (!healthIndexService.isFieldExist(index, fieldName)) {
            HealthIndexField f = healthIndexService.getOldField(fieldName);
            if (f == null)
                f = new HealthIndexField(index, requestCreateHealthIndexFieldDto.getField());
            else index.addField(f);
            healthIndexService.save(f);
            return new ResponseEntity<>(healthIndexService.getIndex(index.getId()), HttpStatus.CREATED);
        } else {
            throw new BadRequestException(user.getId(), "Trường này đã tồn tại");
        }
    }

    @DeleteMapping("/{id}/field")
    @Restricted(excluded = Patient.class)
    @ApiOperation(
            value = "Xóa trường của chỉ số sức khỏe",
            notes = "Trả về 200 OK hoặc 404 NOT FOUND",
            tags = "Nhân viên phòng khám"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Mã ID của chỉ số", required = true, dataType = "long", paramType = "path", example = "1"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Xóa thành công"),
            @ApiResponse(code = 404, message = "Mã ID không tồn tại")
    })
    @JsonView(Views.Public.class)
    public ResponseEntity<HealthIndex> deleteField(@ApiIgnore @RequestAttribute("User") User user,
                                                   @PathVariable("id") long indexId,
                                                   @RequestBody RequestDeleteDto requestDeleteDto) {
        HealthIndex index = healthIndexService.getIndex(indexId);
        HealthIndexField field = healthIndexService.getField(requestDeleteDto.getId());
        index.removeField(field);
        return new ResponseEntity<>((HealthIndex) healthIndexService.save(index), HttpStatus.OK);
    }

    @GetMapping("/schedule")
    @Restricted(excluded = {Coordinator.class, Practitioner.class})
    @ApiOperation(
            value = "Lấy danh sách lịch gửi chỉ số sức khỏe",
            notes = "Trả về 200 OK hoặc 404 NOT FOUND",
            tags = {"Bệnh nhân", "Chuyên khoa", "Dinh dưỡng"}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "search", value = "Filter lọc lịch (ID bệnh nhân, tên bệnh nhân, status)", paramType = "query", example = "status=1,name=admin,"),
            @ApiImplicitParam(name = "page", value = "Số thứ tự trang", required = true, paramType = "query", allowableValues = "range[1, infinity]", example = "1"),
            @ApiImplicitParam(name = "size", value = "Kích thước trang", required = true, paramType = "query", example = "4")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Danh sách lịch theo yêu cầu"),
            @ApiResponse(code = 404, message = "Không tìm thấy lịch nào đúng yêu cầu")
    })
    @JsonView(Views.Abridged.class)
    public ResponseEntity<Page<HealthIndexSchedule>> getScheduleList(@ApiIgnore @RequestAttribute("User") User user,
                                                                     @RequestParam("search") @Nullable String search,
                                                                     @RequestParam("page") int page,
                                                                     @RequestParam("size") int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("patient").ascending());
        if (StringUtils.isNull(search)) {
            return new ResponseEntity<>(healthIndexService.getScheduleList(user, pageable), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(healthIndexService.getScheduleList(user, search, pageable), HttpStatus.OK);
        }
    }

    @GetMapping("/schedule/{id}")
    @Restricted(excluded = Coordinator.class)
    @ApiOperation(
            value = "Lấy thông tin chi tiết lịch nhắc nhở",
            notes = "Trả về 200 OK hoặc 403 FORBIDDEN",
            tags = {"Đa khoa", "Bệnh nhân", "Chuyên khoa", "Dinh dưỡng"}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Mã ID của lịch", required = true, paramType = "path", dataType = "long", example = "1")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Thông tin lịch nhắc nhở"),
            @ApiResponse(code = 403, message = "Không có quyền truy cập vào lịch này")
    })
    @JsonView(Views.Public.class)
    public ResponseEntity<HealthIndexSchedule> getScheduleDetail(@ApiIgnore @RequestAttribute("User") User user,
                                                                 @PathVariable("id") long id) {
        HealthIndexSchedule schedule = healthIndexService.getSchedule(id);
        if (schedule.getPatient().equals(user) || schedule.getDoctor().equals(user))
            return new ResponseEntity<>(schedule, HttpStatus.OK);
        else throw new ForbiddenException(user.getId(), "Không có quyền truy cập vào lịch này");
    }

    @PostMapping("/schedule")
    @Restricted(included = {Practitioner.class, Specialist.class, Dietitian.class})
    @ApiOperation(
            value = "Tạp lịch gửi chỉ số sức khỏe",
            notes = "Trả về 201 CREATED hoặc 400 BAD REQUEST",
            tags = {"Đa khoa", "Chuyên khoa", "Dinh dưỡng"}
    )
    @ApiResponses({
            @ApiResponse(code = 201, message = "Tạo lịch thành công"),
            @ApiResponse(code = 400, message = "Format thời gian không hợp lệ"),
            @ApiResponse(code = 404, message = "Mã ID không hợp lệ")
    })
    @JsonView(Views.Private.class)
    public ResponseEntity<HealthIndexSchedule> createSchedule(@ApiIgnore @RequestAttribute("User") User user,
                                                              @RequestBody RequestCreateHealthIndexScheduleDto requestCreateHealthIndexScheduleDto) {
        try {
            LocalDateTime startedAt = DateTimeUtils.parseDateTime(requestCreateHealthIndexScheduleDto.getStart());
            Patient patient = patientService.getById(requestCreateHealthIndexScheduleDto.getPatientId());
            HealthIndex index = healthIndexService.getIndex(requestCreateHealthIndexScheduleDto.getIndexId());
            return new ResponseEntity<>(healthIndexService.createSchedule(patient, (Doctor) user, requestCreateHealthIndexScheduleDto.getScheduledTime(), startedAt, index), HttpStatus.CREATED);
        } catch (DateTimeParseException e) {
            throw new BadRequestException(user.getId(), "Format thời gian không hợp lệ");
        }
    }

    @DeleteMapping("/schedule")
    @Restricted(included = {Practitioner.class, Specialist.class, Dietitian.class})
    @ApiOperation(
            value = "Xóa lịch hẹn chỉ số sức khỏe",
            notes = "Trả về 200 OK hoặc 403 FORBIDDEN",
            tags = {"Đa khoa", "Chuyên khoa", "Dinh dưỡng"}
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "Xóa thành công"),
            @ApiResponse(code = 403, message = "Không có quyền truy cập vào lịch này")
    })
    public ResponseEntity deleteSchedule(@ApiIgnore @RequestAttribute("User") User user,
                                         @RequestBody RequestDeleteDto requestDeleteDto) {
        HealthIndexSchedule schedule = healthIndexService.getSchedule(requestDeleteDto.getId());
        if (schedule.getDoctor().equals(user)) {
            schedule.setStatus(PayloadStatus.ON_HOLD);
            schedule.setStartedAt(null);
            schedule.setEndedAt(null);
            healthIndexService.save(schedule);
            return new ResponseEntity(HttpStatus.OK);
        } else throw new ForbiddenException(user.getId(), "Không có quyền truy cập vào lịch này");
    }

    @GetMapping("/result")
    @Restricted(excluded = Coordinator.class)
    @ApiOperation(
            value = "Lấy danh sách kết quả chỉ số sức khỏe",
            notes = "Trả về 200 OK hoặc 404 NOT FOUND",
            tags = {"Đa khoa", "Bệnh nhân", "Chuyên khoa", "Dinh dưỡng"}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "search", value = "Filter lọc kết quả (patient_id, patient_name, schedule_id, index_id, index_name)", paramType = "query", example = "status=1,name=admin,"),
            @ApiImplicitParam(name = "page", value = "Số thứ tự trang", required = true, paramType = "query", allowableValues = "range[1, infinity]", example = "1"),
            @ApiImplicitParam(name = "size", value = "Kích thước trang", required = true, paramType = "query", example = "4")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Danh sách kết quả theo yêu cầu"),
            @ApiResponse(code = 404, message = "Không tìm thấy kết quả nào đúng yêu cầu")
    })
    @JsonView(Views.Abridged.class)
    public ResponseEntity<PageDto<GetIndexResultResponseDto>> getResultList(@ApiIgnore @RequestAttribute("User") User user,
                                                                            @RequestParam("search") @Nullable String search,
                                                                            @RequestParam("page") int page,
                                                                            @RequestParam("size") int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        if (StringUtils.isNull(search)) {
            return new ResponseEntity<>(healthIndexService.getValuesList(user, pageable), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(healthIndexService.getValuesList(user, search, pageable), HttpStatus.OK);
        }
    }

    @GetMapping("/result/{id}")
    @Restricted(included = {Practitioner.class, Specialist.class, Dietitian.class})
    @ApiOperation(
            value = "Lấy danh sách kết quả thông số sức khỏe theo ID kết quả",
            notes = "Trả về 200 OK hoặc 404 NOT FOUND",
            tags = {"Đa khoa", "Chuyên khoa", "Dinh dưỡng"}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "ID của kết quả", required = true, paramType = "path", dataType = "long", example = "1")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Danh sách kết quả"),
            @ApiResponse(code = 404, message = "Mã ID không tồn tại")
    })
    @JsonView(Views.Public.class)
    @JsonSerialize(using = ViewSerializer.class)
    public ResponseEntity<List<PatientHealthIndexField>> getResultList(@ApiIgnore @RequestAttribute("User") User user,
                                                                       @PathVariable("id") long id) {
        return new ResponseEntity<>(healthIndexService.getValuesList(user, id), HttpStatus.OK);
    }

    @PostMapping("/result")
    @Restricted(included = Patient.class)
    @ApiOperation(
            value = "Tạo kết quả chỉ số sức khỏe",
            notes = "Trả về 202 CREATED hoặc 400 BAD REQUEST",
            tags = "Bệnh nhân"
    )
    @ApiResponses({
            @ApiResponse(code = 201, message = "Tạo kết quả thành công"),
            @ApiResponse(code = 400, message = "Số lượng giá trị không hợp lệ")
    })
    @JsonView(Views.Private.class)
    public ResponseEntity<IndexResultResponseDto> createResult(@ApiIgnore @RequestAttribute("User") User user,
                                                               @RequestBody RequestCreateHealthIndexResultDto requestCreateHealthIndexResultDto) {
        HealthIndexSchedule schedule = healthIndexService.getSchedule(requestCreateHealthIndexResultDto.getScheduleId());
        List<IndexResultRequestDto> requestDtos = requestCreateHealthIndexResultDto.getResults();
        if (schedule.getIndex().getFields().size() == requestDtos.size()) {
            List<PatientHealthIndexField> values =
                    healthIndexService.createResults(schedule,
                            requestDtos.stream()
                                    .map(r -> healthIndexService.getField(r.getFieldId()))
                                    .collect(Collectors.toList()),
                            requestDtos.stream()
                                    .map(IndexResultRequestDto::getValue)
                                    .collect(Collectors.toList()));
            return new ResponseEntity<>(new IndexResultResponseDto(schedule, values, DateTimeUtils.getCurrent()), HttpStatus.CREATED);
        } else throw new BadRequestException(user.getId(), "Số lượng giá trị không hợp lệ");
    }
}
