package com.abclinic.server.controller;

import com.abclinic.server.annotation.authorized.Restricted;
import com.abclinic.server.common.base.CustomController;
import com.abclinic.server.model.entity.Specialty;
import com.abclinic.server.model.entity.user.Coordinator;
import com.abclinic.server.model.entity.user.Patient;
import com.abclinic.server.model.entity.user.User;
import com.abclinic.server.service.entity.SpecialtyService;
import io.swagger.annotations.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * @author tmduc
 * @package com.abclinic.server.controller
 * @created 2/7/2020 2:49 PM
 */
@RestController
public class SpecialtyResourceController extends CustomController {

    @Autowired
    private SpecialtyService specialtyService;

    @Override
    public void init() {
        this.logger = LoggerFactory.getLogger(SpecialtyResourceController.class);
    }

    @GetMapping("/specialties")
    @Restricted(excluded = Patient.class)
    @ApiOperation(
            value = "Lấy danh sách các chuyên môn",
            notes = "Trả về danh sách chuyên môn",
            tags = "Nhân viên phòng khám"
    )
    public ResponseEntity<List<Specialty>> getSpecialties(@ApiIgnore @RequestAttribute("User") User user) {
        return new ResponseEntity<>(specialtyService.getAll(), HttpStatus.OK);
    }

    @PostMapping("/specialties")
    @ApiOperation(
            value = "Tạo một chuyên môn mới",
            notes = "Trả về 201 CREATED",
            tags = "Điều phối viên"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "Tên chuyên môn", required = true, dataType = "string"),
            @ApiImplicitParam(name = "detail", value = "Mô tả chuyên môn", required = true, dataType = "string")
    })
    @Restricted(included = Coordinator.class)
    public ResponseEntity<Specialty> addSpecialty(@RequestParam("name") String name,
                                                  @RequestParam("detail") String detail) {
        Specialty specialty = new Specialty(name, detail);
        specialty = specialtyService.save(specialty);
        return new ResponseEntity<>(specialty, HttpStatus.CREATED);
    }

    @PutMapping("/specialties")
    @Restricted(included = Coordinator.class)
    @ApiOperation(
            value = "Sửa đổi thông tin chuyên môn",
            notes = "Trả về 200 OK hoặc 404 NOT FOUND",
            tags = "Điều phối viên"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Mã ID của chuyên môn", required = true, dataType = "int", example = "1"),
            @ApiImplicitParam(name = "name", value = "Tên chuyên môn", required = true, dataType = "string"),
            @ApiImplicitParam(name = "detail", value = "Mô tả chuyên môn", required = true, dataType = "string")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Sửa thành công"),
            @ApiResponse(code = 404, message = "Không tìm thấy chuyên môn theo yêu cầu")
    })
    public ResponseEntity editSpecialty(@ApiIgnore @RequestAttribute("User") User user,
                                        @RequestParam("id") long id,
                                        @RequestParam("name") String name,
                                        @RequestParam("detail") String detail) {
        Specialty specialty = specialtyService.getById(id);
        specialty.setName(name);
        specialty.setDetail(detail);
        specialtyService.save(specialty);
        return new ResponseEntity(HttpStatus.OK);
    }
}
