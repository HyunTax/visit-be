package com.sht4873.reservation.domain.visitor;

import com.sht4873.reservation.core.aop.auth.RequireAuth;
import com.sht4873.reservation.domain.visitor.dto.request.ReservationRequest;
import com.sht4873.reservation.domain.visitor.dto.request.ReservationSearchRequest;
import com.sht4873.reservation.domain.visitor.dto.response.ReservationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/visit/reservation")
public class VisitController {

    private final VisitService service;

    public VisitController(VisitService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> reservation(@RequestBody ReservationRequest request) {
        Visit reservation = service.reservation(Visit.convertEntity(request));
        return ResponseEntity.ok(ReservationResponse.convert(reservation));
    }

    @RequireAuth
    @GetMapping("/find")
    public ResponseEntity<ReservationResponse> findReservation(ReservationSearchRequest request) {
        Visit reservation = service.findReservation(request);
        return ResponseEntity.ok(ReservationResponse.convert(reservation));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ReservationResponse>> finaALl() {
        List<ReservationResponse> responses = service.finaAll().stream().map(ReservationResponse::convert).toList();
        return ResponseEntity.ok(responses);
    }

    @RequireAuth
    @PutMapping("/{id}")
    public ResponseEntity<ReservationResponse> updateReservation(@PathVariable Long id, @RequestBody ReservationRequest request) {
        Visit reservation = service.updateReservation(id, Visit.convertEntity(request, "name", "phoneNum", "password"));
        return ResponseEntity.ok(ReservationResponse.convert(reservation));
    }

    @RequireAuth
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelReservation(@PathVariable Long id) {
        service.cancelReservation(id);
        return ResponseEntity.ok().build();
    }
}
