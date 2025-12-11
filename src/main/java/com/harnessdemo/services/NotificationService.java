package com.harnessdemo.services;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NotificationService {

    private final Map<Long, List<Notification>> userNotifications = new ConcurrentHashMap<>();

    public enum NotificationType {
        EMAIL, SMS, PUSH, IN_APP
    }

    public enum NotificationStatus {
        PENDING, SENT, DELIVERED, FAILED, READ
    }

    public static class Notification {
        private String id;
        private Long userId;
        private String title;
        private String message;
        private NotificationType type;
        private NotificationStatus status;
        private LocalDateTime createdAt;
        private LocalDateTime sentAt;

        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public NotificationType getType() { return type; }
        public void setType(NotificationType type) { this.type = type; }
        public NotificationStatus getStatus() { return status; }
        public void setStatus(NotificationStatus status) { this.status = status; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        public LocalDateTime getSentAt() { return sentAt; }
        public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }
    }

    public Notification createNotification(Long userId, String title, String message, NotificationType type) {
        validateNotification(userId, title, message, type);

        Notification notification = new Notification();
        notification.setId(UUID.randomUUID().toString());
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setStatus(NotificationStatus.PENDING);
        notification.setCreatedAt(LocalDateTime.now());

        userNotifications.computeIfAbsent(userId, k -> new ArrayList<>()).add(notification);
        return notification;
    }

    public Notification sendNotification(String notificationId, Long userId) {
        Notification notification = getNotificationById(notificationId, userId);
        if (notification.getStatus() != NotificationStatus.PENDING) {
            throw new IllegalStateException("Notification is not in pending status");
        }

        // Simulate sending
        notification.setStatus(NotificationStatus.SENT);
        notification.setSentAt(LocalDateTime.now());
        return notification;
    }

    public Notification markAsDelivered(String notificationId, Long userId) {
        Notification notification = getNotificationById(notificationId, userId);
        notification.setStatus(NotificationStatus.DELIVERED);
        return notification;
    }

    public Notification markAsRead(String notificationId, Long userId) {
        Notification notification = getNotificationById(notificationId, userId);
        notification.setStatus(NotificationStatus.READ);
        return notification;
    }

    public List<Notification> getUserNotifications(Long userId) {
        return userNotifications.getOrDefault(userId, Collections.emptyList());
    }

    public List<Notification> getUnreadNotifications(Long userId) {
        return getUserNotifications(userId).stream()
            .filter(n -> n.getStatus() != NotificationStatus.READ)
            .toList();
    }

    public long countUnreadNotifications(Long userId) {
        return getUnreadNotifications(userId).size();
    }

    public void deleteNotification(String notificationId, Long userId) {
        List<Notification> notifications = userNotifications.get(userId);
        if (notifications != null) {
            notifications.removeIf(n -> n.getId().equals(notificationId));
        }
    }

    public void clearAllNotifications(Long userId) {
        userNotifications.remove(userId);
    }

    public Notification sendOrderConfirmation(Long userId, Long orderId) {
        return createNotification(userId, "Order Confirmed",
            "Your order #" + orderId + " has been confirmed!", NotificationType.EMAIL);
    }

    public Notification sendShippingNotification(Long userId, Long orderId, String trackingNumber) {
        return createNotification(userId, "Order Shipped",
            "Your order #" + orderId + " has been shipped. Tracking: " + trackingNumber,
            NotificationType.PUSH);
    }

    private Notification getNotificationById(String id, Long userId) {
        List<Notification> notifications = userNotifications.get(userId);
        if (notifications == null) {
            throw new RuntimeException("No notifications found for user: " + userId);
        }
        return notifications.stream()
            .filter(n -> n.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Notification not found: " + id));
    }

    private void validateNotification(Long userId, String title, String message, NotificationType type) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID is required");
        }
        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException("Title is required");
        }
        if (message == null || message.isEmpty()) {
            throw new IllegalArgumentException("Message is required");
        }
        if (type == null) {
            throw new IllegalArgumentException("Notification type is required");
        }
    }
}
