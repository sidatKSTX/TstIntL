package com.harnessdemo.services;

import com.harnessdemo.services.NotificationService.Notification;
import com.harnessdemo.services.NotificationService.NotificationStatus;
import com.harnessdemo.services.NotificationService.NotificationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NotificationServiceTest {

    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationService();
    }

    // ==================== Create Notification Tests (20 tests) ====================

    @Test
    void testCreateNotificationSuccess() {
        Notification result = notificationService.createNotification(
            1L, "Test Title", "Test Message", NotificationType.EMAIL);

        assertNotNull(result);
        assertNotNull(result.getId());
    }

    @Test
    void testCreateNotificationSetsUserId() {
        Notification result = notificationService.createNotification(
            123L, "Title", "Message", NotificationType.EMAIL);

        assertEquals(123L, result.getUserId());
    }

    @Test
    void testCreateNotificationSetsTitle() {
        Notification result = notificationService.createNotification(
            1L, "Specific Title", "Message", NotificationType.EMAIL);

        assertEquals("Specific Title", result.getTitle());
    }

    @Test
    void testCreateNotificationSetsMessage() {
        Notification result = notificationService.createNotification(
            1L, "Title", "Specific Message", NotificationType.EMAIL);

        assertEquals("Specific Message", result.getMessage());
    }

    @Test
    void testCreateNotificationSetsType() {
        Notification result = notificationService.createNotification(
            1L, "Title", "Message", NotificationType.PUSH);

        assertEquals(NotificationType.PUSH, result.getType());
    }

    @Test
    void testCreateNotificationSetsPendingStatus() {
        Notification result = notificationService.createNotification(
            1L, "Title", "Message", NotificationType.EMAIL);

        assertEquals(NotificationStatus.PENDING, result.getStatus());
    }

    @Test
    void testCreateNotificationSetsCreatedAt() {
        Notification result = notificationService.createNotification(
            1L, "Title", "Message", NotificationType.EMAIL);

        assertNotNull(result.getCreatedAt());
    }

    @Test
    void testCreateNotificationWithNullUserId() {
        assertThrows(IllegalArgumentException.class, () ->
            notificationService.createNotification(null, "Title", "Message", NotificationType.EMAIL));
    }

    @Test
    void testCreateNotificationWithNullTitle() {
        assertThrows(IllegalArgumentException.class, () ->
            notificationService.createNotification(1L, null, "Message", NotificationType.EMAIL));
    }

    @Test
    void testCreateNotificationWithEmptyTitle() {
        assertThrows(IllegalArgumentException.class, () ->
            notificationService.createNotification(1L, "", "Message", NotificationType.EMAIL));
    }

    @Test
    void testCreateNotificationWithNullMessage() {
        assertThrows(IllegalArgumentException.class, () ->
            notificationService.createNotification(1L, "Title", null, NotificationType.EMAIL));
    }

    @Test
    void testCreateNotificationWithEmptyMessage() {
        assertThrows(IllegalArgumentException.class, () ->
            notificationService.createNotification(1L, "Title", "", NotificationType.EMAIL));
    }

    @Test
    void testCreateNotificationWithNullType() {
        assertThrows(IllegalArgumentException.class, () ->
            notificationService.createNotification(1L, "Title", "Message", null));
    }

    @Test
    void testCreateNotificationEmailType() {
        Notification result = notificationService.createNotification(
            1L, "Title", "Message", NotificationType.EMAIL);

        assertEquals(NotificationType.EMAIL, result.getType());
    }

    @Test
    void testCreateNotificationSmsType() {
        Notification result = notificationService.createNotification(
            1L, "Title", "Message", NotificationType.SMS);

        assertEquals(NotificationType.SMS, result.getType());
    }

    @Test
    void testCreateNotificationPushType() {
        Notification result = notificationService.createNotification(
            1L, "Title", "Message", NotificationType.PUSH);

        assertEquals(NotificationType.PUSH, result.getType());
    }

    @Test
    void testCreateNotificationInAppType() {
        Notification result = notificationService.createNotification(
            1L, "Title", "Message", NotificationType.IN_APP);

        assertEquals(NotificationType.IN_APP, result.getType());
    }

    @Test
    void testCreateNotificationIdIsUnique() {
        Notification result1 = notificationService.createNotification(
            1L, "Title1", "Message1", NotificationType.EMAIL);
        Notification result2 = notificationService.createNotification(
            1L, "Title2", "Message2", NotificationType.EMAIL);

        assertNotEquals(result1.getId(), result2.getId());
    }

    @Test
    void testCreateNotificationWithLongTitle() {
        String longTitle = "A".repeat(200);
        Notification result = notificationService.createNotification(
            1L, longTitle, "Message", NotificationType.EMAIL);

        assertEquals(200, result.getTitle().length());
    }

    @Test
    void testCreateNotificationWithLongMessage() {
        String longMessage = "A".repeat(1000);
        Notification result = notificationService.createNotification(
            1L, "Title", longMessage, NotificationType.EMAIL);

        assertEquals(1000, result.getMessage().length());
    }

    // ==================== Send Notification Tests (15 tests) ====================

    @Test
    void testSendNotificationSuccess() {
        Notification notification = notificationService.createNotification(
            1L, "Title", "Message", NotificationType.EMAIL);

        Notification result = notificationService.sendNotification(notification.getId(), 1L);

        assertEquals(NotificationStatus.SENT, result.getStatus());
    }

    @Test
    void testSendNotificationSetsSentAt() {
        Notification notification = notificationService.createNotification(
            1L, "Title", "Message", NotificationType.EMAIL);

        Notification result = notificationService.sendNotification(notification.getId(), 1L);

        assertNotNull(result.getSentAt());
    }

    @Test
    void testSendNotificationNotFound() {
        notificationService.createNotification(1L, "Title", "Message", NotificationType.EMAIL);

        assertThrows(RuntimeException.class, () ->
            notificationService.sendNotification("invalid-id", 1L));
    }

    @Test
    void testSendNotificationNoUserNotifications() {
        assertThrows(RuntimeException.class, () ->
            notificationService.sendNotification("any-id", 999L));
    }

    @Test
    void testSendAlreadySentNotification() {
        Notification notification = notificationService.createNotification(
            1L, "Title", "Message", NotificationType.EMAIL);
        notificationService.sendNotification(notification.getId(), 1L);

        assertThrows(IllegalStateException.class, () ->
            notificationService.sendNotification(notification.getId(), 1L));
    }

    @Test
    void testSendNotificationPreservesTitle() {
        Notification notification = notificationService.createNotification(
            1L, "Preserved Title", "Message", NotificationType.EMAIL);

        Notification result = notificationService.sendNotification(notification.getId(), 1L);

        assertEquals("Preserved Title", result.getTitle());
    }

    @Test
    void testSendNotificationPreservesMessage() {
        Notification notification = notificationService.createNotification(
            1L, "Title", "Preserved Message", NotificationType.EMAIL);

        Notification result = notificationService.sendNotification(notification.getId(), 1L);

        assertEquals("Preserved Message", result.getMessage());
    }

    @Test
    void testSendNotificationPreservesType() {
        Notification notification = notificationService.createNotification(
            1L, "Title", "Message", NotificationType.PUSH);

        Notification result = notificationService.sendNotification(notification.getId(), 1L);

        assertEquals(NotificationType.PUSH, result.getType());
    }

    @Test
    void testMarkAsDelivered() {
        Notification notification = notificationService.createNotification(
            1L, "Title", "Message", NotificationType.EMAIL);
        notificationService.sendNotification(notification.getId(), 1L);

        Notification result = notificationService.markAsDelivered(notification.getId(), 1L);

        assertEquals(NotificationStatus.DELIVERED, result.getStatus());
    }

    @Test
    void testMarkAsRead() {
        Notification notification = notificationService.createNotification(
            1L, "Title", "Message", NotificationType.EMAIL);

        Notification result = notificationService.markAsRead(notification.getId(), 1L);

        assertEquals(NotificationStatus.READ, result.getStatus());
    }

    @Test
    void testMarkAsReadNotFound() {
        notificationService.createNotification(1L, "Title", "Message", NotificationType.EMAIL);

        assertThrows(RuntimeException.class, () ->
            notificationService.markAsRead("invalid-id", 1L));
    }

    @Test
    void testSendMultipleNotifications() {
        Notification n1 = notificationService.createNotification(
            1L, "Title1", "Message1", NotificationType.EMAIL);
        Notification n2 = notificationService.createNotification(
            1L, "Title2", "Message2", NotificationType.PUSH);

        notificationService.sendNotification(n1.getId(), 1L);
        notificationService.sendNotification(n2.getId(), 1L);

        List<Notification> notifications = notificationService.getUserNotifications(1L);
        assertTrue(notifications.stream().allMatch(n -> n.getStatus() == NotificationStatus.SENT));
    }

    @Test
    void testSendNotificationToCorrectUser() {
        notificationService.createNotification(1L, "Title", "Message", NotificationType.EMAIL);
        Notification n2 = notificationService.createNotification(
            2L, "Title2", "Message2", NotificationType.EMAIL);

        Notification result = notificationService.sendNotification(n2.getId(), 2L);

        assertEquals(2L, result.getUserId());
    }

    @Test
    void testMarkAsDeliveredPreservesOtherFields() {
        Notification notification = notificationService.createNotification(
            1L, "Test Title", "Test Message", NotificationType.EMAIL);

        Notification result = notificationService.markAsDelivered(notification.getId(), 1L);

        assertEquals("Test Title", result.getTitle());
        assertEquals("Test Message", result.getMessage());
    }

    @Test
    void testMarkAsReadPreservesOtherFields() {
        Notification notification = notificationService.createNotification(
            1L, "Test Title", "Test Message", NotificationType.PUSH);

        Notification result = notificationService.markAsRead(notification.getId(), 1L);

        assertEquals("Test Title", result.getTitle());
        assertEquals(NotificationType.PUSH, result.getType());
    }

    // ==================== Get Notifications Tests (15 tests) ====================

    @Test
    void testGetUserNotificationsSuccess() {
        notificationService.createNotification(1L, "Title", "Message", NotificationType.EMAIL);

        List<Notification> result = notificationService.getUserNotifications(1L);

        assertEquals(1, result.size());
    }

    @Test
    void testGetUserNotificationsEmpty() {
        List<Notification> result = notificationService.getUserNotifications(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetUserNotificationsMultiple() {
        notificationService.createNotification(1L, "Title1", "Message1", NotificationType.EMAIL);
        notificationService.createNotification(1L, "Title2", "Message2", NotificationType.PUSH);
        notificationService.createNotification(1L, "Title3", "Message3", NotificationType.SMS);

        List<Notification> result = notificationService.getUserNotifications(1L);

        assertEquals(3, result.size());
    }

    @Test
    void testGetUnreadNotifications() {
        notificationService.createNotification(1L, "Title1", "Message1", NotificationType.EMAIL);
        notificationService.createNotification(1L, "Title2", "Message2", NotificationType.PUSH);

        List<Notification> result = notificationService.getUnreadNotifications(1L);

        assertEquals(2, result.size());
    }

    @Test
    void testGetUnreadNotificationsWithSomeRead() {
        Notification n1 = notificationService.createNotification(
            1L, "Title1", "Message1", NotificationType.EMAIL);
        notificationService.createNotification(1L, "Title2", "Message2", NotificationType.PUSH);
        notificationService.markAsRead(n1.getId(), 1L);

        List<Notification> result = notificationService.getUnreadNotifications(1L);

        assertEquals(1, result.size());
    }

    @Test
    void testCountUnreadNotifications() {
        notificationService.createNotification(1L, "Title1", "Message1", NotificationType.EMAIL);
        notificationService.createNotification(1L, "Title2", "Message2", NotificationType.PUSH);

        long count = notificationService.countUnreadNotifications(1L);

        assertEquals(2, count);
    }

    @Test
    void testCountUnreadNotificationsZero() {
        long count = notificationService.countUnreadNotifications(999L);

        assertEquals(0, count);
    }

    @Test
    void testCountUnreadAfterMarkingAsRead() {
        Notification n1 = notificationService.createNotification(
            1L, "Title1", "Message1", NotificationType.EMAIL);
        notificationService.createNotification(1L, "Title2", "Message2", NotificationType.PUSH);
        notificationService.markAsRead(n1.getId(), 1L);

        long count = notificationService.countUnreadNotifications(1L);

        assertEquals(1, count);
    }

    @Test
    void testDeleteNotification() {
        Notification notification = notificationService.createNotification(
            1L, "Title", "Message", NotificationType.EMAIL);

        notificationService.deleteNotification(notification.getId(), 1L);

        List<Notification> result = notificationService.getUserNotifications(1L);
        assertTrue(result.isEmpty());
    }

    @Test
    void testDeleteNotificationNotFound() {
        notificationService.createNotification(1L, "Title", "Message", NotificationType.EMAIL);

        // Should not throw, just silently fail
        assertDoesNotThrow(() -> notificationService.deleteNotification("invalid-id", 1L));
    }

    @Test
    void testClearAllNotifications() {
        notificationService.createNotification(1L, "Title1", "Message1", NotificationType.EMAIL);
        notificationService.createNotification(1L, "Title2", "Message2", NotificationType.PUSH);

        notificationService.clearAllNotifications(1L);

        List<Notification> result = notificationService.getUserNotifications(1L);
        assertTrue(result.isEmpty());
    }

    @Test
    void testClearAllNotificationsForNonExistentUser() {
        assertDoesNotThrow(() -> notificationService.clearAllNotifications(999L));
    }

    @Test
    void testNotificationsIsolatedByUser() {
        notificationService.createNotification(1L, "Title1", "Message1", NotificationType.EMAIL);
        notificationService.createNotification(2L, "Title2", "Message2", NotificationType.EMAIL);

        List<Notification> user1Notifications = notificationService.getUserNotifications(1L);
        List<Notification> user2Notifications = notificationService.getUserNotifications(2L);

        assertEquals(1, user1Notifications.size());
        assertEquals(1, user2Notifications.size());
        assertEquals("Title1", user1Notifications.get(0).getTitle());
        assertEquals("Title2", user2Notifications.get(0).getTitle());
    }

    @Test
    void testSendOrderConfirmation() {
        Notification result = notificationService.sendOrderConfirmation(1L, 123L);

        assertNotNull(result);
        assertTrue(result.getMessage().contains("123"));
        assertEquals(NotificationType.EMAIL, result.getType());
    }

    @Test
    void testSendShippingNotification() {
        Notification result = notificationService.sendShippingNotification(1L, 123L, "TRACK123");

        assertNotNull(result);
        assertTrue(result.getMessage().contains("123"));
        assertTrue(result.getMessage().contains("TRACK123"));
        assertEquals(NotificationType.PUSH, result.getType());
    }
}
