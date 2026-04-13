package com.sht4873.reservation.domain.visitor.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ReservationRequest {
    private String name; // 이름
    private String phoneNum; // 전화번호
    private LocalDate visitDate; // 방문일
    private Long visitorCount; // 방문인원
    private String password; // 비밀번호
    private Boolean hasAllergy; // 알러지 유무
    private String memo; // 메모
}
