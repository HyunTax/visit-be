package com.sht4873.reservation.core.event;

import com.sht4873.reservation.core.enums.MailType;
import com.sht4873.reservation.domain.visitor.Visit;

public record MailEvent(Visit reservation, MailType mailType) {
}