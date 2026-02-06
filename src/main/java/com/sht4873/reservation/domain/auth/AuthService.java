package com.sht4873.reservation.domain.auth;

import com.sht4873.reservation.domain.auth.dto.request.AuthRequest;
import com.sht4873.reservation.domain.visitor.Visit;
import com.sht4873.reservation.domain.visitor.VisitRepository;
import com.sht4873.reservation.core.excrption.VisitException;
import com.sht4873.reservation.core.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final AuthRepository repository;
    private final VisitRepository visitRepository;
    private final SecurityUtils securityUtils;

    @Autowired
    public AuthService(AuthRepository repository, VisitRepository visitRepository, SecurityUtils securityUtils) {
        this.repository = repository;
        this.visitRepository = visitRepository;
        this.securityUtils = securityUtils;
    }

    public String issueToken(AuthRequest request) {
        verifyRequest(request);
        return repository.issueToken(request);
    }

    public Boolean verifyKey(String token) {
        return repository.existsByToken(token);
    }

    private void verifyRequest(AuthRequest request) {
        Visit find = visitRepository.findByNameAndPhoneNum(request.getName(), request.getPhoneNum()).orElseThrow(() -> new VisitException("예약 정보 없음"));
        if (securityUtils.nonMatches(request.getPassword(), find.getPassword()))
            throw new VisitException("비밀번호 오류");
    }

}
