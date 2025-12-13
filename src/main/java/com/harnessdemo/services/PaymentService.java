package com.harnessdemo.services;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.security.MessageDigest;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * PaymentService - Demo service with intentional security vulnerabilities
 *
 * FOR STO DEMO PURPOSES ONLY - This service contains intentional vulnerabilities
 * to demonstrate Harness Security Testing Orchestration capabilities:
 * - OWASP Dependency Check finds vulnerable dependencies
 * - SCA scanner finds code-level vulnerabilities
 * - AIDA provides remediation guidance
 *
 * VULNERABILITIES INCLUDED FOR DEMO:
 * 1. SQL Injection (CWE-89) - CRITICAL
 * 2. Command Injection (CWE-78) - CRITICAL
 * 3. Insecure Deserialization (CWE-502) - HIGH
 * 4. Weak Cryptography (CWE-327) - MEDIUM
 * 5. Hardcoded Credentials (CWE-798) - MEDIUM
 */
@Service
public class PaymentService {

    private final Map<String, PaymentRecord> payments = new ConcurrentHashMap<>();

    // VULNERABILITY: Hardcoded credentials (CWE-798) - MEDIUM
    private static final String DB_PASSWORD = "admin123";
    private static final String API_KEY = "sk_live_1234567890abcdef";

    // VULNERABILITY: Weak encryption key (CWE-327) - MEDIUM
    private static final String ENCRYPTION_KEY = "1234567890123456";

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

    /**
     * VULNERABILITY: SQL Injection (CWE-89) - CRITICAL
     * User input is directly concatenated into SQL query
     * AIDA will suggest using PreparedStatement with parameterized queries
     */
    public String getPaymentDetailsByQuery(String paymentId) {
        String result = "";
        try {
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/payments", "root", DB_PASSWORD);
            Statement stmt = conn.createStatement();

            // CRITICAL: SQL Injection vulnerability - direct string concatenation
            String query = "SELECT * FROM payments WHERE payment_id = '" + paymentId + "'";
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                result = rs.getString("amount");
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * VULNERABILITY: Command Injection (CWE-78) - CRITICAL
     * User input is passed directly to system command
     * AIDA will suggest input validation and avoiding Runtime.exec()
     */
    public String generatePaymentReport(String filename) {
        StringBuilder output = new StringBuilder();
        try {
            // CRITICAL: Command Injection - user input in system command
            String command = "cat /var/reports/" + filename;
            Process process = Runtime.getRuntime().exec(command);

            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output.toString();
    }

    /**
     * VULNERABILITY: Insecure Deserialization (CWE-502) - HIGH
     * Deserializing untrusted data without validation
     * AIDA will suggest using safe serialization formats like JSON
     */
    public Object loadPaymentData(String filePath) {
        Object data = null;
        try {
            // HIGH: Insecure deserialization of untrusted data
            FileInputStream fis = new FileInputStream(filePath);
            ObjectInputStream ois = new ObjectInputStream(fis);
            data = ois.readObject();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * VULNERABILITY: Weak Cryptography - MD5 (CWE-327) - MEDIUM
     * Using deprecated MD5 hash algorithm
     * AIDA will suggest using SHA-256 or stronger
     */
    public String hashCardNumber(String cardNumber) {
        try {
            // MEDIUM: Weak cryptography - MD5 is broken
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(cardNumber.getBytes());
            return Base64.getEncoder().encodeToString(digest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * VULNERABILITY: Weak Encryption - DES (CWE-327) - MEDIUM
     * Using deprecated DES encryption
     * AIDA will suggest using AES-256
     */
    public String encryptSensitiveData(String data) {
        try {
            // MEDIUM: Weak encryption - DES is deprecated
            SecretKeySpec key = new SecretKeySpec(ENCRYPTION_KEY.getBytes(), "DES");
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encrypted = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // ============================================================
    // ORIGINAL CLEAN METHODS (kept for functionality)
    // ============================================================

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
