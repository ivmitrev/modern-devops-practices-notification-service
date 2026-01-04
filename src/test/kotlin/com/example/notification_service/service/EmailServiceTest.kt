package com.example.notification_service.service

import com.example.notification_service.model.Notification
import com.example.notification_service.model.NotificationStatus
import com.example.notification_service.model.NotificationType
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import jakarta.mail.internet.MimeMessage
import org.junit.jupiter.api.Assertions.assertTrue
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.mail.MailSendException
import org.springframework.mail.javamail.JavaMailSender
import java.util.*

class EmailServiceTest {

    private lateinit var mailSender: JavaMailSender
    private lateinit var emailService: EmailService

    @BeforeEach
    fun setup() {
        mailSender = mockk()
        emailService = EmailService(mailSender)
    }

    @Test
    fun `should send email successfully`() {
        // Given
        val notification = Notification(
            id = UUID.randomUUID(),
            userId = "user123",
            type = NotificationType.EMAIL,
            recipient = "test@example.com",
            subject = "Test Subject",
            message = "Test message",
            status = NotificationStatus.PENDING
        )

        val mimeMessage = mockk<MimeMessage>(relaxed = true)
        every { mailSender.createMimeMessage() } returns mimeMessage
        every { mailSender.send(any<MimeMessage>()) } just Runs

        // When
        val result = emailService.sendEmail(notification)

        // Then
        assertTrue(result)
        verify(exactly = 1) { mailSender.createMimeMessage() }
        verify(exactly = 1) { mailSender.send(any<MimeMessage>()) }
    }

    @Test
    fun `should send email with default subject when subject is null`() {
        // Given
        val notification = Notification(
            id = UUID.randomUUID(),
            userId = "user123",
            type = NotificationType.EMAIL,
            recipient = "test@example.com",
            subject = null,
            message = "Test message",
            status = NotificationStatus.PENDING
        )

        val mimeMessage = mockk<MimeMessage>(relaxed = true)
        every { mailSender.createMimeMessage() } returns mimeMessage
        every { mailSender.send(any<MimeMessage>()) } just Runs

        // When
        val result = emailService.sendEmail(notification)

        // Then
        assertTrue(result)
    }

    @Test
    fun `should throw exception when email sending fails`() {
        // Given
        val notification = Notification(
            id = UUID.randomUUID(),
            userId = "user123",
            type = NotificationType.EMAIL,
            recipient = "invalid@example.com",
            message = "Test",
            status = NotificationStatus.PENDING
        )

        every { mailSender.createMimeMessage() } throws
                MailSendException("SMTP connection failed")

        // When & Then
        assertThrows(MailSendException::class.java) {
            emailService.sendEmail(notification)
        }
    }
}
