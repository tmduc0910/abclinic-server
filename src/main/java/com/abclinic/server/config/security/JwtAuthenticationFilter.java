package com.abclinic.server.config.security;

import com.abclinic.server.common.constant.UserStatus;
import com.abclinic.server.exception.ForbiddenException;
import com.abclinic.server.exception.UnauthorizedActionException;
import com.abclinic.server.model.entity.user.User;
import com.abclinic.server.service.entity.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * @author tmduc
 * @package com.abclinic.server.config.security
 * @created 6/2/2020 2:43 PM
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserService userService;
    private Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // Lấy jwt từ request
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                // Lấy id user từ chuỗi jwt
                Long userId = tokenProvider.getUserIdFromJWT(jwt);
                // Lấy thông tin người dùng từ id
                CustomUserDetails userDetails = new CustomUserDetails(userService.getById(userId));
                if (userDetails != null) {
                    // Nếu người dùng hợp lệ, set thông tin cho Security Context
                    UsernamePasswordAuthenticationToken
                            authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception ex) {
            log.error("failed on set user authentication", ex);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String requestUri = request.getRequestURI();

        if (requestUri.startsWith("/api/error") ||
                requestUri.contains("swagger") ||
                requestUri.contains("api-docs") ||
                requestUri.equalsIgnoreCase("/api/") ||
                requestUri.equalsIgnoreCase("/api/csrf") ||
                requestUri.contains("/misc") ||
                request.getMethod().contains("OPTIONS"))
            return null;

        if (!requestUri.contains("/api/auth/login") && !requestUri.contains("/ws")) {
            String uid = request.getHeader("Authorization");
            if (requestUri.contains("/api/auth/sign_up") && uid == null && userService.getCoordinatorsCount() != 0) {
                throw new ForbiddenException(-1, "Tài khoản gốc đã được đăng ký");
            }

            if (uid == null || uid.trim().isEmpty())
                throw new UnauthorizedActionException(-1, "Thông tin xác thực rỗng");
            Optional<User> op = userService.findByUID(uid);
            if (!op.isPresent())
                throw new UnauthorizedActionException(-1, "Thông tin xác thực không tồn tại");
            User user = op.get();
            if (user.getUid() == null)
                throw new UnauthorizedActionException(user.getId(), "Tài khoản chưa đăng nhập");
            if (user.getStatus() == UserStatus.DEACTIVATED.getValue())
                throw new ForbiddenException(user.getId(), "Tài khoản đã bị xóa hoặc vô hiệu hóa");
            request.setAttribute("User", user);
            return uid;
        } else {
            return null;
        }
    }
}
