package Gold40.Service;

import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class OtpService {

    @Autowired
    private EmailService emailService;

    // Method to generate a random OTP
    public String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);  // Generate a 6-digit OTP
        return String.valueOf(otp);
    }

    // Method to send OTP via email
    public void sendOtpToEmail(String email, String otp) {
        String subject = "Your OTP Code";
        String body = "Your OTP code is: " + otp;
        try {
            emailService.sendEmail(email, subject, body, false);  // false to send as plain text
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("Error sending OTP email.");
        }
    }

    // Method to verify OTP (you may need a way to store OTPs temporarily)
    public boolean verifyOtp(String email, String otp) {
        // Logic to check if OTP is valid (you may want to store OTP temporarily in a map or database)
        return true;  // Assuming OTP verification is successful for simplicity
    }
}

