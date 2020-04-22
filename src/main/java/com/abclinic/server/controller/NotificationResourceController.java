package com.abclinic.server.controller;

import com.abclinic.server.common.base.BaseController;
import com.abclinic.server.common.base.Views;
import com.abclinic.server.common.constant.MessageType;
import com.abclinic.server.exception.ForbiddenException;
import com.abclinic.server.exception.NotFoundException;
import com.abclinic.server.model.entity.notification.Notification;
import com.abclinic.server.model.entity.notification.NotificationMessage;
import com.abclinic.server.model.entity.user.*;
import com.abclinic.server.service.NotificationService;
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

import java.util.List;
import java.util.Optional;

/**
 * @author tmduc
 * @package com.abclinic.server.controller
 * @created 2/13/2020 2:00 PM
 */
@RestController
public class NotificationResourceController extends BaseController {

    @Autowired
    NotificationService service;

    @Override
    public void init() {
        this.logger = LoggerFactory.getLogger(NotificationResourceController.class);
    }

    @PostMapping("/notifications")
    @ApiOperation(value = "", tags = "Khác")
    public ResponseEntity createNotification(@ApiIgnore @RequestAttribute("User") User user) {
        service.makeNotification(userRepository.findById(5).get(), new NotificationMessage(MessageType.INQUIRE, user, inquiryRepository.findById(1).get()));
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @GetMapping("/notifications")
    @ApiOperation(
            value = "Lấy danh sách thông báo",
            notes = "Trả về danh sách thông báo hoặc 404 NOT FOUND",
            tags = {"Nhân viên phòng khám", "Bệnh nhân"}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "Số thứ tự trang", required = true, paramType = "query", allowableValues = "range[1, infinity]", example = "1"),
            @ApiImplicitParam(name = "size", value = "Kích thước trang", required = true, paramType = "query", example = "4")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Danh sách thông báo"),
            @ApiResponse(code = 404, message = "Không tìm thấy thông báo nào")
    })
    @JsonView(Views.Abridged.class)
    public ResponseEntity<Page<Notification>> getNotifications(@ApiIgnore @RequestAttribute("User") User user,
                                                               @RequestParam("page") int page,
                                                               @RequestParam("size") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Optional<Page<Notification>> op = notificationRepository.findByReceiver(user, pageable);
        if (op.isPresent())
            return new ResponseEntity<>(op.get(), HttpStatus.OK);
        else throw new NotFoundException(user.getId());
    }

    @GetMapping("/notifications/{id}")
    @ApiOperation(
            value = "Lấy chi tiết thông báo",
            notes = "Trả về chi tiết thông báo hoặc 403 FORBIDDEN hoặc 404 NOT FOUND",
            tags = {"Nhân viên phòng khám", "Bệnh nhân"}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Mã ID của thông báo", required = true, paramType = "path", dataType = "int", example = "1"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Chi tiết thông báo"),
            @ApiResponse(code = 403, message = "Không được phép truy cập thông báo của người dùng khác"),
            @ApiResponse(code = 404, message = "Thông báo không tồn tại")
    })
    @JsonView(Views.Private.class)
    public ResponseEntity<Notification> getNotification(@ApiIgnore @RequestAttribute("User") User user,
                                                        @PathVariable("id") long id) {
        Notification notification = notificationRepository.findById(id);
        if (notification != null) {
            if (notification.getReceiver().equals(user)) {
                notification.setIsRead(true);
                save(notification);
                return new ResponseEntity<>(notification, HttpStatus.OK);
            }
            else throw new ForbiddenException(user.getId(), "Không được phép truy cập thông báo của người dùng khác");
        } else throw new NotFoundException(user.getId());
    }
}
