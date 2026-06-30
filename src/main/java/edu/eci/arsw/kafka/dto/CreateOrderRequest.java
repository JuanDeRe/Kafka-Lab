package edu.eci.arsw.kafka.dto;

import java.time.Instant;

public class CreateOrderRequest {

    private String customerId;
    private Double total;

    public String getCustomerId() {
        return customerId;
    }
    public Double getTotal() {
        return total;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setTotal(Double total) {
        this.total = total;
    }
}
