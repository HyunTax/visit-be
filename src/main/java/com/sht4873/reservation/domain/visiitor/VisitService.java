package com.sht4873.reservation.domain.visiitor;

import com.sht4873.reservation.domain.visiitor.dto.request.ReservationSearchRequest;
import com.sht4873.reservation.excrption.VisitException;
import com.sht4873.reservation.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
public class VisitService {

    private final VisitRepository repository;
    private final SecurityUtils securityUtils;

    @Autowired
    public VisitService(SecurityUtils securityUtils, VisitRepository repository) {
        this.repository = repository;
        this.securityUtils = securityUtils;
    }

    @Transactional
    public Visit reservation(Visit entity) {
        if (repository.findByNameAndPhoneNum(entity.getName(), entity.getPhoneNum()).isPresent())
            throw new VisitException("이미 예약된 정보가 존재합니다.");
        entity.setPassword(securityUtils.encode(entity.getPassword()));
        return repository.save(entity);
    }

    public Visit findReservation(ReservationSearchRequest request) {
        Visit find = repository.findByNameAndPhoneNum(request.getName(), request.getPhoneNum()).orElseThrow(() -> new VisitException("예약 정보 없음"));
        if (securityUtils.nonMatches(request.getPassword(), find.getPassword()))
            throw new VisitException("비밀번호 오류");
        return find;
    }

    public List<Visit> finaAll() {
        return repository.findAll();
    }

    @Transactional
    public Visit updateReservation(Long id, Visit visit) {
        Visit find = repository.findById(id).orElseThrow(() -> new VisitException("예약 정보 없음"));
        if (!ObjectUtils.isEmpty(visit.getVisitDate()))
            find.setVisitDate(visit.getVisitDate());
        if (!ObjectUtils.isEmpty(visit.getVisitorCount()))
            find.setVisitorCount(visit.getVisitorCount());
        if (!ObjectUtils.isEmpty(visit.getVisitorDescription()))
            find.setVisitorDescription(visit.getVisitorDescription());
        if (!ObjectUtils.isEmpty(visit.getMomo()))
            find.setMomo(visit.getMomo());
        return find;
    }

    @Transactional
    public void cancelReservation(Long id) {
        Visit find = repository.findById(id).orElseThrow(() -> new VisitException("예약 정보 없음"));
        repository.delete(find);
    }
}
