package com.example.notification_service.dto

import com.example.notification_service.model.Notification
import com.example.notification_service.model.NotificationStatus
import com.example.notification_service.model.NotificationType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.*

class NotificationResponseTest {

    @Test
    fun `should create NotificationResponse from Notification`() {
        // Given
        val notification = Notification(
            id = UUID.randomUUID(),
            userId = "user123",
            type = NotificationType.EMAIL,
            recipient = "test@example.com",
            subject = "Test",
            message = "Test message",
            status = NotificationStatus.SENT,
            createdAt = LocalDateTime.now(),
            sentAt = LocalDateTime.now()
        )

        // When
        val response = NotificationResponse.from(notification)

        // Then
        assertEquals(notification.id, response.id)
        assertEquals(notification.userId, response.userId)
        assertEquals(notification.type, response.type)
        assertEquals(notification.recipient, response.recipient)
        assertEquals(notification.subject, response.subject)
        assertEquals(notification.message, response.message)
        assertEquals(notification.status, response.status)
        assertEquals(notification.createdAt, response.createdAt)
        assertEquals(notification.sentAt, response.sentAt)
        assertEquals(notification.error, response.error)
    }

    @Test
    fun `should handle null fields correctly`() {
        // Given
        val notification = Notification(
            id = UUID.randomUUID(),
            userId = "user123",
            type = NotificationType.WEBHOOK,
            recipient = "https://webhook.site/test",
            subject = null,
            message = "Test",
            status = NotificationStatus.PENDING,
            sentAt = null,
            error = null
        )

        // When
        val response = NotificationResponse.from(notification)

        // Then
        assertNull(response.subject)
        assertNull(response.sentAt)
        assertNull(response.error)
    }
}
