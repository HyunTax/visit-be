package com.sht4873.reservation.domain.visiitor.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReservationSearchRequest {
    private String name; // 이름
    private String phoneNum; // 잔화번호
    private String password; // 비밀번호
}
