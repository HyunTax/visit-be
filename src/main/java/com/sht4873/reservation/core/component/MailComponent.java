package com.sht4873.reservation.core.component;

import com.sht4873.reservation.core.enums.MailType;
import com.sht4873.reservation.core.util.SecurityUtils;
import com.sht4873.reservation.domain.visitor.Visit;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Slf4j
@Component
public class MailComponent {

    private final JavaMailSender mailSender;
    private final SecurityUtils securityUtils;

    @Value("${admin.mail}")
    private List<String> adminMails;
    @Value("${spring.mail.username}")
    private String from;
    @Value("${admin.page-url}")
    private String adminPageUrl;

    public MailComponent(JavaMailSender mailSender, SecurityUtils securityUtils) {
        this.mailSender = mailSender;
        this.securityUtils = securityUtils;
    }

    public void sendAdminMail(Visit reservation, MailType mailType) {
        adminMails.forEach(adminMail -> {
            try {
                log.info("메일 발송중 ... : [{}] {}({})", mailType.getTitle(), reservation.getName(), reservation.getVisitDate());
                send(adminMail, mailType.getTitle(), createMailTemplate(reservation, mailType));
                log.info("메일 발송 성공 : [{}] {}({})", mailType.getTitle(), reservation.getName(), reservation.getVisitDate());
            } catch (Exception e) {
                log.error("메일 발송 실패 : [{}] {}({})", mailType.getTitle(), reservation.getName(), reservation.getVisitDate(), e);
            }
        });
    }

    private void send(String to, String subject, String html) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
        helper.setFrom(String.format("스위트홈 <%s>", from));
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true);
        mailSender.send(message);
    }

    private String createMailTemplate(Visit reservation, MailType mailType) {
        String hasAllergyTemplate = """
                <div style="display:flex;gap:8px;margin-top:24px;flex-wrap:wrap;">
                  <span
                    style="display:inline-flex;align-items:center;gap:6px;padding:6px 14px;border-radius:999px;font-size:12px;font-weight:500;background:rgba(251,191,36,0.12);border:1px solid rgba(251,191,36,0.3);color:#fbbf24;">
                    <span style="font-size:11px;">⚠</span>
                    알러지 있음
                  </span>
                </div>
                """;
        String template = """
                <div
                  style="background:#e8e5e0;font-family:'Noto Sans KR',sans-serif;-webkit-font-smoothing:antialiased;padding:32px 16px;">
                  <div style="max-width:560px;margin:0 auto;">
                    <div
                      style="background:linear-gradient(160deg,#1a1410 0%,#2d1f0e 55%,#3d2a12 100%);border-radius:16px 16px 0 0;padding:40px 36px 36px;position:relative;overflow:hidden;">
                      <div style="display:flex;align-items:center;gap:10px;margin-bottom:20px;">
                        <span style="font-size:10px;letter-spacing:0.18em;color:#c8863c;font-weight:500;text-transform:uppercase;">SWEET
                          HOME</span>
                      </div>
                      <h1
                        style="font-family:'Noto Serif KR',serif;font-size:30px;font-weight:700;color:#f5ede0;line-height:1.25;margin-bottom:12px;">
                        #CONTENT#</h1>
                      <p style="font-size:13px;color:rgba(245,237,224,0.55);line-height:1.7;font-weight:300;">
                        #SUB_CONTENT#
                      </p>
                      #HASALLERGY#
                    </div>
                    <div style="background:#faf9f7;border-radius:0 0 16px 16px;padding:36px;border:1px solid #e2ddd6;border-top:none;">
                      <p
                        style="font-size:10px;letter-spacing:0.15em;text-transform:uppercase;color:#a09585;font-weight:500;margin-bottom:14px;">
                        예약 정보</p>
                      <div style="border:1px solid #e2ddd6;border-radius:12px;overflow:hidden;">
                        <div style="display:grid;grid-template-columns:1fr 1fr;border-bottom:1px solid #e2ddd6;">
                          <div style="padding:16px 20px;border-right:1px solid #e2ddd6;">
                            <div style="font-size:11px;color:#a09585;margin-bottom:5px;font-weight:400;">예약자</div>
                            <div style="font-size:15px;color:#1a1410;font-weight:500;">#NAME#</div>
                          </div>
                          <div style="padding:16px 20px;">
                            <div style="font-size:11px;color:#a09585;margin-bottom:5px;font-weight:400;">연락처</div>
                            <div style="font-size:15px;color:#1a1410;font-weight:500;">#PHONE_NUM#</div>
                          </div>
                        </div>
                        <div style="display:grid;grid-template-columns:1fr 1fr;">
                          <div style="padding:16px 20px;border-right:1px solid #e2ddd6;">
                            <div style="font-size:11px;color:#a09585;margin-bottom:5px;font-weight:400;">방문 날짜</div>
                            <div style="font-size:15px;color:#1a1410;font-weight:500;">#VISIT_DATE#</div>
                          </div>
                          <div style="padding:16px 20px;">
                            <div style="font-size:11px;color:#a09585;margin-bottom:5px;font-weight:400;">방문 인원</div>
                            <div style="font-size:15px;color:#1a1410;font-weight:500;">#VISIT_COUNT#명</div>
                          </div>
                        </div>
                      </div>
                      <div style="margin-top:12px;padding:16px 20px;background:#fff8ed;border-radius:0 8px 8px 0;">
                        <div style="font-size:10px;color:#a09585;letter-spacing:0.1em;text-transform:uppercase;margin-bottom:6px;">메모
                        </div>
                        <div style="font-size:13px;color:#3d2a12;line-height:1.65;">#MEMO#</div>
                      </div>
                      <div style="border:none;border-top:1px solid #e2ddd6;margin:28px 0;"></div>
                      <a href="#ADMIN_PAGE_URL#"
                        style="display:block;padding:16px 20px;border-radius:10px;text-align:center;font-size:14px;font-weight:600;text-decoration:none;letter-spacing:0.02em;background:#1a1410;color:#f5ede0;">
                        관리자 페이지로 이동</a>
                      <div style="margin-top:24px;padding-top:20px;border-top:1px solid #e2ddd6;">
                        <p style="font-size:11px;color:#b0a898;line-height:1.7;">
                          해당 메일은 스위트홈 예약 시스템에서 자동 발송되었습니다.
                        </p>
                      </div>
                    </div>
                  </div>
                </div>
                """;

        template = template.replace("#CONTENT#", mailType.getContent());
        template = template.replace("#SUB_CONTENT#", mailType.getSubContent());
        template = template.replace("#NAME#", reservation.getName());
        template = template.replace("#PHONE_NUM#", securityUtils.decryptPhone(reservation.getPhoneNum()));
        template = template.replace("#VISIT_DATE#", String.valueOf(reservation.getVisitDate()));
        template = template.replace("#VISIT_COUNT#", String.valueOf(reservation.getVisitorCount()));
        template = template.replace("#HASALLERGY#", reservation.getHasAllergy() ? hasAllergyTemplate : "");
        template = template.replace("#MEMO#", MailType.CANCEL.equals(mailType) ? ObjectUtils.isEmpty(reservation.getStatusMemo()) ? "" : reservation.getStatusMemo() : ObjectUtils.isEmpty(reservation.getMemo()) ? "" : reservation.getMemo());
        template = template.replace("#ADMIN_PAGE_URL#", adminPageUrl);

        return template;
    }
}
