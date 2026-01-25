package com.sht4873.reservation.domain.visiitor;

import com.sht4873.reservation.domain.visiitor.dto.request.ReservationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/visit")
public class VisitController {

    private final VisitService service;

    @Autowired
    public VisitController(VisitService service) {
        this.service = service;
    }

    @PostMapping("/reservation")
    public ResponseEntity<?> reservation(@RequestBody ReservationRequest request) {
        Visit reservation = service.reservation(Visit.convertEntity(request));
        return ResponseEntity.ok(reservation);
    }
}
