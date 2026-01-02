package com.example.notification_service.service

import com.example.notification_service.model.Notification
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.time.LocalDateTime

@Service
class WebhookService(
    private val restTemplate: RestTemplate,
    private val objectMapper: ObjectMapper
) {

    private val logger = LoggerFactory.getLogger(WebhookService::class.java)

    fun sendWebhook(notification: Notification): Boolean {
        return try {
            logger.info("Preparing to send webhook to: ${notification.recipient}")

            val webhookUrl = notification.recipient
            val payload = buildWebhookPayload(notification)

            val headers = HttpHeaders()
            headers.contentType = MediaType.APPLICATION_JSON
            headers.set("User-Agent", "NotificationService/1.0")
            headers.set("X-Notification-Id", notification.id.toString())

            val request = HttpEntity(payload, headers)

            val response = restTemplate.exchange(
                webhookUrl,
                HttpMethod.POST,
                request,
                String::class.java
            )

            if (response.statusCode.is2xxSuccessful) {
                logger.info("Webhook sent successfully to: $webhookUrl - Status: ${response.statusCode}")
                true
            } else {
                logger.warn("Webhook returned non-2xx status: ${response.statusCode}")
                false
            }

        } catch (e: Exception) {
            logger.error("Failed to send webhook to: ${notification.recipient}", e)
            throw e
        }
    }

    private fun buildWebhookPayload(notification: Notification): String {
        val payload = mapOf(
            "event" to "notification.sent",
            "timestamp" to LocalDateTime.now().toString(),
            "data" to mapOf(
                "notificationId" to notification.id.toString(),
                "userId" to notification.userId,
                "type" to notification.type.name,
                "subject" to notification.subject,
                "message" to notification.message,
                "recipient" to notification.recipient,
                "status" to notification.status.name
            )
        )

        return objectMapper.writeValueAsString(payload)
    }
}