package com.sht4873.reservation.domain.visitor;

import com.sht4873.reservation.domain.visitor.dto.request.ReservationRequest;
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
    @Column(name = "PASSWORD")
    @NotNull(message = "비밀번호는 필수입니다.")
    private String password;
    @Column(name = "ALLERGY_YN")
    @NotNull(message = "알러지 여부는 필수입니다.")
    private Boolean hasAllergy;
    @Column(name = "MEMO")
    private String memo;
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private Status status;
    @Column(name = "STATUS_MEMO")
    private String statusMemo;

    public static Visit convertEntity(ReservationRequest request, String... ignoreProperties) {
        Visit entity = new Visit();
        BeanUtils.copyProperties(request, entity, ignoreProperties);
        return entity;
    }

    public enum Status {
        WAIT, CONFIRM, REJECT, CANCEL
    }
}
