package com.sht4873.reservation.domain.auth;

import com.sht4873.reservation.core.exception.VisitException;
import com.sht4873.reservation.core.util.SecurityUtils;
import com.sht4873.reservation.domain.auth.dto.request.AuthRequest;
import com.sht4873.reservation.domain.visitor.Visit;
import com.sht4873.reservation.domain.visitor.VisitRepository;
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
        String encryptedPhone = securityUtils.encryptPhone(request.getPhoneNum());
        verifyRequest(request, encryptedPhone);
        return repository.issueToken(request.getName(), encryptedPhone);
    }

    public Boolean verifyKey(String token) {
        return repository.existsByToken(token);
    }

    public Boolean verifyAdmin(String token, String adminPhoneNum) {
        String storedPhone = repository.getPhoneNumByToken(token);
        if (storedPhone == null) return false;
        return storedPhone.equals(securityUtils.encryptPhone(adminPhoneNum));
    }

    private void verifyRequest(AuthRequest request, String encryptedPhone) {
        Visit find = visitRepository.findByNameAndPhoneNum(request.getName(), encryptedPhone)
                .orElseThrow(() -> new VisitException("예약 정보가 존재하지 않습니다."));
        if (securityUtils.nonMatches(request.getPassword(), find.getPassword()))
            throw new VisitException("비밀번호가 다릅니다.");
    }

}