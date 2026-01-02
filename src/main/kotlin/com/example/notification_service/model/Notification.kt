package com.example.notification_service.model

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "notifications")
data class Notification(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(name = "user_id", nullable = false)
    val userId: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val type: NotificationType,

    @Column(nullable = false)
    val recipient: String,

    @Column(length = 500)
    val subject: String? = null,

    @Column(nullable = false, columnDefinition = "TEXT")
    val message: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: NotificationStatus,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "sent_at")
    var sentAt: LocalDateTime? = null,

    @Column(columnDefinition = "TEXT")
    var error: String? = null
)

enum class NotificationType {
    EMAIL,
    WEBHOOK
}

enum class NotificationStatus {
    PENDING,
    SENT,
    FAILED
}