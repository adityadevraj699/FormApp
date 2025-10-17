package com.myproject.FormApp.Service;

import java.time.LocalDate;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // Send simple text email
    public void sendEmail(String to, String subject, String message) {
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(to);
            mail.setSubject(subject);
            mail.setText(message);
            mailSender.send(mail);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

 // ------------------- Teacher -------------------
    public void sendTeacherStatusUpdate(String to, String name, String employeeId, String status, String email) {
        String subject = "Teacher Account Status Updated";
        String msg = "Hello " + name + ",\n\n"
                   + "Your Teacher account (Employee ID: " + employeeId + ", Email: " + email + ") status has been changed to: " + status + ".\n\n"
                   + "Regards,\nAdmin Team";
        sendEmail(to, subject, msg);
    }


        // --- Method to send assignment email ---
        public void sendTeacherProgramAssignment(String to, String subject, String htmlContent) {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

                helper.setTo(to);
                helper.setSubject(subject);
                helper.setText(htmlContent, true); // true = send as HTML

                mailSender.send(message);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }

        // --- Build HTML email template ---
        public String buildProgramAssignmentTemplate(String teacherName,
                                                     String trainingProgram,
                                                     String course,
                                                     String branch,
                                                     String year,
                                                     String section,
                                                     String semester,
                                                     LocalDate startDate,
                                                     LocalDate endDate) {

            return """
            <html>
            <body style="font-family: 'Segoe UI', sans-serif; background-color: #f4f7fc; margin: 0; padding: 0;">
                <div style="max-width: 650px; margin: 40px auto; background-color: #ffffff; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); overflow: hidden;">
                    
                    <div style="background-color: #003366; padding: 20px 30px; text-align: center;">
                        <h2 style="color: #ffffff; margin: 0;">Meerut Institute of Technology</h2>
                        <p style="color: #d1e4ff; font-size: 14px; margin: 5px 0 0;">Empowering Excellence in Education</p>
                    </div>
                    
                    <div style="padding: 30px;">
                        <p style="font-size: 16px;">Dear <strong>%s</strong>,</p>
                        <p style="font-size: 15px; color: #333;">
                            You have been <strong>assigned</strong> to the following training program. Please review the details carefully.
                        </p>
                        
                        <table style="width: 100%%; border-collapse: collapse; margin: 20px 0;">
                            <tr style="background-color: #f0f4fa;">
                                <td style="padding: 10px; font-weight: bold;">Program Name:</td>
                                <td style="padding: 10px;">%s</td>
                            </tr>
                            <tr>
                                <td style="padding: 10px; font-weight: bold;">Course:</td>
                                <td style="padding: 10px;">%s</td>
                            </tr>
                            <tr style="background-color: #f0f4fa;">
                                <td style="padding: 10px; font-weight: bold;">Branch:</td>
                                <td style="padding: 10px;">%s</td>
                            </tr>
                            <tr>
                                <td style="padding: 10px; font-weight: bold;">Year:</td>
                                <td style="padding: 10px;">%s</td>
                            </tr>
                            <tr style="background-color: #f0f4fa;">
                                <td style="padding: 10px; font-weight: bold;">Section:</td>
                                <td style="padding: 10px;">%s</td>
                            </tr>
                            <tr>
                                <td style="padding: 10px; font-weight: bold;">Semester:</td>
                                <td style="padding: 10px;">%s</td>
                            </tr>
                            <tr style="background-color: #f0f4fa;">
                                <td style="padding: 10px; font-weight: bold;">Duration:</td>
                                <td style="padding: 10px;">%s - %s</td>
                            </tr>
                        </table>
                        
                        <p style="font-size: 15px; color: #333;">
                            We look forward to your valuable contribution and dedicated effort in delivering this program effectively.
                        </p>

                        <p style="font-size: 15px; color: #333;">Best regards,<br>
                        <strong>Admin Team</strong><br>
                        Meerut Institute of Technology</p>
                    </div>

                    <div style="background-color: #003366; padding: 10px 20px; text-align: center; color: #ffffff; font-size: 12px;">
                        © 2025 Meerut Institute of Technology. All Rights Reserved.
                    </div>
                </div>
            </body>
            </html>
            """.formatted(
                    teacherName,
                    trainingProgram,
                    course != null ? course : "N/A",
                    branch != null ? branch : "N/A",
                    year != null ? year : "N/A",
                    section != null ? section : "N/A",
                    semester != null ? semester : "N/A",
                    startDate != null ? startDate : "N/A",
                    endDate != null ? endDate : "N/A"
            );
        }

    // ------------------- Student -------------------
    public void sendStudentStatusUpdate(String to, String name, String rollNo, String role, String status) {
        String subject = "Student Account Status Updated";
        String msg = "Hello " + name + ",\n\n"
                   + "Your Student account (Roll No: " + rollNo + ", Role: " + role + ") status has been changed to: " + status + ".\n\n"
                   + "Regards,\nAdmin Team";
        sendEmail(to, subject, msg);
    }
    
 // ------------------- Teacher Program Assignment -------------------
    public void sendTeacherProgramAssignment(String to, String teacherName, String programName, String programDetails) {
        String subject = "You Have Been Assigned to a New Program";
        String msg = "Hello " + teacherName + ",\n\n"
                   + "You have been successfully assigned to the program: " + programName + ".\n\n"
                   + "Program Details:\n" + programDetails + "\n\n"
                   + "Please check your dashboard for more information.\n\n"
                   + "Regards,\nAdmin Team";

        sendEmail(to, subject, msg);
    }
    
    
    
    
    
 // Send HTML email
    public void sendHtmlEmail(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true); // true = HTML
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    // OTP Email template
    public void sendOtpEmail(String to, String name, int otp) {
        String subject = "Your OTP for College ERP Password Reset";
        
        // HTML template with university branding
        String htmlBody = "<!DOCTYPE html>"
                + "<html><head>"
                + "<meta charset='UTF-8'>"
                + "<title>OTP Verification</title>"
                + "</head>"
                + "<body style='font-family: Arial, sans-serif; background-color: #f2f2f2; padding: 20px;'>"
                + "<div style='max-width: 600px; margin: auto; background-color: #fff; border-radius: 8px; padding: 20px; box-shadow: 0 0 10px rgba(0,0,0,0.1);'>"
                + "<h2 style='color: #2575fc;'>MIT Meerut College ERP</h2>"
                + "<p>Hi <strong>" + name + "</strong>,</p>"
                + "<p>You requested a password reset. Use the following OTP to proceed:</p>"
                + "<h1 style='text-align: center; color: #ff5722;'>" + otp + "</h1>"
                + "<p style='font-size: 14px; color: #555;'>This OTP is valid for 10 minutes.</p>"
                + "<hr>"
                + "<p style='font-size: 12px; color: #888;'>If you did not request this, please ignore this email.</p>"
                + "<p style='font-size: 12px; color: #888;'>MIT Meerut ERP Team</p>"
                + "</div></body></html>";

        sendHtmlEmail(to, subject, htmlBody);
    }



}
