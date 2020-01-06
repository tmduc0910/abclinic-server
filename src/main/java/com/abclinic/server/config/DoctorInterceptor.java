package com.abclinic.server.config;

import com.abclinic.server.constant.Role;
import com.abclinic.server.exception.ForbiddenException;
import com.abclinic.server.exception.UnauthorizedActionException;
import com.abclinic.server.model.entity.user.User;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author tmduc
 * @package com.abclinic.server.config
 * @created 1/5/2020 10:22 AM
 */
public class DoctorInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestUri = request.getRequestURI();
        User user = (User) request.getAttribute("User");
        boolean isPermit = auth(user, requestUri);
        if (isPermit)
            return true;
        else throw new ForbiddenException(user.getId(), "API này không dành cho loại bác sĩ của bạn");
    }

    private boolean auth(User user, String requestUri) {
        switch (user.getRole()) {
            case COORDINATOR:
                return requestUri.startsWith("/admin/c");
            case DIETITIAN:
                return requestUri.startsWith("/admin/d");
            case SPECIALIST:
                return requestUri.startsWith("/admin/s");
            case PRACTITIONER:
                return requestUri.startsWith("/admin/p");
            default:
                return true;
        }
    }
}
