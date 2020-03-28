package com.abclinic.server.controller;

import com.abclinic.server.annotation.authorized.Restricted;
import com.abclinic.server.base.BaseController;
import com.abclinic.server.base.Views;
import com.abclinic.server.constant.Role;
import com.abclinic.server.constant.Status;
import com.abclinic.server.exception.ForbiddenException;
import com.abclinic.server.exception.NotFoundException;
import com.abclinic.server.model.entity.Specialty;
import com.abclinic.server.model.entity.user.Coordinator;
import com.abclinic.server.model.entity.user.Patient;
import com.abclinic.server.model.entity.user.User;
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

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * @author tmduc
 * @package com.abclinic.server.controller
 * @created 1/11/2020 2:47 PM
 */
@RestController
public class DoctorResourceController extends BaseController {

    @Override
    public void init() {
        this.logger = LoggerFactory.getLogger(DoctorResourceController.class);
    }

    @GetMapping("/doctors")
    @Transactional
    @Restricted(excluded = Patient.class)
    @ApiOperation(
            value = "Lọc và lấy danh sách bác sĩ",
            notes = "Trả về danh sách các bác sĩ còn đang hoạt động hoặc nếu không tồn tại thì trả về 404 NOT FOUND",
            tags = "Nhân viên phòng khám"
    )
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
            result = userRepository.findByRoleAndStatus(Integer.parseInt(type), Status.User.ACTIVATED, pageable);
        } catch (NumberFormatException e) {
            result = userRepository.findAllByRoleIsLessThanAndStatus(Role.PATIENT.getValue(), Status.User.ACTIVATED, pageable);
        }
        if (result.isPresent())
            return new ResponseEntity(result.get(), HttpStatus.OK);
        else throw new NotFoundException(user.getId());
    }

    @DeleteMapping("/doctors")
    @Restricted(included = Coordinator.class)
    @ApiOperation(
            value = "Xóa một bác sĩ",
            notes = "Trả về 200 OK hoặc 404 NOT FOUND",
            tags = {"Điều phối viên"}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "ID của bác sĩ cần xóa", dataType = "int", example = "1")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Xóa bác sĩ thành công"),
            @ApiResponse(code = 404, message = "ID của bác sĩ không tồn tại")
    })
    public ResponseEntity deleteDoctor(@ApiIgnore @RequestAttribute("User") User user,
                                       @RequestParam("id") long id) {
        Optional<User> op = userRepository.findById(id);
        if (op.isPresent()) {
            User doctor = op.get();
            doctor.setStatus(Status.User.DEACTIVATED);
            save(doctor);
            return new ResponseEntity(HttpStatus.OK);
        } else throw new NotFoundException(user.getId());
    }

    @GetMapping("/doctors/{id}")
    @Transactional
    @ApiOperation(
            value = "Lấy thông tin chi tiết bác sĩ",
            notes = "Trả về thông tin chi tiết của một bác sĩ hoặc 404 NOT FOUND",
            tags = {"Nhân viên phòng khám", "Bệnh nhân"}
    )
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
