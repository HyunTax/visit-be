package com.sht4873.reservation.domain.visiitor;

import com.sht4873.reservation.domain.visiitor.dto.request.ReservationSearchRequest;
import com.sht4873.reservation.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class VisitService {

    private final VisitRepository repository;
    private final SecurityUtils securityUtils;

    @Autowired
    public VisitService(SecurityUtils securityUtils, VisitRepository repository) {
        this.repository = repository;
        this.securityUtils = securityUtils;
    }

    public Visit reservation(Visit entity) {
        encodingPassword(entity);
        return repository.save(entity);
    }

    public Visit findReservation(ReservationSearchRequest request) throws Exception {
        Visit find = repository.findByNameAndPhoneNum(request.getName(), request.getPhoneNum());
        if (Objects.isNull(find))
            throw new Exception("예약 정보 없음");
        if (securityUtils.nonMatches(request.getPassword(), find.getPassword()))
            throw new Exception("비밀번호 오류");
        return find;
    }

    public List<Visit> finaAll() {
        return repository.findAll();
    }

    private void encodingPassword(Visit entity) {
        String requestPassword = entity.getPassword();
        entity.setPassword(securityUtils.encode(requestPassword));
    }
}
