package com.example.notification_service.service

import com.example.notification_service.dto.NotificationRequest
import com.example.notification_service.model.Notification
import com.example.notification_service.model.NotificationStatus
import com.example.notification_service.model.NotificationType
import com.example.notification_service.repository.NotificationRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID


@Service
class NotificationService(
    private val notificationRepository: NotificationRepository,
    private val emailService: EmailService,
    private val webhookService: WebhookService
) {

    private val logger = LoggerFactory.getLogger(NotificationService::class.java)

    @Transactional
    fun createAndSend(request: NotificationRequest): Notification {
        logger.info("Creating notification for user: ${request.userId}")

        validateRecipient(request.type, request.recipient)

        var notification = Notification(
            userId = request.userId,
            type = request.type,
            recipient = request.recipient,
            subject = request.subject,
            message = request.message,
            status = NotificationStatus.PENDING
        )

        notification = notificationRepository.save(notification)
        logger.info("Notification created with id: ${notification.id}")

        try {
            sendNotification(notification)
            notification.status = NotificationStatus.SENT
            notification.sentAt = LocalDateTime.now()
            logger.info("Notification sent successfully: ${notification.id}")
        } catch (e: Exception) {
            notification.status = NotificationStatus.FAILED
            notification.error = e.message
            logger.error("Failed to send notification: ${notification.id}", e)
        }

        return notificationRepository.save(notification)
    }

    private fun sendNotification(notification: Notification) {
        when (notification.type) {
            NotificationType.EMAIL -> {
                logger.info("Simulating email send to: ${notification.recipient}")
                logger.info("Subject: ${notification.subject}")
                logger.info("Message: ${notification.message}")
                emailService.sendEmail(notification)
            }
            NotificationType.WEBHOOK -> {
                logger.info("Sending webhook notification...")
                webhookService.sendWebhook(notification)
            }
        }
    }

    private fun validateRecipient(type: NotificationType, recipient: String) {
        when (type) {
            NotificationType.EMAIL -> {
                val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
                if (!emailRegex.matches(recipient)) {
                    throw IllegalArgumentException("Invalid email address: $recipient")
                }
            }
            NotificationType.WEBHOOK -> {
                if (!recipient.startsWith("http://") && !recipient.startsWith("https://")) {
                    throw IllegalArgumentException("Webhook URL must start with http:// or https://")
                }
            }
        }
    }

    fun getById(id: UUID): Notification {
        return notificationRepository.findById(id)
            .orElseThrow { NoSuchElementException("Notification not found with id: $id") }
    }

    fun getByUserId(userId: String): List<Notification> {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
    }

    fun getAllNotifications(): List<Notification> {
        return notificationRepository.findAll()
    }
}