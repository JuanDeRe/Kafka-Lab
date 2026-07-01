package edu.eci.arsw.kafka.dto;

import java.time.Instant;

public class InventoryProcessedEvent {
    private String inventoryId;
    private String orderId;
    private String customerId;
    private Double total;
    private String status;
    private Instant occurredAt;

    public InventoryProcessedEvent() {}
    public InventoryProcessedEvent(String inventoryId, String orderId, String customerId,
                                   String status, Instant occurredAt) {
        this.inventoryId = inventoryId;
        this.orderId = orderId;
        this.customerId = customerId;
        this.status = status;
        this.occurredAt = occurredAt;
    }

    public String getInventoryId() {
        return inventoryId;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public Double getTotal() {
        return total;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public String getStatus() {
        return status;
    }

    public void setInventoryId(String inventoryId) {
        this.inventoryId = inventoryId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
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
