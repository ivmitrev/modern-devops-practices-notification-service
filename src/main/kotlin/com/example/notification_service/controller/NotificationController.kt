package com.example.notification_service.controller

import com.example.notification_service.dto.NotificationRequest
import com.example.notification_service.dto.NotificationResponse
import com.example.notification_service.service.NotificationService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.NoSuchElementException
import java.util.UUID

@RestController
@RequestMapping("/api/notifications")
class NotificationController(
    private val notificationService: NotificationService
) {

    @PostMapping("/send")
    fun sendNotification(
        @Valid @RequestBody request: NotificationRequest
    ): ResponseEntity<NotificationResponse> {
        val notification = notificationService.createAndSend(request)
        val response = NotificationResponse.from(notification)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping("/{id}")
    fun getNotification(@PathVariable id: UUID): ResponseEntity<NotificationResponse> {
        val notification = notificationService.getById(id)
        val response = NotificationResponse.from(notification)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/user/{userId}")
    fun getUserNotifications(@PathVariable userId: String): ResponseEntity<List<NotificationResponse>> {
        val notifications = notificationService.getByUserId(userId)
        val responses = notifications.map { NotificationResponse.from(it) }
        return ResponseEntity.ok(responses)
    }

    @GetMapping
    fun getAllNotifications(): ResponseEntity<List<NotificationResponse>> {
        val notifications = notificationService.getAllNotifications()
        val responses = notifications.map { NotificationResponse.from(it) }
        return ResponseEntity.ok(responses)
    }

    @ExceptionHandler(java.util.NoSuchElementException::class)
    fun handleNotFound(e: NoSuchElementException): ResponseEntity<Map<String, String>> {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(mapOf("error" to (e.message ?: "Resource not found")))
    }
}
