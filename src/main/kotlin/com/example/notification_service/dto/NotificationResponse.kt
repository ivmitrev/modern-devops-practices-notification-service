package com.example.notification_service.dto

import com.example.notification_service.model.Notification
import com.example.notification_service.model.NotificationStatus
import com.example.notification_service.model.NotificationType
import java.time.LocalDateTime
import java.util.UUID

data class NotificationResponse(
    val id: UUID,
    val userId: String,
    val type: NotificationType,
    val recipient: String,
    val subject: String?,
    val message: String,
    val status: NotificationStatus,
    val createdAt: LocalDateTime,
    val sentAt: LocalDateTime?,
    val error: String?
) {
    companion object {
        fun from(notification: Notification): NotificationResponse {
            return NotificationResponse(
                id = notification.id!!,
                userId = notification.userId,
                type = notification.type,
                recipient = notification.recipient,
                subject = notification.subject,
                message = notification.message,
                status = notification.status,
                createdAt = notification.createdAt,
                sentAt = notification.sentAt,
                error = notification.error
            )
        }
    }
}