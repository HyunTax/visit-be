package com.sht4873.reservation.core.aop.auth;

import com.sht4873.reservation.core.excrption.VisitException;
import com.sht4873.reservation.domain.auth.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class AuthAspect {

    private final AuthService authService;

    public AuthAspect(AuthService authService) {
        this.authService = authService;
    }

    @Before("@annotation(com.sht4873.reservation.core.aop.auth.RequireAuth)")
    public void validateAuthentication() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String token = request.getHeader("Authorization");
        if (ObjectUtils.isEmpty(token))
            throw new VisitException("인증 정보 없음", HttpStatus.UNAUTHORIZED);
        if (!authService.verifyKey(token))
            throw new VisitException("유효하지 않은 인증 정보", HttpStatus.UNAUTHORIZED);
    }
}