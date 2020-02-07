package com.abclinic.server.controller;

import com.abclinic.server.annotation.authorized.Restricted;
import com.abclinic.server.base.BaseController;
import com.abclinic.server.exception.NotFoundException;
import com.abclinic.server.model.entity.Specialty;
import com.abclinic.server.model.entity.user.Coordinator;
import com.abclinic.server.model.entity.user.Patient;
import com.abclinic.server.model.entity.user.User;
import io.swagger.annotations.*;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author tmduc
 * @package com.abclinic.server.controller
 * @created 2/7/2020 2:49 PM
 */
@RestController
public class SpecialtyResourceController extends BaseController {

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
        return new ResponseEntity<>(specialtyRepository.findAll(), HttpStatus.OK);
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
    public ResponseEntity addSpecialty(@RequestParam("name") String name,
                                       @RequestParam("detail") String detail) {
        Specialty specialty = new Specialty(name, detail);
        save(specialty);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @PutMapping("/specialties")
    @Restricted(included = Coordinator.class)
    @ApiOperation(
            value = "Sửa đổi thông tin chuyên môn",
            notes = "Trả về 200 OK hoặc 404 NOT FOUND",
            tags = "Điều phối viên"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Mã ID của chuyên môn", required = true, dataType = "int"),
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
        Specialty specialty = specialtyRepository.findById(id);
        if (specialty != null) {
            specialty.setName(name);
            specialty.setDetail(detail);
            save(specialty);
            return new ResponseEntity(HttpStatus.OK);
        } else throw new NotFoundException(user.getId());
    }
}
