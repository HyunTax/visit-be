package com.sht4873.reservation.domain.auth;

import com.sht4873.reservation.domain.auth.dto.request.AdminAuthRequest;
import com.sht4873.reservation.domain.auth.dto.request.AuthRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/visit/auth")
public class AuthController {
    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<String> issueToken(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(service.issueToken(request));
    }

    @PostMapping("/admin")
    public ResponseEntity<String> issueAdminToken(@RequestBody AdminAuthRequest request) {
        return ResponseEntity.ok(service.issueAdminToken(request));
    }
}
