Documentación del Proyecto de Estacionamiento API
Nombre del Proyecto: estacionamiento_api

Descripción: Una API REST para gestionar el registro de entradas y salidas de vehículos en un estacionamiento, calcular tarifas, generar comprobantes y mostrar un resumen diario.

Tecnologías Utilizadas:

Java: Lenguaje de programación principal.
Spring Boot: Framework para construir la aplicación de manera rápida y sencilla.
Spring Web MVC: Módulo de Spring para construir aplicaciones web con arquitectura MVC.
Spring Data JPA: Para la interacción con la base de datos utilizando el patrón JPA (Java Persistence API).
Hibernate: Implementación de JPA utilizada por Spring Data JPA.
Thymeleaf: Motor de plantillas Java para generar las páginas HTML dinámicamente.
Java Time API (java.time): Para el manejo de fechas y horas.
Maven: Herramienta de gestión de dependencias y construcción del proyecto.
Arquitectura:

El proyecto sigue una arquitectura típica de Spring Boot con las siguientes capas:

Controladores (Controller): Reciben las peticiones HTTP del cliente, interactúan con la capa de servicio y devuelven respuestas (vistas HTML o datos).
Servicios (Service): Contienen la lógica de negocio de la aplicación. Realizan operaciones como registrar entradas, calcular tarifas, registrar salidas y obtener resúmenes.
Repositorios (Repository): Interfaces gestionadas por Spring Data JPA que permiten la interacción con la base de datos (CRUD - Crear, Leer, Actualizar, Eliminar).
Modelos (Model): Clases Java que representan las entidades de la base de datos (por ejemplo, Estacionamiento).
Vistas (templates): Archivos HTML (utilizando Thymeleaf) que se renderizan y se envían al cliente.
Funcionalidades Implementadas:

Registro de Entrada:

Permite registrar la entrada de un vehículo solicitando la placa y el nombre del cliente.
Automáticamente guarda la hora de entrada actual.
Permite seleccionar un tipo de servicio ("hora", "mediaJornada", "jornadaCompleta") y, opcionalmente, la cantidad de horas.
Calcula una tarifa base y aplica descuentos según el tipo de servicio.
Calcula una hora de salida esperada (si se proporciona la duración).
Muestra un comprobante de registro (resultado.html).
Registro de Salida:

Permite registrar la salida de un vehículo buscando por su ID.
Guarda la hora de salida actual.
Calcula la duración real de la estadía.
Recalcula el total a pagar basado en la duración real y la tarifa por hora (diurna o nocturna).
Cálculo de Tarifas:

Define tarifas diferentes para el día (6:00 - 19:59) y la noche (20:00 - 5:59).
Aplica descuentos para los servicios de "mediaJornada" (5%) y "jornadaCompleta" (10%).
Resumen Diario de Estacionamientos:

Permite ver un resumen de los estacionamientos registrados para un día específico (o el día actual por defecto).
Implementa paginación para mostrar los resultados en varias páginas.
Permite filtrar el resumen por fecha a través de un parámetro en la URL.
Comprobante de Registro:

Genera una página HTML (resultado.html) después de un registro exitoso, mostrando los detalles del estacionamiento (placa, tipo de servicio, hora de entrada, horas estimadas, total estimado, descuento).
Incluye un botón para imprimir el comprobante utilizando JavaScript.
Incluye enlaces para registrar otro vehículo y ver el resumen.
Tarea Programada (Salidas Excedidas):

Una tarea programada (@Scheduled) se ejecuta cada minuto para verificar las salidas que han excedido la hora de salida esperada.
Implementa una lógica para registrar automáticamente la salida de estos vehículos.
Archivos Clave:

Estacionamiento.java (Modelo): Representa la entidad del estacionamiento en la base de datos.
EstacionamientoRepository.java (Repositorio): Interface para interactuar con la tabla de estacionamientos.
EstacionamientoService.java (Servicio): Contiene la lógica de negocio para las operaciones del estacionamiento.
EstacionamientoController.java (Controlador): Maneja las peticiones relacionadas con el registro y la salida de vehículos.
ResumenController.java (Controlador): Maneja la petición para mostrar el resumen de estacionamientos.
resultado.html (Vista): Muestra el comprobante de registro.
resumen.html (Vista): Muestra el resumen de los estacionamientos (requiere ser creado y vinculado correctamente).
style.css (Recurso): Archivo de estilos CSS para las páginas HTML.
Próximos Pasos (Posibles):

Implementar completamente la vista resumen.html para mostrar los datos del resumen.
Agregar funcionalidades para actualizar y eliminar registros de estacionamiento (si es necesario).
Implementar seguridad en la API.
Agregar validaciones a los datos de entrada.
Mejorar el manejo de errores y la retroalimentación al usuario.
Implementar la generación de un PDF del resumen.