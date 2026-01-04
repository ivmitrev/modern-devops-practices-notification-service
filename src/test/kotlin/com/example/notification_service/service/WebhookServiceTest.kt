package com.example.notification_service.service

import com.example.notification_service.model.Notification
import com.example.notification_service.model.NotificationStatus
import com.example.notification_service.model.NotificationType
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import java.util.*

class WebhookServiceTest {

    private lateinit var restTemplate: RestTemplate
    private lateinit var objectMapper: ObjectMapper
    private lateinit var webhookService: WebhookService

    @BeforeEach
    fun setup() {
        restTemplate = mockk()
        objectMapper = ObjectMapper().apply {
            registerKotlinModule()
            registerModule(JavaTimeModule())
        }
        webhookService = WebhookService(restTemplate, objectMapper)
    }

    @Test
    fun `should send webhook successfully`() {
        // Given
        val notification = Notification(
            id = UUID.randomUUID(),
            userId = "user123",
            type = NotificationType.WEBHOOK,
            recipient = "https://webhook.site/test",
            message = "Test webhook",
            status = NotificationStatus.PENDING
        )

        val response = ResponseEntity.ok("Success")
        every { restTemplate.exchange(any<String>(), any(), any(), String::class.java) } returns response

        // When
        val result = webhookService.sendWebhook(notification)

        // Then
        assertTrue(result)
        verify(exactly = 1) { restTemplate.exchange(any<String>(), any(), any(), String::class.java) }
    }

    @Test
    fun `should return false when webhook returns non-2xx status`() {
        // Given
        val notification = Notification(
            id = UUID.randomUUID(),
            userId = "user123",
            type = NotificationType.WEBHOOK,
            recipient = "https://webhook.site/test",
            message = "Test",
            status = NotificationStatus.PENDING
        )

        val response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error")
        every { restTemplate.exchange(any<String>(), any(), any(), String::class.java) } returns response

        // When
        val result = webhookService.sendWebhook(notification)

        // Then
        assertFalse(result)
    }

    @Test
    fun `should throw exception when webhook call fails`() {
        // Given
        val notification = Notification(
            id = UUID.randomUUID(),
            userId = "user123",
            type = NotificationType.WEBHOOK,
            recipient = "https://invalid-url.com",
            message = "Test",
            status = NotificationStatus.PENDING
        )

        every {
            restTemplate.exchange(
                any<String>(),
                any(),
                any(),
                String::class.java
            )
        } throws RuntimeException("Connection timeout")

        // When & Then
        assertThrows(RuntimeException::class.java) {
            webhookService.sendWebhook(notification)
        }
    }
}
