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


**Actividad 2. Decisiones de configuración**

Analice una configuración con un topic orders, una partición, factor de replicación 1, mensajes sin clave y retención
de 24 horas. Identifique riesgos y proponga mejoras para un ambiente productivo.

Los riesgos al tener solo una partición en un topic son que no nos permite paralelismo. 
Al tener solo replicación 1, estamos susceptibles a fallos; en cambio, si tuviéramos más, 
nuestro sistema sería tolerante a estas fallas. El tiempo de retención es muy corto para la auditoría.
Para mejorar el ambiente, tendríamos que poner más particiones, aumentar la duración de retención de 
los eventos en el broker y el tamaño de réplicas para que nuestro sistema sea tolerante a fallas.


**Actividad 4. Trazabilidad del evento**

Documente el recorrido del evento desde la solicitud HTTP hasta el consumidor. Indique topic, clave, partición,
consumidor, Consumer Group y evidencia en Kafka UI 

## Actividad 4. Trazabilidad del evento

Documento el recorrido del evento desde la solicitud HTTP hasta el consumidor.

### 1. Levantamiento del entorno

Se ejecutan los siguientes comandos:

- docker compose up -d
- docker ps

### 2. Creación del topic

- docker exec -it arsw-kafka bash
- /opt/kafka/bin/kafka-topics.sh --create --topic orders --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1

### 3. Levantamos la aplicación Spring Boot

- `mvn spring-boot:run`

### 4. Hacemos la solicitud HTTP que es nuestro productor

- curl -X POST http://localhost:8081/orders -H "Content-Type: application/json" -d "{"customerId":"CUS01","total":120000}"

![peticionPost.png](images/peticionPost.png)

El **OrderController** recibe la petición, construye un **OrderCreatedEvent** (con **orderId** autogenerado tipo UUID,
status **CREATED** y **occurredAt** con el timestamp actual) y lo delega a **OrderEventProducer**, que lo publica en el
topic **orders** usando el **orderId** como clave.

### 5. Evidencia en Kafka UI

| Campo | Valor                                                                                                                            |
|---|----------------------------------------------------------------------------------------------------------------------------------|
| **Topic** | orders                                                                                                                           |
| **Clave** | ORD-29832312-1db1-4df8-b29c-b3c3bf943eae                                                                                         |
| **Partición** | 3 particiones, el evento se asigna según hash de la clave (orderId)                                                              |
| **Consumidor** | En la clase OrderEventConsumer el metodo consume(), anotado con @KafkaListener(topics = "orders", groupId = "inventory-service") |
| **Consumer Group** | inventory-service                                                                                                                |

Vista general del topic: 3 particiones, 4 mensajes totales repartidos como 1/2/1.
![invoke.png](images/invoke.png)

Detalle de un mensaje: Key = orderId, Value = JSON con orderId, customerId, total, status y occurredAt.
![mensajes.png](images/mensajes.png)

Consumer Group inventory-service, 1 consumidor activo, estado STABLE.
![consumersP4.png](images/consumersP4.png)


## 6. Recorrido completo del evento

1. Cliente hace **POST /orders** con **customerId** y **total**.
2. El controllador con el metodo **createOrder()** construye el **OrderCreatedEvent** (orderId = UUID, status = CREATED, occurredAt = Instant.now()).
3. En la clase del productor**OrderEventProducer** publica el topico  **orders** por medio del metodo **publishOrderCreated()** usando **event.getOrderId()** como clave del mensaje Kafka.
4. Kafka asigna el mensaje a una partición (según hash de la clave) dentro de las 3 particiones del topic **orders**.
5. **OrderEventConsumer**, suscrito al topic **orders**  del Consumer Group **inventory-service**, lee el evento y lo imprime en consola.
6. Kafka UI confirma el evento: topic, clave, partición, offset, contenido del mensaje y estado del Consumer Group.

**Actividad 6. Evidencia y análisis**

Cree pedidos con valores diferentes y reconstruya el flujo de eventos en Kafka UI. Identifique eventos generados, topics, claves, Consumer Groups, offsets y lag.

- Evidenciamos que se crearon correctamente los topics

