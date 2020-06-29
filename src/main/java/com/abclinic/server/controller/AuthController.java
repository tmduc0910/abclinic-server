package com.abclinic.server.controller;

import com.abclinic.server.common.base.CustomController;
import com.abclinic.server.common.base.Views;
import com.abclinic.server.common.constant.Role;
import com.abclinic.server.common.constant.RoleValue;
import com.abclinic.server.common.constant.UserStatus;
import com.abclinic.server.common.utils.DateTimeUtils;
import com.abclinic.server.config.security.CustomUserDetails;
import com.abclinic.server.config.security.JwtTokenProvider;
import com.abclinic.server.exception.DuplicateValueException;
import com.abclinic.server.exception.ForbiddenException;
import com.abclinic.server.exception.NotFoundException;
import com.abclinic.server.exception.WrongCredentialException;
import com.abclinic.server.model.dto.request.post.RequestLoginDto;
import com.abclinic.server.model.dto.request.post.RequestSignUpDto;
import com.abclinic.server.model.entity.user.*;
import com.abclinic.server.service.entity.UserService;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * @author tmduc
 * @package com.abclinic.server.controller
 * @created 11/23/2019 3:38 PM
 */
@RestController
@Api(tags = "Xác thực")
@RequestMapping("/auth")
public class AuthController extends CustomController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Override
    public void init() {
        logger = LoggerFactory.getLogger(AuthController.class);
    }

    @PostMapping(value = "/login")
    @ApiOperation(value = "Người dùng đăng nhập qua account", notes = "Trả về chuỗi UID hoặc 404 NOT FOUND\n" +
            "Trường UID được trả về sẽ được gán vào header Authorization ở tất cả các API sau đó.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Client-Type", value = "Client-Type", paramType = "header", dataType = "string")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Đăng nhập thành công"),
            @ApiResponse(code = 404, message = "Đăng nhập thất bại")
    })
    @JsonView(Views.Private.class)
    public ResponseEntity<String> processLogin(@RequestHeader("Client-Type") String type,
                                               @RequestBody RequestLoginDto requestLoginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        requestLoginDto.getAccount(),
                        requestLoginDto.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = userService.findByUsername((String) authentication.getPrincipal()).get();
        if (user.getRole() != Role.PATIENT && type.equalsIgnoreCase("mobile"))
            throw new NotFoundException();

        String jwt = tokenProvider.generateToken(new CustomUserDetails(user));
        user.setUid(jwt);
        userService.save(user);
        return new ResponseEntity<>(jwt, HttpStatus.OK);
    }

    @PostMapping(value = "/sign_up")
    @ApiOperation(value = "Đăng kí tài khoản", notes = "Trả về 201 CREATED hoặc 409 CONFLICT")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Đăng kí thành công"),
            @ApiResponse(code = 400, message = "Chỉ có điều phối viên mới được đăng ký cho bệnh nhân"),
            @ApiResponse(code = 409, message = "Email hoặc SĐT này đã được sử dụng")
    })
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity processSignUp(@Nullable @ApiIgnore @RequestAttribute(name = "User") User user,
                                        @RequestBody RequestSignUpDto requestSignUpDto) {
        if (user == null && requestSignUpDto.getRole() != Role.COORDINATOR.getValue())
            throw new ForbiddenException(-1, "Đăng ký thất bại");

        if (userService.findByEmail(requestSignUpDto.getEmail()).isPresent() ||
                userService.findByPhoneNumber(requestSignUpDto.getPhone()).isPresent())
            throw new DuplicateValueException();
        User u = null;
        switch (requestSignUpDto.getRole()) {
            case RoleValue.PRACTITIONER:
                u = new Practitioner(requestSignUpDto.getName(),
                        requestSignUpDto.getEmail(),
                        requestSignUpDto.getGender(),
                        DateTimeUtils.parseDate(requestSignUpDto.getDateOfBirth()),
                        requestSignUpDto.getPassword(),
                        requestSignUpDto.getPhone());
                break;
            case RoleValue.COORDINATOR:
                u = new Coordinator(requestSignUpDto.getName(),
                        requestSignUpDto.getEmail(),
                        requestSignUpDto.getGender(),
                        DateTimeUtils.parseDate(requestSignUpDto.getDateOfBirth()),
                        requestSignUpDto.getPassword(),
                        requestSignUpDto.getPhone());
                break;
            case RoleValue.DIETITIAN:
                u = new Dietitian(requestSignUpDto.getName(),
                        requestSignUpDto.getEmail(),
                        requestSignUpDto.getGender(),
                        DateTimeUtils.parseDate(requestSignUpDto.getDateOfBirth()),
                        requestSignUpDto.getPassword(),
                        requestSignUpDto.getPhone());
                break;
            case RoleValue.SPECIALIST:
                u = new Specialist(requestSignUpDto.getName(),
                        requestSignUpDto.getEmail(),
                        requestSignUpDto.getGender(),
                        DateTimeUtils.parseDate(requestSignUpDto.getDateOfBirth()),
                        requestSignUpDto.getPassword(),
                        requestSignUpDto.getPhone());
                break;
            case RoleValue.PATIENT:
                u = new Patient(requestSignUpDto.getName(),
                        requestSignUpDto.getEmail(),
                        requestSignUpDto.getGender(),
                        DateTimeUtils.parseDate(requestSignUpDto.getDateOfBirth()),
                        requestSignUpDto.getPassword(),
                        requestSignUpDto.getPhone());
                break;
        }
        userService.save(u);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @PostMapping(value = "/sign_out")
    @ApiOperation(value = "Đăng xuất tài khoản", notes = "Trả về 200 OK")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Đăng xuất thành công")
    })
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity processSignOut(@ApiIgnore @RequestAttribute(name = "User") User user) {
        user.setUid(null);
        userService.save(user);
        return new ResponseEntity(HttpStatus.OK);
    }
}
