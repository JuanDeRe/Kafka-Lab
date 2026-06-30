package edu.eci.arsw.kafka.dto;

import java.time.Instant;

public class OrderCreatedEvent {
    private String orderId;
    private String customerId;
    private Double total;
    private String status;
    private Instant occurredAt;

    public OrderCreatedEvent(String orderId,String customerId,Double total,String status,Instant occurredAt)
    {
        this.orderId = orderId;
        this.customerId =customerId;
        this.total =total;
        this.status = status;
        this.occurredAt = occurredAt;
    }
    public String getOrderId(){
        return orderId;
    }
    public String getCustomerId() {
        return customerId;
    }

    public Double getTotal() {
        return total;
    }
    public String getStatus() {
        return status;
    }
    public Instant getOccurredAt() {
        return occurredAt;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setOccurredAt(Instant occurredAt) {
        this.occurredAt = occurredAt;
    }

}

