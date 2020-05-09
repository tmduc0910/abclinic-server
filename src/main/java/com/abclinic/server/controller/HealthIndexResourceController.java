package com.abclinic.server.controller;

import com.abclinic.server.annotation.authorized.Restricted;
import com.abclinic.server.common.base.CustomController;
import com.abclinic.server.common.base.Views;
import com.abclinic.server.common.utils.StringUtils;
import com.abclinic.server.exception.BadRequestException;
import com.abclinic.server.model.entity.payload.health_index.HealthIndex;
import com.abclinic.server.model.entity.payload.health_index.HealthIndexField;
import com.abclinic.server.model.entity.payload.health_index.HealthIndexSchedule;
import com.abclinic.server.model.entity.user.Coordinator;
import com.abclinic.server.model.entity.user.Patient;
import com.abclinic.server.model.entity.user.User;
import com.abclinic.server.service.entity.HealthIndexService;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.*;
import org.mortbay.util.StringUtil;
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
import javax.swing.text.View;

/**
 * @author tmduc
 * @package com.abclinic.server.controller
 * @created 5/8/2020 2:35 PM
 */
@RestController
@RequestMapping("/health_index")
public class HealthIndexResourceController extends CustomController {
    @Autowired
    private HealthIndexService service;

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
        return new ResponseEntity<>(service.getIndexList(pageable), HttpStatus.OK);
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
        if (!service.isIndexExist(indexName)) {
            return new ResponseEntity<>(service.createIndex(indexName, indexDescription, fields), HttpStatus.CREATED);
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
            @ApiImplicitParam(name = "name", value = "Tên mới", dataType = "string", example = "Mỡ máu"),
            @ApiImplicitParam(name = "description", value = "Mô tả mới", dataType = "string", example = "Mô tả")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Chỉnh sửa thành công"),
            @ApiResponse(code = 404, message = "Mã ID không tồn tại")
    })
    @JsonView(Views.Public.class)
    public ResponseEntity<HealthIndex> editIndex(@ApiIgnore @RequestAttribute("User") User user,
                                                 @RequestParam("id") long indexId,
                                                 @RequestParam("name") @Nullable String name,
                                                 @RequestParam("description") @Nullable String description) {
        HealthIndex index = service.getIndex(indexId);
        if (!StringUtils.isNull(name)) {
            index.setName(name);
        }
        if (!StringUtils.isNull(description)) {
            index.setDescription(description);
        }
        return new ResponseEntity<>((HealthIndex) service.save(index), HttpStatus.OK);
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
        HealthIndex index = service.getIndex(indexId);
        if (!service.isFieldExist(index, field)) {
            HealthIndexField f = new HealthIndexField(index, field);
            service.save(f);
            return new ResponseEntity<>(service.getIndex(index.getId()), HttpStatus.CREATED);
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
        HealthIndex index = service.getIndex(indexId);
        HealthIndexField field = service.getField(fieldId);
        index.removeField(field);
        return new ResponseEntity<>((HealthIndex) service.save(index), HttpStatus.OK);
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
            @ApiResponse(code = 200, message = "Danh sách bệnh nhân theo yêu cầu"),
            @ApiResponse(code = 404, message = "Không tìm thấy lịch nào đúng yêu cầu")
    })
    @JsonView(Views.Abridged.class)
    public ResponseEntity<Page<HealthIndexSchedule>> getScheduleList(@ApiIgnore @RequestAttribute("User") User user,
                                                                     @RequestParam("search") String search,
                                                                     @RequestParam("page") int page,
                                                                     @RequestParam("size") int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("patient").ascending());
        if (StringUtils.isNull(search)) {
            return new ResponseEntity<>(service.getScheduleList(user, pageable), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(service.getScheduleList(user, search, pageable), HttpStatus.OK);
        }
    }
}
