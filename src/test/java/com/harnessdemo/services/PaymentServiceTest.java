package com.harnessdemo.services;

import com.harnessdemo.services.PaymentService.PaymentRecord;
import com.harnessdemo.services.PaymentService.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PaymentServiceTest {

    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentService();
    }

    // ==================== Initiate Payment Tests (20 tests) ====================

    @Test
    void testInitiatePaymentSuccess() {
        PaymentRecord result = paymentService.initiatePayment(
            1L, new BigDecimal("99.99"), "USD", "CREDIT_CARD");

        assertNotNull(result);
        assertNotNull(result.getTransactionId());
    }

    @Test
    void testInitiatePaymentSetsOrderId() {
        PaymentRecord result = paymentService.initiatePayment(
            123L, new BigDecimal("50.00"), "USD", "CREDIT_CARD");

        assertEquals(123L, result.getOrderId());
    }

    @Test
    void testInitiatePaymentSetsAmount() {
        PaymentRecord result = paymentService.initiatePayment(
            1L, new BigDecimal("75.50"), "USD", "CREDIT_CARD");

        assertEquals(new BigDecimal("75.50"), result.getAmount());
    }

    @Test
    void testInitiatePaymentSetsCurrency() {
        PaymentRecord result = paymentService.initiatePayment(
            1L, new BigDecimal("50.00"), "EUR", "CREDIT_CARD");

        assertEquals("EUR", result.getCurrency());
    }

    @Test
    void testInitiatePaymentSetsPaymentMethod() {
        PaymentRecord result = paymentService.initiatePayment(
            1L, new BigDecimal("50.00"), "USD", "PAYPAL");

        assertEquals("PAYPAL", result.getPaymentMethod());
    }

    @Test
    void testInitiatePaymentSetsPendingStatus() {
        PaymentRecord result = paymentService.initiatePayment(
            1L, new BigDecimal("50.00"), "USD", "CREDIT_CARD");

        assertEquals(PaymentStatus.PENDING, result.getStatus());
    }

    @Test
    void testInitiatePaymentWithNullAmount() {
        assertThrows(IllegalArgumentException.class, () ->
            paymentService.initiatePayment(1L, null, "USD", "CREDIT_CARD"));
    }

    @Test
    void testInitiatePaymentWithZeroAmount() {
        assertThrows(IllegalArgumentException.class, () ->
            paymentService.initiatePayment(1L, BigDecimal.ZERO, "USD", "CREDIT_CARD"));
    }

    @Test
    void testInitiatePaymentWithNegativeAmount() {
        assertThrows(IllegalArgumentException.class, () ->
            paymentService.initiatePayment(1L, new BigDecimal("-10.00"), "USD", "CREDIT_CARD"));
    }

    @Test
    void testInitiatePaymentWithNullCurrency() {
        assertThrows(IllegalArgumentException.class, () ->
            paymentService.initiatePayment(1L, new BigDecimal("50.00"), null, "CREDIT_CARD"));
    }

    @Test
    void testInitiatePaymentWithInvalidCurrency() {
        assertThrows(IllegalArgumentException.class, () ->
            paymentService.initiatePayment(1L, new BigDecimal("50.00"), "INVALID", "CREDIT_CARD"));
    }

    @Test
    void testInitiatePaymentWithNullPaymentMethod() {
        assertThrows(IllegalArgumentException.class, () ->
            paymentService.initiatePayment(1L, new BigDecimal("50.00"), "USD", null));
    }

    @Test
    void testInitiatePaymentWithEmptyPaymentMethod() {
        assertThrows(IllegalArgumentException.class, () ->
            paymentService.initiatePayment(1L, new BigDecimal("50.00"), "USD", ""));
    }

    @Test
    void testInitiatePaymentTransactionIdStartsWithTXN() {
        PaymentRecord result = paymentService.initiatePayment(
            1L, new BigDecimal("50.00"), "USD", "CREDIT_CARD");

        assertTrue(result.getTransactionId().startsWith("TXN-"));
    }

    @Test
    void testInitiatePaymentTransactionIdIsUnique() {
        PaymentRecord result1 = paymentService.initiatePayment(
            1L, new BigDecimal("50.00"), "USD", "CREDIT_CARD");
        PaymentRecord result2 = paymentService.initiatePayment(
            2L, new BigDecimal("75.00"), "USD", "CREDIT_CARD");

        assertNotEquals(result1.getTransactionId(), result2.getTransactionId());
    }

    @Test
    void testInitiatePaymentWithLargeAmount() {
        PaymentRecord result = paymentService.initiatePayment(
            1L, new BigDecimal("999999.99"), "USD", "CREDIT_CARD");

        assertEquals(new BigDecimal("999999.99"), result.getAmount());
    }

    @Test
    void testInitiatePaymentWithSmallAmount() {
        PaymentRecord result = paymentService.initiatePayment(
            1L, new BigDecimal("0.01"), "USD", "CREDIT_CARD");

        assertEquals(new BigDecimal("0.01"), result.getAmount());
    }

    @Test
    void testInitiatePaymentWithUSDCurrency() {
        PaymentRecord result = paymentService.initiatePayment(
            1L, new BigDecimal("50.00"), "USD", "CREDIT_CARD");

        assertEquals("USD", result.getCurrency());
    }

    @Test
    void testInitiatePaymentWithEURCurrency() {
        PaymentRecord result = paymentService.initiatePayment(
            1L, new BigDecimal("50.00"), "EUR", "CREDIT_CARD");

        assertEquals("EUR", result.getCurrency());
    }

    @Test
    void testInitiatePaymentWithGBPCurrency() {
        PaymentRecord result = paymentService.initiatePayment(
            1L, new BigDecimal("50.00"), "GBP", "CREDIT_CARD");

        assertEquals("GBP", result.getCurrency());
    }

    // ==================== Process Payment Tests (20 tests) ====================

    @Test
    void testProcessPaymentSuccess() {
        PaymentRecord payment = paymentService.initiatePayment(
            1L, new BigDecimal("50.00"), "USD", "CREDIT_CARD");

        PaymentRecord result = paymentService.processPayment(payment.getTransactionId());

        assertEquals(PaymentStatus.COMPLETED, result.getStatus());
    }

    @Test
    void testProcessPaymentNotFound() {
        assertThrows(RuntimeException.class, () ->
            paymentService.processPayment("TXN-NOTFOUND"));
    }

    @Test
    void testProcessPaymentAlreadyProcessed() {
        PaymentRecord payment = paymentService.initiatePayment(
            1L, new BigDecimal("50.00"), "USD", "CREDIT_CARD");
        paymentService.processPayment(payment.getTransactionId());

        assertThrows(IllegalStateException.class, () ->
            paymentService.processPayment(payment.getTransactionId()));
    }

    @Test
    void testProcessPaymentPreservesOrderId() {
        PaymentRecord payment = paymentService.initiatePayment(
            123L, new BigDecimal("50.00"), "USD", "CREDIT_CARD");

        PaymentRecord result = paymentService.processPayment(payment.getTransactionId());

        assertEquals(123L, result.getOrderId());
    }

    @Test
    void testProcessPaymentPreservesAmount() {
        PaymentRecord payment = paymentService.initiatePayment(
            1L, new BigDecimal("75.50"), "USD", "CREDIT_CARD");

        PaymentRecord result = paymentService.processPayment(payment.getTransactionId());

        assertEquals(new BigDecimal("75.50"), result.getAmount());
    }

    @Test
    void testProcessPaymentPreservesCurrency() {
        PaymentRecord payment = paymentService.initiatePayment(
            1L, new BigDecimal("50.00"), "EUR", "CREDIT_CARD");

        PaymentRecord result = paymentService.processPayment(payment.getTransactionId());

        assertEquals("EUR", result.getCurrency());
    }

    @Test
    void testProcessPaymentPreservesPaymentMethod() {
        PaymentRecord payment = paymentService.initiatePayment(
            1L, new BigDecimal("50.00"), "USD", "PAYPAL");

        PaymentRecord result = paymentService.processPayment(payment.getTransactionId());

        assertEquals("PAYPAL", result.getPaymentMethod());
    }

    @Test
    void testProcessPaymentTransitionFromPending() {
        PaymentRecord payment = paymentService.initiatePayment(
            1L, new BigDecimal("50.00"), "USD", "CREDIT_CARD");

        assertEquals(PaymentStatus.PENDING, payment.getStatus());

        PaymentRecord result = paymentService.processPayment(payment.getTransactionId());

        assertEquals(PaymentStatus.COMPLETED, result.getStatus());
    }

    @Test
    void testGetPaymentAfterProcess() {
        PaymentRecord payment = paymentService.initiatePayment(
            1L, new BigDecimal("50.00"), "USD", "CREDIT_CARD");
        paymentService.processPayment(payment.getTransactionId());

        PaymentRecord result = paymentService.getPayment(payment.getTransactionId());

        assertEquals(PaymentStatus.COMPLETED, result.getStatus());
    }

    @Test
    void testProcessMultiplePayments() {
        PaymentRecord payment1 = paymentService.initiatePayment(
            1L, new BigDecimal("50.00"), "USD", "CREDIT_CARD");
        PaymentRecord payment2 = paymentService.initiatePayment(
            2L, new BigDecimal("75.00"), "USD", "PAYPAL");

        paymentService.processPayment(payment1.getTransactionId());
        paymentService.processPayment(payment2.getTransactionId());

        assertEquals(PaymentStatus.COMPLETED,
            paymentService.getPayment(payment1.getTransactionId()).getStatus());
        assertEquals(PaymentStatus.COMPLETED,
            paymentService.getPayment(payment2.getTransactionId()).getStatus());
    }

    // ==================== Refund Payment Tests (20 tests) ====================

    @Test
    void testRefundPaymentSuccess() {
        PaymentRecord payment = paymentService.initiatePayment(
            1L, new BigDecimal("50.00"), "USD", "CREDIT_CARD");
        paymentService.processPayment(payment.getTransactionId());

        PaymentRecord result = paymentService.refundPayment(payment.getTransactionId());

        assertEquals(PaymentStatus.REFUNDED, result.getStatus());
    }

    @Test
    void testRefundPaymentNotFound() {
        assertThrows(RuntimeException.class, () ->
            paymentService.refundPayment("TXN-NOTFOUND"));
    }

    @Test
    void testRefundPendingPayment() {
        PaymentRecord payment = paymentService.initiatePayment(
            1L, new BigDecimal("50.00"), "USD", "CREDIT_CARD");

        assertThrows(IllegalStateException.class, () ->
            paymentService.refundPayment(payment.getTransactionId()));
    }

    @Test
    void testRefundPaymentPreservesOrderId() {
        PaymentRecord payment = paymentService.initiatePayment(
            123L, new BigDecimal("50.00"), "USD", "CREDIT_CARD");
        paymentService.processPayment(payment.getTransactionId());

        PaymentRecord result = paymentService.refundPayment(payment.getTransactionId());

        assertEquals(123L, result.getOrderId());
    }

    @Test
    void testRefundPaymentPreservesAmount() {
        PaymentRecord payment = paymentService.initiatePayment(
            1L, new BigDecimal("99.99"), "USD", "CREDIT_CARD");
        paymentService.processPayment(payment.getTransactionId());

        PaymentRecord result = paymentService.refundPayment(payment.getTransactionId());

        assertEquals(new BigDecimal("99.99"), result.getAmount());
    }

    @Test
    void testDoubleRefundNotAllowed() {
        PaymentRecord payment = paymentService.initiatePayment(
            1L, new BigDecimal("50.00"), "USD", "CREDIT_CARD");
        paymentService.processPayment(payment.getTransactionId());
        paymentService.refundPayment(payment.getTransactionId());

        assertThrows(IllegalStateException.class, () ->
            paymentService.refundPayment(payment.getTransactionId()));
    }

    // ==================== Validate Card Tests (20 tests) ====================

    @Test
    void testValidateCardSuccess() {
        assertTrue(paymentService.validateCard("4111111111111111", "12/25", "123"));
    }

    @Test
    void testValidateCardNullCardNumber() {
        assertFalse(paymentService.validateCard(null, "12/25", "123"));
    }

    @Test
    void testValidateCardShortCardNumber() {
        assertFalse(paymentService.validateCard("123456789", "12/25", "123"));
    }

    @Test
    void testValidateCardNullExpiry() {
        assertFalse(paymentService.validateCard("4111111111111111", null, "123"));
    }

    @Test
    void testValidateCardInvalidExpiryFormat() {
        assertFalse(paymentService.validateCard("4111111111111111", "1225", "123"));
    }

    @Test
    void testValidateCardNullCvv() {
        assertFalse(paymentService.validateCard("4111111111111111", "12/25", null));
    }

    @Test
    void testValidateCardShortCvv() {
        assertFalse(paymentService.validateCard("4111111111111111", "12/25", "12"));
    }

    @Test
    void testValidateCardValidVisa() {
        assertTrue(paymentService.validateCard("4111111111111111", "12/25", "123"));
    }

    @Test
    void testValidateCardValidMastercard() {
        assertTrue(paymentService.validateCard("5500000000000004", "12/25", "123"));
    }

    @Test
    void testValidateCardValidAmex() {
        assertTrue(paymentService.validateCard("340000000000009", "12/25", "1234"));
    }

    @Test
    void testValidateCardInvalidLuhn() {
        assertFalse(paymentService.validateCard("4111111111111112", "12/25", "123"));
    }

    @Test
    void testValidateCardValidExpiryFormat() {
        assertTrue(paymentService.validateCard("4111111111111111", "01/30", "123"));
    }

    @Test
    void testValidateCardWith3DigitCvv() {
        assertTrue(paymentService.validateCard("4111111111111111", "12/25", "123"));
    }

    @Test
    void testValidateCardWith4DigitCvv() {
        assertTrue(paymentService.validateCard("4111111111111111", "12/25", "1234"));
    }

    @Test
    void testValidateCardEmptyCardNumber() {
        assertFalse(paymentService.validateCard("", "12/25", "123"));
    }

    @Test
    void testValidateCardEmptyExpiry() {
        assertFalse(paymentService.validateCard("4111111111111111", "", "123"));
    }

    @Test
    void testValidateCardEmptyCvv() {
        assertFalse(paymentService.validateCard("4111111111111111", "12/25", ""));
    }

    @Test
    void testValidateCardWithSpaces() {
        assertFalse(paymentService.validateCard("4111 1111 1111 1111", "12/25", "123"));
    }

    @Test
    void testValidateCardWithDashes() {
        assertFalse(paymentService.validateCard("4111-1111-1111-1111", "12/25", "123"));
    }

    @Test
    void testValidateCardExpiryWithSlash() {
        assertTrue(paymentService.validateCard("4111111111111111", "12/25", "123"));
    }
}
