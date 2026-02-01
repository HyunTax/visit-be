package com.sht4873.reservation.domain.visiitor;

import com.sht4873.reservation.domain.visiitor.dto.request.ReservationRequest;
import com.sht4873.reservation.domain.visiitor.dto.request.ReservationSearchRequest;
import com.sht4873.reservation.domain.visiitor.dto.response.ReservationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/visit/reservation")
public class VisitController {

    private final VisitService service;

    @Autowired
    public VisitController(VisitService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> reservation(@RequestBody ReservationRequest request) {
        Visit reservation = service.reservation(Visit.convertEntity(request));
        return ResponseEntity.ok(ReservationResponse.convert(reservation));
    }

    @GetMapping("/find")
    public ResponseEntity<?> findReservation(ReservationSearchRequest request) throws Exception {
        Visit reservation = service.findReservation(request);
        return ResponseEntity.ok(ReservationResponse.convert(reservation));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ReservationResponse>> finaALl() {
        List<ReservationResponse> responses = service.finaAll().stream().map(ReservationResponse::convert).toList();
        return ResponseEntity.ok(responses);
    }
}
