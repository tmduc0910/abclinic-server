package com.abclinic.server.annotation.authorized;

import com.abclinic.server.common.base.CustomRuntimeException;
import com.abclinic.server.exception.ForbiddenException;
import com.abclinic.server.model.entity.user.User;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.jdo.annotations.Join;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author tmduc
 * @package com.abclinic.server.annotation.authorized
 * @created 1/11/2020 2:58 PM
 */
@Aspect
@Component
public class AuthAspect {
    @Before("@annotation(annotation)")
    public void execute(Restricted annotation) throws ForbiddenException {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        User user = (User) request.getAttribute("User");

        //Nếu có định nghĩa included và user không thuộc include
        //hoặc nếu có định nghĩa excluded và user thuộc excluded
        //trả về 403 Forbidden và chặn API
        if ((annotation.included().length != 0 && !Arrays.asList(annotation.included()).contains(user.getClass()) ||
                (annotation.excluded().length != 0 && Arrays.asList(annotation.excluded()).contains(user.getClass()))))
            throw new ForbiddenException(user.getId(), "Không có quyền hạn sử dụng API này");
    }
}
