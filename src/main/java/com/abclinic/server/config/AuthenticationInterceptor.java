package com.abclinic.server.config;

import com.abclinic.server.constant.Role;
import com.abclinic.server.constant.RoleValue;
import com.abclinic.server.constant.Status;
import com.abclinic.server.exception.BadRequestException;
import com.abclinic.server.exception.ForbiddenException;
import com.abclinic.server.exception.UnauthorizedActionException;
import com.abclinic.server.model.entity.user.User;
import com.abclinic.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;

public class AuthenticationInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestUri = request.getRequestURI();
        if (requestUri.startsWith("/api/error") || requestUri.contains("swagger") || requestUri.contains("api-docs"))
            return true;

        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.addHeader("Access-Control-Max-Age", "1000");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.addHeader("Cache-Control", "private");

        try {
            if (!requestUri.startsWith("/api/auth")) {
                String uid = request.getHeader("Authorization");
                if (uid == null)
                    throw new UnauthorizedActionException(-1, "Thông tin xác thực rỗng");
                Optional<User> op = userRepository.findByUid(uid);
                if (!op.isPresent())
                    throw new UnauthorizedActionException(-1, "Thông tin xác thực không tồn tại");
                User user = op.get();
                if (user.getUid() == null)
                    throw new UnauthorizedActionException(user.getId(), "Tài khoản chưa đăng nhập");
                if (user.getStatus() == Status.User.DEACTIVATED)
                    throw new ForbiddenException(user.getId(), "Tài khoản đã bị xóa hoặc vô hiệu hóa");
//                else if (requestUri.contains("/admin") && user.getRole() == Role.PATIENT)
//                    throw new UnauthorizedActionException(user.getId(), "Bạn chưa đăng nhập");
                request.setAttribute("User", user);
            } else {
                String req = request.getParameterMap().entrySet().iterator().next().getValue()[0];
                Optional<User> user = userRepository.findByEmailOrPhoneNumber(req, req);
                if (user.isPresent()) {
                    if (user.get().getUid() != null)
                        throw new ForbiddenException(user.get().getId(), "Bạn chưa đăng xuất");
                }
            }
        } catch (NumberFormatException | NullPointerException e) {
            System.out.println(requestUri);
            throw new ForbiddenException(-1);
        }
        return super.preHandle(request, response, handler);
    }
}
