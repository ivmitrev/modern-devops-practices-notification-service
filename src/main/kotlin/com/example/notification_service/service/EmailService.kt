package com.example.notification_service.service

import com.example.notification_service.model.Notification
import jakarta.mail.internet.MimeMessage
import org.slf4j.LoggerFactory
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class EmailService(
    private val mailSender: JavaMailSender
) {

    private val logger = LoggerFactory.getLogger(EmailService::class.java)

    fun sendEmail(notification: Notification): Boolean {
        return try {
            logger.info("Preparing to send email to: ${notification.recipient}")

            val message: MimeMessage = mailSender.createMimeMessage()
            val helper = MimeMessageHelper(message, true, "UTF-8")

            helper.setTo(notification.recipient)
            helper.setSubject(notification.subject ?: "Notification from Notification Service")
            helper.setText(buildEmailBody(notification), true) // true = HTML
            helper.setFrom("noreply@notificationservice.com")

            mailSender.send(message)

            logger.info("Email sent successfully to: ${notification.recipient}")
            true

        } catch (e: Exception) {
            logger.error("Failed to send email to: ${notification.recipient}", e)
            throw e
        }
    }

    private fun buildEmailBody(notification: Notification): String {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        line-height: 1.6;
                        color: #333;
                    }
                    .container {
                        max-width: 600px;
                        margin: 0 auto;
                        padding: 20px;
                        border: 1px solid #ddd;
                        border-radius: 5px;
                    }
                    .header {
                        background-color: #4CAF50;
                        color: white;
                        padding: 10px;
                        text-align: center;
                        border-radius: 5px 5px 0 0;
                    }
                    .content {
                        padding: 20px;
                        background-color: #f9f9f9;
                    }
                    .footer {
                        margin-top: 20px;
                        padding: 10px;
                        text-align: center;
                        font-size: 12px;
                        color: #777;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h2>📧 Notification Service</h2>
                    </div>
                    <div class="content">
                        <p><strong>Hello,</strong></p>
                        <p>${notification.message}</p>
                    </div>
                    <div class="footer">
                        <p>User ID: ${notification.userId}</p>
                        <p>Sent at: ${LocalDateTime.now()}</p>
                        <p>This is an automated message from Notification Service.</p>
                    </div>
                </div>
            </body>
            </html>
        """.trimIndent()
    }
}