package com.sht4873.reservation.domain.visitor;

import com.sht4873.reservation.core.annotation.RequireAdmin;
import com.sht4873.reservation.core.annotation.RequireAuth;
import com.sht4873.reservation.core.util.SecurityUtils;
import com.sht4873.reservation.domain.visitor.dto.request.ReservationRequest;
import com.sht4873.reservation.domain.visitor.dto.request.ReservationSearchRequest;
import com.sht4873.reservation.domain.visitor.dto.request.ReservationStatusRequest;
import com.sht4873.reservation.domain.visitor.dto.response.ReservationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/visit/reservation")
public class VisitController {

    private final VisitService service;
    private final SecurityUtils securityUtils;

    @Autowired
    public VisitController(VisitService service, SecurityUtils securityUtils) {
        this.service = service;
        this.securityUtils = securityUtils;
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> reservation(@RequestBody ReservationRequest request) {
        Visit reservation = service.reservation(Visit.convertEntity(request));
        return ResponseEntity.ok(ReservationResponse.convert(reservation, securityUtils));
    }

    @RequireAuth
    @GetMapping("/find")
    public ResponseEntity<ReservationResponse> findReservation(ReservationSearchRequest request) {
        Visit reservation = service.findReservation(request);
        return ResponseEntity.ok(ReservationResponse.convert(reservation, securityUtils));
    }

    @RequireAuth
    @PutMapping("/{id}")
    public ResponseEntity<ReservationResponse> updateReservation(@PathVariable Long id, @RequestBody ReservationRequest request) {
        Visit reservation = service.updateReservation(id, Visit.convertEntity(request, "name", "phoneNum", "password"));
        return ResponseEntity.ok(ReservationResponse.convert(reservation, securityUtils));
    }

    @RequireAuth
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelReservation(@PathVariable Long id) {
        service.cancelReservation(id);
        return ResponseEntity.ok().build();
    }

    @RequireAdmin
    @GetMapping("/all")
    public ResponseEntity<List<ReservationResponse>> findAll() {
        List<ReservationResponse> responses = service.findAll().stream()
                .map(visit -> ReservationResponse.convert(visit, securityUtils))
                .toList();
        return ResponseEntity.ok(responses);
    }

    @RequireAdmin
    @PostMapping("/{id}/confirm")
    public ResponseEntity<Void> confirm(@PathVariable Long id) {
        service.confirm(id);
        return ResponseEntity.ok().build();
    }

    @RequireAdmin
    @PostMapping("/{id}/reject")
    public ResponseEntity<Void> reject(@PathVariable Long id, @RequestBody ReservationStatusRequest request) {
        service.reject(id, request);
        return ResponseEntity.ok().build();
    }

}