package com.example.notification_service.controller

import com.example.notification_service.dto.NotificationRequest
import com.example.notification_service.model.Notification
import com.example.notification_service.model.NotificationStatus
import com.example.notification_service.model.NotificationType
import com.example.notification_service.service.NotificationService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import io.mockk.verify
import org.springframework.http.HttpStatus
import java.util.*

class NotificationControllerTest {

    private lateinit var notificationService: NotificationService
    private lateinit var controller: NotificationController

    @BeforeEach
    fun setup() {
        notificationService = mockk()
        controller = NotificationController(notificationService)
    }

    @Test
    fun `should create email notification successfully`() {
        // Given
        val request = NotificationRequest(
            userId = "user123",
            type = NotificationType.EMAIL,
            recipient = "test@example.com",
            subject = "Test",
            message = "Test message"
        )

        val notification = Notification(
            id = UUID.randomUUID(),
            userId = request.userId,
            type = request.type,
            recipient = request.recipient,
            subject = request.subject,
            message = request.message,
            status = NotificationStatus.SENT
        )

        every { notificationService.createAndSend(any()) } returns notification

        // When
        val response = controller.sendNotification(request)

        // Then
        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertNotNull(response.body)
        assertEquals("user123", response.body?.userId)
        assertEquals(NotificationStatus.SENT, response.body?.status)
        verify(exactly = 1) { notificationService.createAndSend(any()) }
    }

    @Test
    fun `should create webhook notification successfully`() {
        // Given
        val request = NotificationRequest(
            userId = "user456",
            type = NotificationType.WEBHOOK,
            recipient = "https://webhook.site/test",
            message = "Webhook test"
        )

        val notification = Notification(
            id = UUID.randomUUID(),
            userId = request.userId,
            type = request.type,
            recipient = request.recipient,
            message = request.message,
            status = NotificationStatus.SENT
        )

        every { notificationService.createAndSend(any()) } returns notification

        // When
        val response = controller.sendNotification(request)

        // Then
        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals(NotificationType.WEBHOOK, response.body?.type)
    }

    @Test
    fun `should get notification by id successfully`() {
        // Given
        val notificationId = UUID.randomUUID()
        val notification = Notification(
            id = notificationId,
            userId = "user123",
            type = NotificationType.EMAIL,
            recipient = "test@example.com",
            message = "Test",
            status = NotificationStatus.SENT
        )

        every { notificationService.getById(notificationId) } returns notification

        // When
        val response = controller.getNotification(notificationId)

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(notificationId, response.body?.id)
        assertEquals("user123", response.body?.userId)
        verify(exactly = 1) { notificationService.getById(notificationId) }
    }

    @Test
    fun `should return 404 when notification not found`() {
        // Given
        val notificationId = UUID.randomUUID()
        every { notificationService.getById(notificationId) } throws NoSuchElementException("Notification not found")

        // When
        val errorResponse = controller.handleNotFound(NoSuchElementException("Notification not found"))

        // Then
        assertEquals(HttpStatus.NOT_FOUND, errorResponse.statusCode)
        assertEquals("Notification not found", errorResponse.body?.get("error"))
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
                recipient = "test1@example.com",
                message = "Test 1",
                status = NotificationStatus.SENT
            ),
            Notification(
                id = UUID.randomUUID(),
                userId = userId,
                type = NotificationType.WEBHOOK,
                recipient = "https://webhook.site/test",
                message = "Test 2",
                status = NotificationStatus.SENT
            )
        )

        every { notificationService.getByUserId(userId) } returns notifications

        // When
        val response = controller.getUserNotifications(userId)

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(2, response.body?.size)
        assertTrue(response.body!!.all { it.userId == userId })
        verify(exactly = 1) { notificationService.getByUserId(userId) }
    }

    @Test
    fun `should get all notifications`() {
        // Given
        val notifications = listOf(
            Notification(
                id = UUID.randomUUID(),
                userId = "user1",
                type = NotificationType.EMAIL,
                recipient = "test1@example.com",
                message = "Test 1",
                status = NotificationStatus.SENT
            ),
            Notification(
                id = UUID.randomUUID(),
                userId = "user2",
                type = NotificationType.EMAIL,
                recipient = "test2@example.com",
                message = "Test 2",
                status = NotificationStatus.SENT
            )
        )

        every { notificationService.getAllNotifications() } returns notifications

        // When
        val response = controller.getAllNotifications()

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(2, response.body?.size)
        verify(exactly = 1) { notificationService.getAllNotifications() }
    }

    @Test
    fun `should handle exception with null message`() {
        // Given
        val exception = NoSuchElementException()

        // When
        val response = controller.handleNotFound(exception)

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals("Resource not found", response.body?.get("error"))
    }
}
