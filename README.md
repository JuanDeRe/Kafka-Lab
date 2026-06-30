# Kafka-Lab
ARSW


# Apache Kafka y Arquitecturas Orientadas por Eventos

## Actividad 1. Análisis de comunicación

Para una tienda en línea, clasifique qué procesos deberían ser síncronos, asíncronos o híbridos: consultar
productos, crear pedido, validar pago, enviar notificación, actualizar analítica y registrar auditoría. Justifique
brevemente su decisión.

### Consultar productos

- Asincrónico: Es un evento y no requiere ningún orden en específico, únicamente consultar la base de datos y esperar que llegue el resultado.

### Crear pedido

- Sincrónico: Se debe verificar que el producto exista primero para poder crear el pedido.

### Validar pago

- Asincrónico: El cliente puede haber terminado de hacer su pedido mientras espera que se confirme el pago.

### Enviar notificación

- Asincrónico: No se requiere de ningún orden, solo que las notificaciones sean enviadas.

### Actualizar analítica

- Hibrido: Se pueden hacer todas las consultas de información necesarias para actualizar la analítica de manera asincrona, se debe esperar que se recolete toda la información para realizar el analisis.

### Registrar auditoria

- Asincrónico: Cada servicio se puede estar registrando individualmente, no hay ningun orden específico para hacerlo, solo se necesita que lo que se haga quede registrado.


2. **Actividad 2. Decisiones de configuración**
   Analice una configuración con un topic orders, una partición, factor de replicación 1, mensajes sin clave y retención
   de 24 horas. Identifique riesgos y proponga mejoras para un ambiente productivo.

Los riesgos al tener solo una partición en un topic son que no nos permite paralelismo. Al tener solo replicación 1, estamos susceptibles a fallos; en cambio, si tuviéramos más, nuestro sistema sería tolerante a estas fallas. El tiempo de retención es muy corto para la auditoría.
Para mejorar el ambiente, tendríamos que poner más particiones, aumentar la duración de retención de los eventos en el broker y el tamaño de réplicas para que nuestro sistema sea tolerante a fallas.

**Actividad 4. Trazabilidad del evento**

Documente el recorrido del evento desde la solicitud HTTP hasta el consumidor. Indique topic, clave, partición,
consumidor, Consumer Group y evidencia en Kafka UI 

corremos docker con los siguiente comandos

docker compose up -d
docker ps

creamos el topico

docker exec -it arsw-kafka bash
/opt/kafka/bin/kafka-topics.sh --create --topic orders --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1

levantamos spring-boot

mvn spring-boot:run

Hacemos la peticion 

curl -X POST http://localhost:8081/orders -H "Content-Type: application/json" -d "{\"customerId\":\"CUS01\",\"total\":120000}"

![peticionPost.png](images%2FpeticionPost.png)

Ahora, consultamos kafka UI : http://localhost:8080/

Topic : orders
Clave : 
Partición : 3 
Consumidor : 
Consumer Group : inventory-service


