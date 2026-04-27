package com.sht4873.reservation.core.component;

import com.sht4873.reservation.core.event.MailEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class MailEventListener {

    private final MailComponent mailComponent;

    public MailEventListener(MailComponent mailComponent) {
        this.mailComponent = mailComponent;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(MailEvent event) {
        mailComponent.sendAdminMail(event.reservation(), event.mailType());
    }
}