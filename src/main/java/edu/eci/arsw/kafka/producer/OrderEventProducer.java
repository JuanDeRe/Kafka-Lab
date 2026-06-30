package edu.eci.arsw.kafka.producer;

import edu.eci.arsw.kafka.dto.OrderCreatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderEventProducer {
    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    public OrderEventProducer(KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishOrderCreated(OrderCreatedEvent event) {
        kafkaTemplate.send("orders", event.getOrderId(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        System.err.println("Error al publicar evento: " + ex.getMessage());
                        ex.printStackTrace();
                    } else {
                        System.out.println("Evento publicado en partición "
                                + result.getRecordMetadata().partition()
                                + " offset " + result.getRecordMetadata().offset());
                    }
                });
    }
}