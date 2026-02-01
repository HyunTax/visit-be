package com.sht4873.reservation.domain.visiitor.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ReservationRequest {
    private String name; // 이름
    private String phoneNum; // 잔화번호
    private LocalDate visitDate; // 방문일
    private Long visitorCount; // 방문인원
    private String visitorDescription; // 방문자 설명
    private String momo; // 메모
    private String password; // 비밀번호
}
