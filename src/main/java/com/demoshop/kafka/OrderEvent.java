package com.demoshop.kafka;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class OrderEvent {

    private String eventType;
    private Long orderId;
    private Long userId;
    private String userEmail;
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime timestamp;

    public static OrderEvent of(String type, Long orderId, Long userId, String email,
                                BigDecimal total, String status) {
        OrderEvent e = new OrderEvent();
        e.eventType = type;
        e.orderId = orderId;
        e.userId = userId;
        e.userEmail = email;
        e.totalAmount = total;
        e.status = status;
        e.timestamp = LocalDateTime.now();
        return e;
    }
}
