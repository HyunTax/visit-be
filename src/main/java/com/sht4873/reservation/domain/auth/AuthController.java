package com.sht4873.reservation.domain.auth;

import com.sht4873.reservation.domain.auth.dto.request.AuthRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/visit/auth")
public class AuthController {
    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<String> issueToken(@RequestBody AuthRequest request) {
        String issuedToken = service.issueToken(request);
        return ResponseEntity.ok(issuedToken);
    }
}
