package com.abclinic.server.controller;

import com.abclinic.server.common.base.CustomController;
import com.abclinic.server.model.dto.AccountDto;
import com.abclinic.server.service.entity.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author tmduc
 * @package com.abclinic.server.controller
 * @created 1/9/2020 3:13 PM
 */
@RestController
@RequestMapping("/misc")
@Api(tags = "Khác")
public class MiscController extends CustomController {
    @Autowired
    private UserService userService;

    @Override
    public void init() {
        this.logger = LoggerFactory.getLogger(MiscController.class);
    }

    @GetMapping("/wipe")
    @ApiOperation(value = "Xóa sạch UID của tất cả tài khoản")
    public ResponseEntity handleLogoutAll() {
        userService.findAll().forEach(u -> {
            u.setUid(null);
            userService.save(u);
        });
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/accounts")
    @ApiOperation(value = "Trả về danh sách tài khoản đang lưu trong hệ thống")
    public ResponseEntity<List<AccountDto>> handleGetAllAccount() {
        return new ResponseEntity<>(userService.findAll().stream()
                .map(u -> new AccountDto(u.getId(), u.getUid(), u.getEmail(), u.getPhoneNumber(), u.getPassword(), u.getName()))
                .collect(Collectors.toList()), HttpStatus.OK);
    }
}
