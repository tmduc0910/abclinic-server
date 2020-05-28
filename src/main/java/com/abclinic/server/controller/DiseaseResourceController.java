package com.abclinic.server.controller;

import com.abclinic.server.annotation.authorized.Restricted;
import com.abclinic.server.common.base.CustomController;
import com.abclinic.server.common.base.Views;
import com.abclinic.server.common.criteria.DiseasePredicateBuilder;
import com.abclinic.server.exception.BadRequestException;
import com.abclinic.server.model.dto.request.post.RequestCreateDiseaseDto;
import com.abclinic.server.model.dto.request.put.RequestUpdateDiseaseDto;
import com.abclinic.server.model.entity.Disease;
import com.abclinic.server.model.entity.user.Coordinator;
import com.abclinic.server.model.entity.user.Patient;
import com.abclinic.server.model.entity.user.Practitioner;
import com.abclinic.server.model.entity.user.User;
import com.abclinic.server.service.entity.DiseaseService;
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

/**
 * @author tmduc
 * @package com.abclinic.server.controller
 * @created 5/13/2020 3:29 PM
 */
@RestController
@RequestMapping("/diseases")
public class DiseaseResourceController extends CustomController {
    @Autowired
    private DiseaseService diseaseService;

    @Override
    public void init() {
        this.logger = LoggerFactory.getLogger(DiseaseResourceController.class);
    }

    @GetMapping("")
    @Restricted(excluded = Patient.class)
    @ApiOperation(
            value = "Lấy danh sách các căn bệnh",
            notes = "Trả về 200 OK hoặc 404 NOT FOUND",
            tags = "Nhân viên phòng khám"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "search", value = "Filter lọc bệnh nhân (name, description)", paramType = "query", example = "name=Đau,"),
            @ApiImplicitParam(name = "page", value = "Số thứ tự trang", required = true, paramType = "query", allowableValues = "range[1, infinity]", example = "1"),
            @ApiImplicitParam(name = "size", value = "Kích thước trang", required = true, paramType = "query", example = "4")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Danh sách bệnh"),
            @ApiResponse(code = 404, message = "Không có bệnh nào như yêu cầu")
    })
    @JsonView(Views.Abridged.class)
    public ResponseEntity<Page<Disease>> getDiseaseList(@ApiIgnore @RequestAttribute("User") User user,
                                                        @RequestParam("search") @Nullable String search,
                                                        @RequestParam("page") int page,
                                                        @RequestParam("size") int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("name").ascending());
        return new ResponseEntity<>(diseaseService.getList(user, search, new DiseasePredicateBuilder(), pageable), HttpStatus.OK);
    }

    @PostMapping("")
    @Restricted(included = {Coordinator.class, Practitioner.class})
    @ApiOperation(
            value = "Tạo căn bệnh mới",
            notes = "Trả về 201 CREATED hoặc 400 BAD REQUEST",
            tags = {"Điều phối viên", "Đa khoa"}
    )
    @ApiResponses({
            @ApiResponse(code = 201, message = "Tạo mới thành công"),
            @ApiResponse(code = 400, message = "Bệnh này đã tồn tại")
    })
    public ResponseEntity<Disease> createDisease(@ApiIgnore @RequestAttribute("User") User user,
                                                 @RequestBody RequestCreateDiseaseDto requestCreateDiseaseDto) {
        if (!diseaseService.isExist(requestCreateDiseaseDto.getName())) {
            return new ResponseEntity<>(diseaseService.save(new Disease(
                    requestCreateDiseaseDto.getName(),
                    requestCreateDiseaseDto.getDescription())), HttpStatus.CREATED);
        } else throw new BadRequestException(user.getId(), "Bệnh này đã tồn tại");
    }

    @PutMapping("")
    @Restricted(included = Coordinator.class)
    @ApiOperation(
            value = "Sửa đổi thông tin bệnh",
            notes = "Trả về 200 OK",
            tags = "Điều phối viên"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "Tên bệnh", required = true, dataType = "string", example = "Đau thận"),
            @ApiImplicitParam(name = "description", value = "Mô tả bệnh", required = true, dataType = "string", example = "Bệnh đau thận")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Chỉnh sửa thành công"),
    })
    public ResponseEntity<Disease> editDisease(@ApiIgnore @RequestAttribute("User") User user,
                                               @RequestBody RequestUpdateDiseaseDto requestUpdateDiseaseDto) {
        Disease disease = diseaseService.getById(requestUpdateDiseaseDto.getId());
        disease.setName(requestUpdateDiseaseDto.getName());
        disease.setDescription(requestUpdateDiseaseDto.getDescription());
        return new ResponseEntity<>(diseaseService.save(disease), HttpStatus.OK);
    }
}
