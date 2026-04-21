package com.example.webapp.service;

import java.time.LocalDate;
import java.time.LocalTime;

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

    @Value("${app.clinic.name:TT Care+}")
    private String clinicName;

    @Value("${app.clinic.address:Yen Nghia, Ha Dong, Ha Noi}")
    private String clinicAddress;

    @Value("${app.clinic.phone:0985081624}")
    private String clinicPhone;

    @Value("${app.clinic.email:support@ttcare.vn}")
    private String clinicEmail;

    @Value("${app.clinic.signature:TT Care+}")
    private String clinicSignature;

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

    public boolean sendAppointmentConfirmedEmail(
            String toEmail,
            String customerName,
            String doctorName,
            String roomName,
            LocalDate appointmentDate,
            LocalTime appointmentTime) {
        if (toEmail == null || !toEmail.contains("@")) {
            logger.warn("Skip sending confirmation email because recipient is not a valid email: {}", toEmail);
            return false;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setReplyTo(replyToEmail);
            message.setTo(toEmail);
            message.setSubject("Lịch hẹn đã được bác sĩ xác nhận - TT Care+");

            String body = buildAppointmentConfirmedEmailBody(
                    customerName,
                    doctorName,
                    roomName,
                    appointmentDate,
                    appointmentTime);
            message.setText(body);

            mailSender.send(message);
            logger.info("Appointment confirmation email sent successfully to: {}", toEmail);
            return true;
        } catch (MailException e) {
            logger.error("SMTP send failed for {}. Message: {}", toEmail, e.getMessage(), e);
            return false;
        } catch (Exception e) {
            logger.error("Unexpected error when sending appointment confirmation email to {}", toEmail, e);
            return false;
        }
    }

    public boolean sendAppointmentCompletedEmail(
            String toEmail,
            String customerName,
            String doctorName,
            LocalDate appointmentDate) {
        if (toEmail == null || !toEmail.contains("@")) {
            logger.warn("Skip sending completed email because recipient is not a valid email: {}", toEmail);
            return false;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setReplyTo(replyToEmail);
            message.setTo(toEmail);
            message.setSubject("Cảm ơn quý khách đã đến khám tại " + clinicName);
            message.setText(buildAppointmentCompletedEmailBody(customerName, doctorName, appointmentDate));
            mailSender.send(message);
            logger.info("Appointment completed email sent successfully to: {}", toEmail);
            return true;
        } catch (MailException e) {
            logger.error("SMTP send failed for {}. Message: {}", toEmail, e.getMessage(), e);
            return false;
        } catch (Exception e) {
            logger.error("Unexpected error when sending appointment completed email to {}", toEmail, e);
            return false;
        }
    }

    public boolean sendAppointmentMissedEmail(
            String toEmail,
            String customerName,
            String doctorName,
            LocalDate appointmentDate,
            LocalTime appointmentTime) {
        if (toEmail == null || !toEmail.contains("@")) {
            logger.warn("Skip sending missed email because recipient is not a valid email: {}", toEmail);
            return false;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setReplyTo(replyToEmail);
            message.setTo(toEmail);
            message.setSubject("Lịch hẹn đã bị bỏ lỡ - TT Care+");
            message.setText(
                    buildAppointmentMissedEmailBody(customerName, doctorName, appointmentDate, appointmentTime));
            mailSender.send(message);
            logger.info("Appointment missed email sent successfully to: {}", toEmail);
            return true;
        } catch (MailException e) {
            logger.error("SMTP send failed for {}. Message: {}", toEmail, e.getMessage(), e);
            return false;
        } catch (Exception e) {
            logger.error("Unexpected error when sending appointment missed email to {}", toEmail, e);
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

    private String buildAppointmentConfirmedEmailBody(
            String customerName,
            String doctorName,
            String roomName,
            LocalDate appointmentDate,
            LocalTime appointmentTime) {
        String displayCustomer = customerName != null && !customerName.trim().isEmpty() ? customerName : "Quy khach";
        String displayDoctor = doctorName != null && !doctorName.trim().isEmpty() ? doctorName : "Bac si";
        String displayClinicName = roomName != null && !roomName.trim().isEmpty() ? roomName : clinicName;
        String displayDate = appointmentDate != null ? appointmentDate.toString() : "(chua cap nhat)";
        String displayTime = appointmentTime != null ? appointmentTime.toString() : "(chua cap nhat)";

        return "Kinh gui " + displayCustomer + ",\n\n"
                + "Phong kham " + displayClinicName + " xin xac nhan lich hen cua quy khach:\n\n"
                + "Thoi gian: " + displayDate + ", " + displayTime + "\n\n"
                + "Bac si phu trach: " + displayDoctor + "\n\n"
                + "Dia diem: " + clinicAddress + "\n\n"
                + "Quy khach vui long den truoc 10 phut de hoan tat thu tuc. Neu co thay doi, xin lien he qua so "
                + clinicPhone + ".\n\n"
                + "Tran trong,\n"
                + clinicSignature;
    }

    private String buildAppointmentCompletedEmailBody(
            String customerName,
            String doctorName,
            LocalDate appointmentDate) {
        String displayCustomer = customerName != null && !customerName.trim().isEmpty() ? customerName : "Quy khach";
        String displayDate = appointmentDate != null ? appointmentDate.toString() : "(chua cap nhat)";

        return "Kinh gui " + displayCustomer + ",\n\n"
                + "Phong kham " + clinicName
                + " chan thanh cam on quy khach da tin tuong va su dung dich vu cua chung toi ngay "
                + displayDate + ".\n\n"
                + "Chung toi hy vong quy khach hai long voi trai nghiem va ket qua tham kham. Neu co bat ky thac mac hoac can ho tro them, xin vui long lien he qua so "
                + clinicPhone + " hoac email " + clinicEmail + ".\n\n"
                + "Chuc quy khach suc khoe va hen gap lai.\n\n"
                + "Tran trong,\n"
                + clinicSignature;
    }

    private String buildAppointmentMissedEmailBody(
            String customerName,
            String doctorName,
            LocalDate appointmentDate,
            LocalTime appointmentTime) {
        String displayCustomer = customerName != null && !customerName.trim().isEmpty() ? customerName : "khách hàng";
        String displayDoctor = doctorName != null && !doctorName.trim().isEmpty() ? doctorName : "bác sĩ";
        String displayDate = appointmentDate != null ? appointmentDate.toString() : "(chưa cập nhật)";
        String displayTime = appointmentTime != null ? appointmentTime.toString() : "(chưa cập nhật)";

        return "Xin chào " + displayCustomer + ",\n\n"
                + "Chúng tôi rất tiếc vì bạn đã bỏ lỡ lịch hẹn với " + displayDoctor + " vào " + displayDate + " lúc "
                + displayTime + ".\n"
                + "TT Care+ thành thật xin lỗi nếu có bất kỳ bất tiện nào và luôn sẵn sàng hỗ trợ bạn đặt lại lịch.\n"
                + "Vui lòng đăng nhập hệ thống để chọn khung giờ phù hợp hơn.\n\n"
                + "Trân trọng,\n"
                + "TT Care+";
    }
}
