package com.sht4873.reservation.domain.visiitor;

import com.sht4873.reservation.domain.visiitor.dto.request.ReservationRequest;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.time.LocalDate;

@Entity(name = "VISITOR")
@Getter
@Setter
public class Visit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    @Column(name = "NAME")
    @NotEmpty(message = "이름은 필수입니다.")
    private String name;
    @Column(name = "PHONE_NUM")
    @NotEmpty(message = "휴대폰 번호는 필수입니다.")
    private String phoneNum;
    @Column(name = "VISIT_DATE")
    @NotNull(message = "날짜 선택은 필수입니다.")
    private LocalDate visitDate;
    @Column(name = "VISIT_COUNT")
    @NotNull(message = "방문자 수 입력은 필수입니다.")
    private Long visitorCount;
    @Column(name = "VISIT_DESCRIPTION")
    private String visitorDescription;
    @Column(name = "MEMO")
    private String momo;

    public static Visit convertEntity(ReservationRequest request) {
        Visit entity = new Visit();
        BeanUtils.copyProperties(request, entity);
        return entity;
    }
}
