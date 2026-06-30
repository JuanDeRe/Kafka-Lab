# Kafka-Lab
ARSW

2. **Actividad 2. Decisiones de configuración**
Analice una configuración con un topic orders, una partición, factor de replicación 1, mensajes sin clave y retención
de 24 horas. Identifique riesgos y proponga mejoras para un ambiente productivo.

Los riesgos al tener solo una partición en un topic son que no nos permite paralelismo. Al tener solo replicación 1, estamos susceptibles a fallos; en cambio, si tuviéramos más, nuestro sistema sería tolerante a estas fallas. El tiempo de retención es muy corto para la auditoría.
Para mejorar el ambiente, tendríamos que poner más particiones, aumentar la duración de retención de los eventos en el broker y el tamaño de réplicas para que nuestro sistema sea tolerante a fallas.