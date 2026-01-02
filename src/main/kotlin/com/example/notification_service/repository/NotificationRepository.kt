package com.example.notification_service.repository

import com.example.notification_service.model.Notification
import com.example.notification_service.model.NotificationStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface NotificationRepository : JpaRepository<Notification, UUID> {

    fun findByUserId(userId: String): List<Notification>

    fun findByStatus(status: NotificationStatus): List<Notification>

    fun findByUserIdOrderByCreatedAtDesc(userId: String): List<Notification>
}