package com.demoshop.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderEventConsumer {

    @KafkaListener(topics = "order-events", groupId = "demo-shop-group")
    public void consume(OrderEvent event) {
        log.info("[ORDER EVENT] type={} orderId={} user={} amount={} status={}",
                event.getEventType(), event.getOrderId(), event.getUserEmail(),
                event.getTotalAmount(), event.getStatus());
    }
}
