package com.sht4873.reservation.domain.auth.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequest {
    private String name;
    private String phoneNum;
    private String password;
}
