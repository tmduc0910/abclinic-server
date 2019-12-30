package com.abclinic.server.config;

import com.abclinic.server.constant.Role;
import com.abclinic.server.constant.RoleValue;
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
        response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        response.addHeader("Access-Control-Max-Age", "1000");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type");
        response.addHeader("Cache-Control", "private");

        try {
            if (!requestUri.startsWith("/auth")) {
                int id = Integer.parseInt(request.getHeader("Authentication"));
                Optional<User> op = userRepository.findById(id);
                if (!op.isPresent())
                    throw new BadRequestException(id, "Authentication failed");
                User user = op.get();
                if (user.getUid() == null)
                    throw new UnauthorizedActionException(id);
                else if (requestUri.contains("/admin") && user.getRole() == Role.PATIENT)
                    throw new UnauthorizedActionException(id);
                request.setAttribute("User-Role", user.getRole());
            } else {
                String req = request.getParameterMap().entrySet().iterator().next().getValue()[0];
                Optional<User> user = userRepository.findByEmailOrPhoneNumber(req, req);
                if (user.isPresent()) {
                    if (user.get().getUid() != null)
                        throw new ForbiddenException();
                }
            }
        } catch (NumberFormatException | NullPointerException e) {
            System.out.println(requestUri);
            throw new ForbiddenException();
        }
        return super.preHandle(request, response, handler);
    }
}
