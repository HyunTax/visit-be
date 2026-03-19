package com.sht4873.reservation.core.interceptor;

import com.sht4873.reservation.core.annotation.RequireAdmin;
import com.sht4873.reservation.core.annotation.RequireAuth;
import com.sht4873.reservation.core.exception.VisitException;
import com.sht4873.reservation.domain.auth.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final AuthService authService;

    @Value("${admin.phone:010-0000-0000}")
    private String adminPhoneNum;

    public AuthInterceptor(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod method)) return true;

        String token = request.getHeader("Authorization");

        if (method.hasMethodAnnotation(RequireAdmin.class)) {
            if (ObjectUtils.isEmpty(token))
                throw new VisitException("인증 정보 없음", HttpStatus.UNAUTHORIZED);
            if (!authService.verifyAdmin(token, adminPhoneNum))
                throw new VisitException("관리자 권한 없음", HttpStatus.FORBIDDEN);
            return true;
        }

        if (method.hasMethodAnnotation(RequireAuth.class)) {
            if (ObjectUtils.isEmpty(token))
                throw new VisitException("인증 정보 없음", HttpStatus.UNAUTHORIZED);
            if (!authService.verifyKey(token))
                throw new VisitException("유효하지 않은 인증 정보", HttpStatus.UNAUTHORIZED);
        }

        return true;
    }
}
