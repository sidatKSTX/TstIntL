package com.harnessdemo.repositories;

import com.harnessdemo.models.Order;
import com.harnessdemo.models.Order.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserId(Long userId);

    List<Order> findByStatus(OrderStatus status);

    List<Order> findByUserIdAndStatus(Long userId, OrderStatus status);

    @Query("SELECT o FROM Order o WHERE o.totalAmount >= ?1")
    List<Order> findByMinimumAmount(BigDecimal minAmount);

    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN ?1 AND ?2")
    List<Order> findByDateRange(LocalDateTime start, LocalDateTime end);

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.userId = ?1")
    BigDecimal getTotalSpendByUser(Long userId);

    long countByStatus(OrderStatus status);
}
