package Gold40.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;





@Service
    public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // Thay đổi phương thức sendEmail để gửi email HTML
    public void sendEmail(String to, String subject, String body, boolean isHtml) throws MessagingException {
        // Tạo MimeMessage thay vì SimpleMailMessage
        MimeMessage message = mailSender.createMimeMessage();

        // Sử dụng MimeMessageHelper để hỗ trợ gửi HTML
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, isHtml); // isHtml = true nếu muốn gửi email HTML

        // Gửi email
        mailSender.send(message);
    }
}