![extensionTopics.png](images/extensionTopics.png)

### 1. Pedidos de prueba

curl -X POST http://localhost:8081/orders -H "Content-Type: application/json" -d "{"customerId":"CUS01","total":123456}"
curl -X POST http://localhost:8081/orders -H "Content-Type: application/json" -d "{"customerId":"CUS02","total":4578935}"
curl -X POST http://localhost:8081/orders -H "Content-Type: application/json" -d "{"customerId":"CUS03","total":98235225}"

- Hacemos peticiones

![peticionesExtencion.png](images/peticionesExtencion.png)

### 2. Eventos generados por topic

**Topic orders**

- Evento: order-created
- Clave: orderId
- Particiones: 3
- Mensajes: 3 (uno por pedido)

![topicOrderMensajes.png](images/topicOrderMensajes.png)

**Topic payments**

- Evento: payment-processed
- Clave: orderId (mismo valor que en orders)
- Particiones: 3
- Mensajes: 3

![paymentsMensajes.png](images/paymentsMensajes.png)

**Topic inventory**

- Evento: inventory-processed
- Clave: orderId (mismo valor que en orders)
- Particiones: 3
- Mensajes: 3

![invMensajes.png](images/invMensajes.png)

### 3. Trazabilidad por clave

Para el pedido **ORD-e5a7612a-6000-461f-952d-af7a7ae8493b** (CUS01, total 123456), la clave orderId se mantiene
constante en los tres topics, lo que permite reconstruir el flujo completo
consultando cada topic por esa misma clave.

| Topic | Key (orderId) | Partition | Offset |
|---|---|-----------|--------|
| orders | ORD-e5a7612a-6000-461f-952d-af7a7ae8493b | 0         | 2      |
| payments |ORD-e5a7612a-6000-461f-952d-af7a7ae8493b | 0         | 2      |
| inventory | ORD-e5a7612a-6000-461f-952d-af7a7ae8493b | 0         | 2      |

### 4. Consumer Groups y lag

| Consumer Group  | Lag | Estado |
|---|---|---|
| payment-service | N/A | STABLE |
| inventory-service | N/A | STABLE |
| orders-service| N/A | STABLE |

![consumersP6.png](images/consumersP6.png)

## **Actividad 8. Diagnóstico de buenas prácticas**

Revise una arquitectura que usa un topic events, mensajes sin clave, factor 
de replicación 1, sin DLT y sin monitoreo de lag. Identifique problemas, atributos afectados
y mejoras prioritarias.

#### Problemas

**1. Un solo topic events para todo**
Mezclar eventos de dominios distintos (pedidos, pagos, inventario, etc.) en
un único topic rompe la organización por dominio, dificulta que cada servicio consuma solo lo que le interesa
y obliga a filtrar mensajes irrelevantes en el consumidor.

**2. Mensajes sin clave**
Sin clave, Kafka reparte los mensajes entre particiones de forma
aleatoria. Esto significa que eventos
relacionados por ejemplo, todos los eventos de un mismo pedido pueden caer
en particiones distintas, perdiendo el orden dentro de una
partición. Asímismo, Rompe la trazabilidad por entidad que se logró en la Actividad 6
usando **orderId** como clave.

**3. Factor de replicación = 1**
Si el único broker que tiene la partición falla, esos datos se pierden por
completo, por lo tanto, hay tolerancia a fallos.

**4. Sin monitoreo de lag**
Sin visibilidad del lag, nadie se entera si un consumidor dejó de procesar mensajes hasta que el problema pasa a grandes escalas.

#### Criterios

- Disponibilidad: Si tenemos una replica de 1, no tenemos tolerancia a fallos de broker 
- Mantenibilidad: Al tener un topic único mezcla dominios y es difícil de mantener y escalar 

#### Mejoras prioritarias

1. **Separar events en topics por dominio** (orders, payments,
   inventory), como se hizo en el punto 6.
2. **Definir una clave de particionamiento** coherente con la entidad cuyo
   orden importa (orderId, consumerId).
3. **Subir el factor de replicación** a al menos 3 en producción, para
   tolerar la caída de un broker.