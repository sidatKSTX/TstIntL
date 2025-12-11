package com.harnessdemo.services;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PaymentService {

    private final Map<String, PaymentRecord> payments = new ConcurrentHashMap<>();

    public enum PaymentStatus {
        PENDING, PROCESSING, COMPLETED, FAILED, REFUNDED
    }

    public static class PaymentRecord {
        private String transactionId;
        private Long orderId;
        private BigDecimal amount;
        private String currency;
        private PaymentStatus status;
        private String paymentMethod;

        // Getters and Setters
        public String getTransactionId() { return transactionId; }
        public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
        public Long getOrderId() { return orderId; }
        public void setOrderId(Long orderId) { this.orderId = orderId; }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
        public PaymentStatus getStatus() { return status; }
        public void setStatus(PaymentStatus status) { this.status = status; }
        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    }

    public PaymentRecord initiatePayment(Long orderId, BigDecimal amount, String currency, String paymentMethod) {
        validatePaymentRequest(amount, currency, paymentMethod);

        PaymentRecord payment = new PaymentRecord();
        payment.setTransactionId(generateTransactionId());
        payment.setOrderId(orderId);
        payment.setAmount(amount);
        payment.setCurrency(currency);
        payment.setPaymentMethod(paymentMethod);
        payment.setStatus(PaymentStatus.PENDING);

        payments.put(payment.getTransactionId(), payment);
        return payment;
    }

    public PaymentRecord processPayment(String transactionId) {
        PaymentRecord payment = getPayment(transactionId);
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new IllegalStateException("Payment is not in pending status");
        }

        payment.setStatus(PaymentStatus.PROCESSING);
        // Simulate payment processing
        payment.setStatus(PaymentStatus.COMPLETED);
        return payment;
    }

    public PaymentRecord refundPayment(String transactionId) {
        PaymentRecord payment = getPayment(transactionId);
        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new IllegalStateException("Can only refund completed payments");
        }
        payment.setStatus(PaymentStatus.REFUNDED);
        return payment;
    }

    public PaymentRecord getPayment(String transactionId) {
        PaymentRecord payment = payments.get(transactionId);
        if (payment == null) {
            throw new RuntimeException("Payment not found: " + transactionId);
        }
        return payment;
    }

    public boolean validateCard(String cardNumber, String expiry, String cvv) {
        if (cardNumber == null || cardNumber.length() < 13) {
            return false;
        }
        if (expiry == null || !expiry.matches("\\d{2}/\\d{2}")) {
            return false;
        }
        if (cvv == null || cvv.length() < 3) {
            return false;
        }
        return luhnCheck(cardNumber);
    }

    private boolean luhnCheck(String number) {
        int sum = 0;
        boolean alternate = false;
        for (int i = number.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(number.substring(i, i + 1));
            if (alternate) {
                n *= 2;
                if (n > 9) n -= 9;
            }
            sum += n;
            alternate = !alternate;
        }
        return (sum % 10 == 0);
    }

    private void validatePaymentRequest(BigDecimal amount, String currency, String paymentMethod) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (currency == null || currency.length() != 3) {
            throw new IllegalArgumentException("Invalid currency code");
        }
        if (paymentMethod == null || paymentMethod.isEmpty()) {
            throw new IllegalArgumentException("Payment method is required");
        }
    }

    private String generateTransactionId() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
