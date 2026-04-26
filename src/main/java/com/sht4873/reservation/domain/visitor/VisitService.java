package com.sht4873.reservation.domain.visitor;

import com.sht4873.reservation.core.component.MailComponent;
import com.sht4873.reservation.core.exception.VisitException;
import com.sht4873.reservation.core.util.SecurityUtils;
import com.sht4873.reservation.domain.visitor.dto.request.ReservationSearchRequest;
import com.sht4873.reservation.domain.visitor.dto.request.ReservationStatusRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.util.List;

@Service
public class VisitService {

    private final VisitRepository repository;
    private final SecurityUtils securityUtils;
    private final MailComponent mailComponent;

    @Autowired
    public VisitService(SecurityUtils securityUtils, VisitRepository repository, MailComponent mailComponent) {
        this.repository = repository;
        this.securityUtils = securityUtils;
        this.mailComponent = mailComponent;
    }

    @Transactional(readOnly = true)
    public List<LocalDate> findAllReservedDates() {
        return repository.findAllByStatusIn(List.of(Visit.Status.WAIT, Visit.Status.CONFIRM))
                .stream().map(Visit::getVisitDate).toList();
    }

    @Transactional
    public Visit reservation(Visit entity) {
        String encryptedPhone = securityUtils.encryptPhone(entity.getPhoneNum());
        if (repository.findByNameAndPhoneNum(entity.getName(), encryptedPhone).isPresent())
            throw new VisitException("이미 예약된 정보가 존재합니다.");
        entity.setPhoneNum(encryptedPhone);
        entity.setPassword(securityUtils.encode(entity.getPassword()));
        entity.setStatus(Visit.Status.WAIT);
        Visit reservation = repository.save(entity);
        sendAdminMail(reservation);
        return reservation;
    }

    @Transactional(readOnly = true)
    public Visit findReservation(ReservationSearchRequest request) {
        String encryptedPhone = securityUtils.encryptPhone(request.getPhoneNum());
        Visit find = repository.findByNameAndPhoneNum(request.getName(), encryptedPhone)
                .orElseThrow(() -> new VisitException("예약 정보가 존재하지 않습니다."));
        if (securityUtils.nonMatches(request.getPassword(), find.getPassword()))
            throw new VisitException("비밀번호가 다릅니다.");
        return find;
    }

    @Transactional
    public Visit updateReservation(Long id, Visit visit) {
        Visit find = repository.findById(id).orElseThrow(() -> new VisitException("예약 정보가 존재하지 않습니다."));
        if (!ObjectUtils.isEmpty(visit.getVisitDate()))
            find.setVisitDate(visit.getVisitDate());
        if (!ObjectUtils.isEmpty(visit.getVisitorCount()))
            find.setVisitorCount(visit.getVisitorCount());
        if (!ObjectUtils.isEmpty(visit.getHasAllergy()))
            find.setHasAllergy(visit.getHasAllergy());
        if (!ObjectUtils.isEmpty(visit.getMemo()))
            find.setMemo(visit.getMemo());
        return find;
    }

    @Transactional
    public void cancelReservation(Long id, ReservationStatusRequest request) {
        Visit find = repository.findById(id).orElseThrow(() -> new VisitException("예약 정보가 존재하지 않습니다."));
        find.setStatus(Visit.Status.CANCEL);
        find.setStatusMemo(request.getStatusMemo());
    }

    @Transactional(readOnly = true)
    public List<Visit> findAll() {
        return repository.findAll();
    }

    @Transactional
    public void confirm(Long id) {
        Visit find = repository.findById(id).orElseThrow(() -> new VisitException("예약 정보가 존재하지 않습니다."));
        find.setStatus(Visit.Status.CONFIRM);
    }

    @Transactional
    public void reject(Long id, ReservationStatusRequest request) {
        Visit find = repository.findById(id).orElseThrow(() -> new VisitException("예약 정보가 존재하지 않습니다."));
        find.setStatus(Visit.Status.REJECT);
        find.setStatusMemo(request.getStatusMemo());
    }

    private void sendAdminMail(Visit reservation) {
        mailComponent.sendAdminMail(reservation);
    }
}