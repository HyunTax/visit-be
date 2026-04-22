package com.sht4873.reservation.domain.auth;

import com.sht4873.reservation.core.exception.VisitException;
import com.sht4873.reservation.core.util.SecurityUtils;
import com.sht4873.reservation.domain.auth.dto.request.AdminAuthRequest;
import com.sht4873.reservation.domain.auth.dto.request.AuthRequest;
import com.sht4873.reservation.domain.visitor.Visit;
import com.sht4873.reservation.domain.visitor.VisitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {
    private final AuthRepository repository;
    private final VisitRepository visitRepository;
    private final SecurityUtils securityUtils;

    @Value("${admin.phone:010-0000-0000}")
    private List<String> adminPhones;

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

    public String issueAdminToken(AdminAuthRequest request) {
        List<String> encryptedPhones = adminPhones.stream()
                .map(p -> securityUtils.encryptPhone(p.trim()))
                .toList();
        List<Visit> admins = visitRepository.findByPhoneNumIn(encryptedPhones);
        if (admins.isEmpty()) throw new VisitException("관리자 정보가 존재하지 않습니다.");

        Visit admin = admins.stream()
                .filter(a -> securityUtils.matches(request.getPassword(), a.getPassword()))
                .findFirst()
                .orElseThrow(() -> new VisitException("비밀번호가 다릅니다."));
        return repository.issueAdminToken(admin.getName(), admin.getPhoneNum());
    }

    public Boolean verifyKey(String token) {
        return repository.existsByToken(token);
    }

    public Boolean verifyAdminKey(String token) {
        return repository.existsAdminByToken(token);
    }

    private void verifyRequest(AuthRequest request, String encryptedPhone) {
        Visit find = visitRepository.findByNameAndPhoneNum(request.getName(), encryptedPhone)
                .orElseThrow(() -> new VisitException("예약 정보가 존재하지 않습니다."));
        if (securityUtils.nonMatches(request.getPassword(), find.getPassword()))
            throw new VisitException("비밀번호가 다릅니다.");
    }

}