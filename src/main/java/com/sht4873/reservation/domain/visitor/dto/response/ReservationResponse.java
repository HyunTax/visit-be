package com.sht4873.reservation.domain.visitor.dto.response;

import com.sht4873.reservation.domain.visitor.Visit;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.time.LocalDate;

@Getter
@Setter
public class ReservationResponse {
    private Long id;
    private String name; // 이름
    private String phoneNum; // 전화번호
    private LocalDate visitDate; // 방문일
    private Long visitorCount; // 방문인원
    private String visitorDescription; // 방문자 설명
    private String memo; // 메모

    public static ReservationResponse convert(Visit visit) {
        ReservationResponse response = new ReservationResponse();
        BeanUtils.copyProperties(visit, response);
        return response;
    }
}