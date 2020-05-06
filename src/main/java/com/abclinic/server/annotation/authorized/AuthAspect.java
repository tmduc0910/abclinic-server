package com.abclinic.server.annotation.authorized;

import com.abclinic.server.exception.ForbiddenException;
import com.abclinic.server.model.entity.user.User;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * @author tmduc
 * @package com.abclinic.server.annotation.authorized
 * @created 1/11/2020 2:58 PM
 */
@Aspect
@Component
public class AuthAspect {
    @Around("execution(* *.*(..)) && @annotation(annotation)")
    public Object execute(ProceedingJoinPoint joinPoint, Restricted annotation) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        User user = (User) request.getAttribute("User");
        if (Arrays.asList(annotation.included()).contains(user.getClass()) || !Arrays.asList(annotation.excluded()).contains(user.getClass()))
            return joinPoint.proceed();
        throw new ForbiddenException(user.getId(), "Không có quyền hạn sử dụng API này");
    }
}
