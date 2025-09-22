package com.myproject.FormApp.Service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

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



}
