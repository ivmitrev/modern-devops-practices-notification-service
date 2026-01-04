package com.example.notification_service.service

import com.example.notification_service.dto.NotificationRequest
import com.example.notification_service.model.Notification
import com.example.notification_service.model.NotificationStatus
import com.example.notification_service.model.NotificationType
import com.example.notification_service.repository.NotificationRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class NotificationServiceTest {

    private lateinit var repository: NotificationRepository
    private lateinit var emailService: EmailService
    private lateinit var webhookService: WebhookService
    private lateinit var service: NotificationService

    @BeforeEach
    fun setup() {
        repository = mockk()
        emailService = mockk()
        webhookService = mockk()
        service = NotificationService(repository, emailService, webhookService)
    }

    @Test
    fun `should create and send email notification successfully`() {
        // Given
        val request = NotificationRequest(
            userId = "user123",
            type = NotificationType.EMAIL,
            recipient = "test@example.com",
            subject = "Test",
            message = "Test message"
        )

        every { repository.save(any()) } answers { firstArg() }
        every { emailService.sendEmail(any()) } returns true

        // When
        val result = service.createAndSend(request)

        // Then
        assertEquals(NotificationStatus.SENT, result.status)
        assertNotNull(result.sentAt)
        verify(exactly = 1) { emailService.sendEmail(any()) }
        verify(exactly = 2) { repository.save(any()) }
    }

    @Test
    fun `should create and send webhook notification successfully`() {
        // Given
        val request = NotificationRequest(
            userId = "user123",
            type = NotificationType.WEBHOOK,
            recipient = "https://webhook.site/test",
            message = "Test webhook"
        )

        every { repository.save(any()) } answers { firstArg() }
        every { webhookService.sendWebhook(any()) } returns true

        // When
        val result = service.createAndSend(request)

        // Then
        assertEquals(NotificationStatus.SENT, result.status)
        assertNotNull(result.sentAt)
        verify(exactly = 1) { webhookService.sendWebhook(any()) }
    }

    @Test
    fun `should mark notification as failed when email sending fails`() {
        // Given
        val request = NotificationRequest(
            userId = "user123",
            type = NotificationType.EMAIL,
            recipient = "test@example.com",
            message = "Test message"
        )

        every { repository.save(any()) } answers { firstArg() }
        every { emailService.sendEmail(any()) } throws RuntimeException("Email sending failed")

        // When
        val result = service.createAndSend(request)

        // Then
        assertEquals(NotificationStatus.FAILED, result.status)
        assertEquals("Email sending failed", result.error)
        assertNull(result.sentAt)
    }

    @Test
    fun `should throw exception for invalid email format`() {
        // Given
        val request = NotificationRequest(
            userId = "user123",
            type = NotificationType.EMAIL,
            recipient = "invalid-email",
            message = "Test"
        )

        // When & Then
        assertThrows<IllegalArgumentException> {
            service.createAndSend(request)
        }
    }

    @Test
    fun `should throw exception for invalid webhook URL`() {
        // Given
        val request = NotificationRequest(
            userId = "user123",
            type = NotificationType.WEBHOOK,
            recipient = "invalid-url",
            message = "Test"
        )

        // When & Then
        assertThrows<IllegalArgumentException> {
            service.createAndSend(request)
        }
    }

    @Test
    fun `should get notification by id successfully`() {
        // Given
        val id = UUID.randomUUID()
        val notification = Notification(
            id = id,
            userId = "user123",
            type = NotificationType.EMAIL,
            recipient = "test@example.com",
            message = "Test",
            status = NotificationStatus.SENT
        )

        every { repository.findById(id) } returns Optional.of(notification)

        // When
        val result = service.getById(id)

        // Then
        assertEquals(id, result.id)
        assertEquals("user123", result.userId)
    }

    @Test
    fun `should throw exception when notification not found`() {
        // Given
        val id = UUID.randomUUID()
        every { repository.findById(id) } returns Optional.empty()

        // When & Then
        assertThrows<NoSuchElementException> {
            service.getById(id)
        }
    }

    @Test
    fun `should get all notifications for user`() {
        // Given
        val userId = "user123"
        val notifications = listOf(
            Notification(
                id = UUID.randomUUID(),
                userId = userId,
                type = NotificationType.EMAIL,
                recipient = "test@example.com",
                message = "Test",
                status = NotificationStatus.SENT
            )
        )

        every { repository.findByUserIdOrderByCreatedAtDesc(userId) } returns notifications

        // When
        val result = service.getByUserId(userId)

        // Then
        assertEquals(1, result.size)
        assertEquals(userId, result[0].userId)
    }

    @Test
    fun `should get all notifications`() {
        // Given
        val notifications = listOf(
            Notification(
                id = UUID.randomUUID(),
                userId = "user1",
                type = NotificationType.EMAIL,
                recipient = "test@example.com",
                message = "Test",
                status = NotificationStatus.SENT
            )
        )

        every { repository.findAll() } returns notifications

        // When
        val result = service.getAllNotifications()

        // Then
        assertEquals(1, result.size)
    }
}
