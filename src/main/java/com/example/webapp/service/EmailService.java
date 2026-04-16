package com.example.webapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    // With Gmail SMTP, sender should be the authenticated account to avoid
    // rejection.
    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.mail.reply-to:support@ttcare.vn}")
    private String replyToEmail;

    public boolean sendRegistrationSuccessEmail(String toEmail, String customerName) {
        if (toEmail == null || !toEmail.contains("@")) {
            logger.warn("Skip sending registration email because recipient is not a valid email: {}", toEmail);
            return false;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setReplyTo(replyToEmail);
            message.setTo(toEmail);
            message.setSubject("Xác nhận đăng ký thành công - TT Care+");

            String body = buildRegistrationEmailBody(customerName);
            message.setText(body);

            mailSender.send(message);
            logger.info("Registration email sent successfully to: {}", toEmail);
            return true;
        } catch (MailException e) {
            logger.error("SMTP send failed for {}. Message: {}", toEmail, e.getMessage(), e);
            return false;
        } catch (Exception e) {
            logger.error("Unexpected error when sending registration email to {}", toEmail, e);
            return false;
        }
    }

    private String buildRegistrationEmailBody(String customerName) {
        String displayName = customerName != null && !customerName.trim().isEmpty() ? customerName : "khách hàng";

        return "Xin chào " + displayName + ",\n\n" +
                "Chúc mừng bạn đã đăng ký tài khoản thành công trên hệ thống đặt lịch khám bệnh trực tuyến của TT Care+.\n\n"
                +
                "Từ bây giờ, bạn có thể:\n" +
                "- Đăng nhập để quản lý thông tin cá nhân.\n" +
                "- Đặt lịch khám bệnh nhanh chóng và thuận tiện.\n" +
                "- Theo dõi lịch sử khám và kết quả xét nghiệm.\n\n" +
                "Nếu bạn có bất kỳ thắc mắc nào, vui lòng liên hệ với chúng tôi qua số điện thoại: 0123 456 789 hoặc email: support@ttcare.vn.\n\n"
                +
                "Cảm ơn bạn đã tin tưởng và lựa chọn dịch vụ của chúng tôi.\n\n" +
                "Trân trọng,\n" +
                "TT Care+";
    }
}
