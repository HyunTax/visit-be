package com.sht4873.reservation.core.enums;

import lombok.Getter;

@Getter
public enum MailType {
    REQUEST("스위트홈 예약 요청", "새로운 방문 요청이 들어왔습니다.", "#NAME#님이 방문 예약을 요청했습니다<br>확인 후 승인 또는 거절해 주세요."),
    CHANGE("스위트홈 예약 변경", "방문 수정 요청이 들어왔습니다.", "#NAME#님이 방문 예약을 수정 요청했습니다<br>확인 후 승인 또는 거절해 주세요."),
    CANCEL("스위트홈 예약 취소", "방문 예약이 취소 되었습니다.", "#NAME#님이 방문 예약을 취소 하였습니다.");

    private final String title;
    private final String content;
    private final String subContent;

    MailType(String title, String content, String subContent) {
        this.title = title;
        this.content = content;
        this.subContent = subContent;
    }
}
