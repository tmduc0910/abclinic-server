package com.abclinic.server.controller;

import com.abclinic.server.annotation.authorized.Restricted;
import com.abclinic.server.base.BaseController;
import com.abclinic.server.base.Views;
import com.abclinic.server.constant.RecordType;
import com.abclinic.server.exception.BadRequestException;
import com.abclinic.server.exception.NotFoundException;
import com.abclinic.server.model.entity.Inquiry;
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

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author tmduc
 * @package com.abclinic.server.controller
 * @created 2/4/2020 2:24 PM
 */
@RestController
public class InquiryResourceController extends BaseController {

    @Override
    public void init() {
        this.logger = LoggerFactory.getLogger(InquiryResourceController.class);
    }

    @PostMapping("/inquiries")
    @Restricted(included = Patient.class)
    @ApiOperation(
            value = "Tạo mới yêu cầu tư vấn",
            notes = "Trả về 201 CREATED hoặc 400 BAD REQUEST",
            tags = "Bệnh nhân"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "album_id", value = "Mã ID của album ảnh", type = "string"),
            @ApiImplicitParam(name = "type", value = "Kiểu yêu cầu tư vấn (0 khám bệnh, 1 dinh dưỡng)", required = true, type = "int", allowableValues = "0, 1", example = "0"),
            @ApiImplicitParam(name = "content", value = "Mô tả yêu cầu", required = true, type = "string")
    })
    @ApiResponses({
            @ApiResponse(code = 201, message = "Tạo mới thành công"),
            @ApiResponse(code = 400, message = "Loại yêu cầu không hợp lệ")
    })
    public ResponseEntity createInquiry(@ApiIgnore @RequestAttribute("User") User user,
                                        @RequestParam("album_id") String albumId,
                                        @RequestParam("type") int type,
                                        @RequestParam("content") String content) {
        Patient patient = patientRepository.findById(user.getId());
        if (type < RecordType.values().length) {
            save(new Inquiry(patient, albumId, content, type));
            return new ResponseEntity(HttpStatus.CREATED);
        } else throw new BadRequestException(user.getId(), "Loại yêu cầu không hợp lệ");
    }

    @GetMapping("/inquiries")
    @Restricted(excluded = Patient.class)
    @ApiOperation(
            value = "Lấy danh sách yêu cầu tư vấn",
            notes = "Trả về danh sách tư vấn hoặc 404 NOT FOUND",
            tags = "Nhân viên phòng khám"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "assigned", value = "Tìm bệnh nhân đã được đa khoa gán cho cấp dưới", paramType = "query", type = "boolean", allowableValues = "true, false"),
            @ApiImplicitParam(name = "page", value = "Số thứ tự trang", required = true, paramType = "query", allowableValues = "range[1, infinity]", example = "1"),
            @ApiImplicitParam(name = "size", value = "Kích thước trang", required = true, paramType = "query", example = "4")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Danh sách yêu cầu tư vấn"),
            @ApiResponse(code = 404, message = "Không tìm thấy yêu cầu hợp lệ")
    })
    @JsonView(Views.Private.class)
    public ResponseEntity<List<Inquiry>> getInquiryList(@ApiIgnore @RequestAttribute("User") User user,
                                                        @RequestParam(value = "assigned", defaultValue = "false") boolean assigned,
                                                        @RequestParam("page") int page,
                                                        @RequestParam("size") int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt"));
        List<Inquiry> inquiries = inquiryRepository.findAll(pageable).getContent();
        switch (user.getRole()) {
            case COORDINATOR:
                inquiries = inquiries.stream()
                        .filter(i -> i.getPatient().getPractitioner() == null)
                        .collect(Collectors.toList());
                break;
            case PRACTITIONER:
                inquiries = inquiries.stream()
                        .filter(i -> i.getPatient().getPractitioner().equals(user))
                        .filter(i -> {
                            if (i.getType() == RecordType.MEDICAL.getValue()) {
                                return (assigned && i.getPatient().getSpecialists().size() > 0) || (!assigned && i.getPatient().getSpecialists().size() == 0);
                            } else if (i.getType() == RecordType.DIET.getValue()) {
                                return (assigned && i.getPatient().getDietitians().size() > 0) || (!assigned && i.getPatient().getDietitians().size() == 0);
                            } else return false;
                        })
                        .collect(Collectors.toList());
                break;
            case SPECIALIST:
                inquiries = inquiries.stream()
                        .filter(i -> i.getPatient().getSpecialists().contains(user))
                        .collect(Collectors.toList());
                break;
            case DIETITIAN:
                inquiries = inquiries.stream()
                        .filter(i -> i.getPatient().getDietitians().contains(user))
                        .collect(Collectors.toList());
                break;
        }
        if (inquiries.size() > 0)
            return new ResponseEntity<>(inquiries, HttpStatus.OK);
        else throw new NotFoundException(user.getId());
    }
}
