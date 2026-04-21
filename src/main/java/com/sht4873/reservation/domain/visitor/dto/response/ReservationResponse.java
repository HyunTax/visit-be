package com.sht4873.reservation.domain.visitor.dto.response;

import com.sht4873.reservation.core.util.SecurityUtils;
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
    private Boolean hasAllergy; // 알러지 유무
    private String memo; // 메모
    private String status; // 상태
    private String statusMemo; // 상태 메모 (REJECT 한정)

    public static ReservationResponse convert(Visit visit, SecurityUtils securityUtils) {
        ReservationResponse response = new ReservationResponse();
        BeanUtils.copyProperties(visit, response);
        response.setPhoneNum(securityUtils.decryptPhone(visit.getPhoneNum()));
        response.setStatus(visit.getStatus().name());
        return response;
    }
}