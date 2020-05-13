package com.abclinic.server.controller;

import com.abclinic.server.annotation.authorized.Restricted;
import com.abclinic.server.common.base.CustomController;
import com.abclinic.server.common.base.Views;
import com.abclinic.server.common.utils.DateTimeUtils;
import com.abclinic.server.common.utils.StringUtils;
import com.abclinic.server.exception.BadRequestException;
import com.abclinic.server.model.dto.IndexResultRequestDto;
import com.abclinic.server.model.dto.IndexResultResponseDto;
import com.abclinic.server.model.entity.payload.health_index.HealthIndex;
import com.abclinic.server.model.entity.payload.health_index.HealthIndexField;
import com.abclinic.server.model.entity.payload.health_index.HealthIndexSchedule;
import com.abclinic.server.model.entity.payload.health_index.PatientHealthIndexField;
import com.abclinic.server.model.entity.user.*;
import com.abclinic.server.service.entity.HealthIndexService;
import com.abclinic.server.service.entity.PatientService;
import com.fasterxml.jackson.annotation.JsonView;
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
@RequestMapping("/health_index")
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
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "Tên chỉ số", required = true, dataType = "string", example = "Huyết áp"),
            @ApiImplicitParam(name = "description", value = "Mô tả chỉ số", required = true, dataType = "string", example = "Mô tả huyết áp"),
            @ApiImplicitParam(name = "fields", value = "Các trường của chỉ số", dataType = "array")
    })
    @JsonView(Views.Public.class)
    public ResponseEntity<HealthIndex> createIndex(@ApiIgnore @RequestAttribute("User") User user,
                                                   @RequestParam("name") String indexName,
                                                   @RequestParam("description") String indexDescription,
                                                   @RequestParam("fields") String[] fields) {
        if (!healthIndexService.isIndexExist(indexName)) {
            return new ResponseEntity<>(healthIndexService.createIndex(indexName, indexDescription, fields), HttpStatus.CREATED);
        } else throw new BadRequestException(user.getId(), "Chỉ số sức khỏe này đã tồn tại");
    }

    @PutMapping("")
    @Restricted(excluded = Patient.class)
    @ApiOperation(
            value = "Chỉnh sửa chỉ số sức khỏe",
            notes = "Trả về 200 OK hoặc 404 NOT FOUND",
            tags = "Nhân viên phòng khám"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Mã ID của chỉ số", required = true, dataType = "long", example = "1"),
            @ApiImplicitParam(name = "name", value = "Tên mới", required = true, dataType = "string", example = "Mỡ máu"),
            @ApiImplicitParam(name = "description", value = "Mô tả mới", required = true, dataType = "string", example = "Mô tả")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Chỉnh sửa thành công"),
            @ApiResponse(code = 404, message = "Mã ID không tồn tại")
    })
    @JsonView(Views.Public.class)
    public ResponseEntity<HealthIndex> editIndex(@ApiIgnore @RequestAttribute("User") User user,
                                                 @RequestParam("id") long indexId,
                                                 @RequestParam("name") String name,
                                                 @RequestParam("description") String description) {
        HealthIndex index = healthIndexService.getIndex(indexId);
        index.setName(name);
        index.setDescription(description);
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
            @ApiImplicitParam(name = "field", value = "Tên trường", required = true, dataType = "string", example = "Cân nặng")
    })
    @ApiResponses({
            @ApiResponse(code = 201, message = "Thêm thành công"),
            @ApiResponse(code = 400, message = "Trường này đã tồn tại"),
            @ApiResponse(code = 404, message = "Mã ID không tồn tại")
    })
    @JsonView(Views.Public.class)
    public ResponseEntity<HealthIndex> addField(@ApiIgnore @RequestAttribute("User") User user,
                                                @RequestParam("field") @Nullable String field,
                                                @PathVariable("id") long indexId) {
        HealthIndex index = healthIndexService.getIndex(indexId);
        if (!healthIndexService.isFieldExist(index, field)) {
            HealthIndexField f = new HealthIndexField(index, field);
            healthIndexService.save(f);
            return new ResponseEntity<>(healthIndexService.getIndex(index.getId()), HttpStatus.CREATED);
        } else throw new BadRequestException(user.getId(), "Trường này đã tồn tại");
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
            @ApiImplicitParam(name = "field-id", value = "Mã ID của trường", required = true, dataType = "long", example = "1")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Xóa thành công"),
            @ApiResponse(code = 404, message = "Mã ID không tồn tại")
    })
    @JsonView(Views.Public.class)
    public ResponseEntity<HealthIndex> deleteField(@ApiIgnore @RequestAttribute("User") User user,
                                                   @PathVariable("id") long indexId,
                                                   @RequestParam("field-id") long fieldId) {
        HealthIndex index = healthIndexService.getIndex(indexId);
        HealthIndexField field = healthIndexService.getField(fieldId);
        index.removeField(field);
        return new ResponseEntity<>((HealthIndex) healthIndexService.save(index), HttpStatus.OK);
    }

    @GetMapping("/schedule")
    @Restricted(excluded = Coordinator.class)
    @ApiOperation(
            value = "Lấy danh sách lịch gửi chỉ số sức khỏe",
            notes = "Trả về 200 OK hoặc 404 NOT FOUND",
            tags = {"Bệnh nhân", "Đa khoa", "Chuyên khoa", "Dinh dưỡng"}
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
                                                                     @RequestParam("search") String search,
                                                                     @RequestParam("page") int page,
                                                                     @RequestParam("size") int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("patient").ascending());
        if (StringUtils.isNull(search)) {
            return new ResponseEntity<>(healthIndexService.getScheduleList(user, pageable), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(healthIndexService.getScheduleList(user, search, pageable), HttpStatus.OK);
        }
    }

    @PostMapping("/schedule")
    @Restricted(included = {Specialist.class, Dietitian.class})
    @ApiOperation(
            value = "Tạp lịch gửi chỉ số sức khỏe",
            notes = "Trả về 201 CREATED hoặc 400 BAD REQUEST",
            tags = {"Chuyên khoa", "Dinh dưỡng"}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "patient-id", value = "Mã ID của bệnh nhân", required = true, dataType = "long", example = "5"),
            @ApiImplicitParam(name = "index-id", value = "Mã ID của chỉ số", required = true, dataType = "long", example = "1"),
            @ApiImplicitParam(name = "start", value = "Thời gian bắt đầu lập lịch (dd/MM/yyyy HH:mm:ss)", required = true, dataType = "string", example = "01/06/2020 12:30:00"),
            @ApiImplicitParam(name = "scheduled", value = "Chu kỳ của lịch, tính bằng giây", required = true, dataType = "long", example = "86400")
    })
    @ApiResponses({
            @ApiResponse(code = 201, message = "Tạo lịch thành công"),
            @ApiResponse(code = 400, message = "Format thời gian không hợp lệ"),
            @ApiResponse(code = 404, message = "Mã ID không hợp lệ")
    })
    @JsonView(Views.Private.class)
    public ResponseEntity<HealthIndexSchedule> createSchedule(@ApiIgnore @RequestAttribute("User") User user,
                                                              @RequestParam("patient-id") long patientId,
                                                              @RequestParam("index-id") long indexId,
                                                              @RequestParam("start") String start,
                                                              @RequestParam("scheduled") long scheduledTime) {
        try {
            LocalDateTime startedAt = DateTimeUtils.parseDateTime(start);
            Patient patient = patientService.getById(patientId);
            HealthIndex index = healthIndexService.getIndex(indexId);
            return new ResponseEntity<>(healthIndexService.createSchedule(patient, (Doctor) user, scheduledTime, startedAt, index), HttpStatus.CREATED);
        } catch (DateTimeParseException e) {
            throw new BadRequestException(user.getId(), "Format thời gian không hợp lệ");
        }
    }

    @GetMapping("/result")
    @Restricted(excluded = Coordinator.class)
    @ApiOperation(
            value = "Lấy danh sách kết quả chỉ số sức khỏe",
            notes = "Trả về 200 OK hoặc 404 NOT FOUND",
            tags = {"Bệnh nhân", "Đa khoa", "Chuyên khoa", "Dinh dưỡng"}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "search", value = "Filter lọc kết quả (ID bệnh nhân, tên bệnh nhân, ID lịch, ID chỉ số)", paramType = "query", example = "status=1,name=admin,"),
            @ApiImplicitParam(name = "page", value = "Số thứ tự trang", required = true, paramType = "query", allowableValues = "range[1, infinity]", example = "1"),
            @ApiImplicitParam(name = "size", value = "Kích thước trang", required = true, paramType = "query", example = "4")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Danh sách kết quả theo yêu cầu"),
            @ApiResponse(code = 404, message = "Không tìm thấy kết quả nào đúng yêu cầu")
    })
    @JsonView(Views.Abridged.class)
    public ResponseEntity<Page<PatientHealthIndexField>> getResultList(@ApiIgnore @RequestAttribute("User") User user,
                                                                       @RequestParam("search") String search,
                                                                       @RequestParam("page") int page,
                                                                       @RequestParam("size") int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("patient").ascending());
        if (StringUtils.isNull(search)) {
            return new ResponseEntity<>(healthIndexService.getValuesList(user, pageable), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(healthIndexService.getValuesList(user, search, pageable), HttpStatus.OK);
        }
    }

    @PostMapping("/result")
    @Restricted(included = Patient.class)
    @ApiOperation(
            value = "Tạo kết quả chỉ số sức khỏe",
            notes = "Trả về 202 CREATED hoặc 400 BAD REQUEST",
            tags = "Bệnh nhân"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "schedule-id", value = "Mã ID của lịch", required = true, dataType = "long", example = "1"),
            @ApiImplicitParam(name = "result", value = "Danh sách kết quả (fieldId, value)", required = true, dataType = "array")
    })
    @ApiResponses({
            @ApiResponse(code = 201, message = "Tạo kết quả thành công"),
            @ApiResponse(code = 400, message = "Số lượng giá trị không hợp lệ")
    })
    @JsonView(Views.Private.class)
    public ResponseEntity<IndexResultResponseDto> createResult(@ApiIgnore @RequestAttribute("User") User user,
                                                               @RequestParam("schedule-id") long scheduleId,
                                                               @RequestParam("result") List<IndexResultRequestDto> requestDtos) {
        HealthIndexSchedule schedule = healthIndexService.getSchedule(scheduleId);
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
