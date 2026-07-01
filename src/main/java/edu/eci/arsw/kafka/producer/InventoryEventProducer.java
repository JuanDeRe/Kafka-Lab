package edu.eci.arsw.kafka.producer;

import edu.eci.arsw.kafka.dto.InventoryProcessedEvent;
import org.springframework.stereotype.Service;
import org.springframework.kafka.core.KafkaTemplate;

@Service
public class InventoryEventProducer {
    private final KafkaTemplate<String, InventoryProcessedEvent> kafkaTemplate;

    public InventoryEventProducer(KafkaTemplate<String, InventoryProcessedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishInventoryProcessed(InventoryProcessedEvent event){
        kafkaTemplate.send("Inventory", event.getOrderId(), event);
        System.out.println("Evento de inventario publicado: " + event.getInventoryId() + " - Estado: " + event.getStatus());
    }
}
