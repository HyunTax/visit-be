package com.sht4873.reservation.core.interceptor;

import com.sht4873.reservation.core.annotation.RequireAdmin;
import com.sht4873.reservation.core.annotation.RequireAuth;
import com.sht4873.reservation.core.exception.VisitException;
import com.sht4873.reservation.domain.auth.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final AuthService authService;

    public AuthInterceptor(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod method)) return true;

        boolean requireAdmin = method.hasMethodAnnotation(RequireAdmin.class);
        boolean requireAuth = method.hasMethodAnnotation(RequireAuth.class);

        if (!requireAdmin && !requireAuth) return true;

        String token = resolveToken(request);
        if (token == null)
            throw new VisitException("인증 정보 없음", HttpStatus.UNAUTHORIZED);

        if (requireAdmin && !authService.verifyAdminKey(token))
            throw new VisitException("관리자 권한 없음", HttpStatus.FORBIDDEN);

        if (requireAuth && !authService.verifyKey(token))
            throw new VisitException("유효하지 않은 인증 정보", HttpStatus.FORBIDDEN);

        return true;
    }

    private String resolveToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) return null;
        return header.substring(7);
    }
}
