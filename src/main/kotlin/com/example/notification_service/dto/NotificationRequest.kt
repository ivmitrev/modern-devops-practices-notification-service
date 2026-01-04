package com.example.notification_service.dto

import com.example.notification_service.model.NotificationType
import jakarta.validation.constraints.NotBlank

data class NotificationRequest(
    @field:NotBlank(message = "User ID is required")
    val userId: String,

    val type: NotificationType = NotificationType.EMAIL,

    @field:NotBlank(message = "Recipient is required")
    val recipient: String,

    val subject: String? = null,

    @field:NotBlank(message = "Message is required")
    val message: String
)
