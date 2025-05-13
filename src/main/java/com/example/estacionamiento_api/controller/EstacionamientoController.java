package com.example.estacionamiento_api.controller;

import com.example.estacionamiento_api.model.Estacionamiento;
import com.example.estacionamiento_api.request.EstacionamientoRequest;
import com.example.estacionamiento_api.service.EstacionamientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
public class EstacionamientoController {

    @Autowired
    private EstacionamientoService estacionamientoService;

    @GetMapping("/")
    public String mostrarInicio() {
        return "inicio"; // Devuelve el nombre del archivo index.html
    }

    @GetMapping("/formulario")
    public String mostrarFormulario(Model model) {
        model.addAttribute("estacionamientoRequest", new EstacionamientoRequest());
        return "formulario";
    }

      @PostMapping("/registrar")
    public String registrarEstacionamiento(@ModelAttribute EstacionamientoRequest request, RedirectAttributes redirectAttributes) {
        System.out.println("Recibiendo solicitud de registro: " + request);
        Estacionamiento estacionamiento = new Estacionamiento();
        estacionamiento.setPlaca(request.getPlaca());
        estacionamiento.setNombreCliente(request.getNombreCliente());
        estacionamiento.setTipoServicio(request.getTipoServicio());
        estacionamiento.setTurno(request.getTurno()); // Guardar el turno <---- ESTO ES CRUCIAL
        System.out.println("Turno registrado: " + estacionamiento.getTurno()); // <---- LÍNEA AGREGADA PARA DIAGNÓSTICO

        // Establecer las horas según el tipo de servicio
        if ("hora".equals(request.getTipoServicio())) {
            estacionamiento.setHoras(request.getHoras());
        } else if ("mediaJornada".equals(request.getTipoServicio())) {
            estacionamiento.setHoras(5); // Valor predeterminado para media jornada
        } else if ("jornadaCompleta".equals(request.getTipoServicio())) {
            estacionamiento.setHoras(10); // Valor predeterminado para jornada completa
        }

        Estacionamiento registrado = estacionamientoService.registrarEntrada(estacionamiento);
        System.out.println("Objeto Estacionamiento guardado (controlador): " + registrado);

        if (registrado != null) {
            redirectAttributes.addFlashAttribute("mensaje", "Registro exitoso");
            redirectAttributes.addFlashAttribute("estacionamiento", registrado); // Pasar el objeto registrado
            return "redirect:/comprobante/" + registrado.getId(); // Redirigir al comprobante de entrada por ID
        } else {
            redirectAttributes.addFlashAttribute("error", "Error al registrar el vehículo");
            return "redirect:/formulario";
        }
    }
     @GetMapping("/resumen/noche")
    public String verResumenNoche(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "fecha", required = false) LocalDate fechaConsulta,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        LocalDate fechaConsultaNoche = fechaConsulta != null ? fechaConsulta : LocalDate.now(estacionamientoService.getZonaHorariaArgentina());

        LocalDateTime inicioNocheConsulta = fechaConsultaNoche.atTime(LocalTime.of(20, 0));
        LocalDateTime finNocheConsulta = fechaConsultaNoche.plusDays(1).atTime(LocalTime.of(6, 0));

        Page<Estacionamiento> resumenNoche = estacionamientoService.obtenerResumenPorNochePaginado(inicioNocheConsulta, finNocheConsulta, page, size);

        // Crear una copia mutable de la lista
        List<Estacionamiento> listaRegistrosNoche = new ArrayList<>(resumenNoche.getContent());
        listaRegistrosNoche.removeIf(Objects::isNull);

        model.addAttribute("registros", listaRegistrosNoche);
        model.addAttribute("totalRecaudado", estacionamientoService.calcularTotalRecaudadoPorNoche(inicioNocheConsulta, finNocheConsulta));
        model.addAttribute("currentPage", resumenNoche.getNumber());
        model.addAttribute("totalPages", resumenNoche.getTotalPages());
        model.addAttribute("totalItems", resumenNoche.getTotalElements());
        model.addAttribute("fechaConsulta", fechaConsulta);
        model.addAttribute("turno", "Noche");
        model.addAttribute("tituloResumen", "Resumen de Estacionamientos de la Noche");
        return "resumen";
    }

    @GetMapping("/comprobante/{id}")
    public String verComprobanteEntrada(@PathVariable Long id, Model model) {
        Estacionamiento estacionamiento = estacionamientoService.findById(id);
        if (estacionamiento != null) {
            model.addAttribute("estacionamiento", estacionamiento);
            return "comprobante"; // Tu vista actual "comprobante.html" para la entrada
        } else {
            model.addAttribute("error", "No se encontró el registro para el comprobante.");
            return "error";
        }
    }

    @GetMapping("/resumen")
    public String verResumen(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "fecha", required = false) LocalDate fechaConsulta,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Estacionamiento> resumen;
        LocalDateTime fechaInicioDia;
        LocalDateTime fechaFinDia;

        if (fechaConsulta != null) {
            fechaInicioDia = fechaConsulta.atStartOfDay();
            fechaFinDia = fechaConsulta.atTime(LocalTime.MAX);
            resumen = estacionamientoService.obtenerResumenPorDia(fechaInicioDia, page, size);
            model.addAttribute("fechaConsulta", fechaConsulta);
            model.addAttribute("tituloResumen", "Resumen de Estacionamientos");
        } else {
            LocalDateTime now = LocalDateTime.now(estacionamientoService.getZonaHorariaArgentina());
            resumen = estacionamientoService.obtenerResumenPorDia(now, page, size);
            model.addAttribute("tituloResumen", "Resumen de Estacionamientos");
        }

        List<Estacionamiento> listaRegistros = new ArrayList<>(resumen.getContent());
        listaRegistros.removeIf(Objects::isNull);

        System.out.println("Tamaño de la lista de registros en el controlador (resumen): " + listaRegistros.size());
        model.addAttribute("registros", listaRegistros);
        model.addAttribute("totalRecaudado", estacionamientoService.calcularTotalRecaudadoPorDia(LocalDateTime.now(estacionamientoService.getZonaHorariaArgentina())));
        model.addAttribute("currentPage", resumen.getNumber());
        model.addAttribute("totalPages", resumen.getTotalPages());
        model.addAttribute("totalItems", resumen.getTotalElements());
        return "resumen";
    }

    @GetMapping("/resumen/dia")
    public String verResumenDia(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "fecha", required = false) LocalDate fechaConsulta,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Estacionamiento> resumenDia;

        if (fechaConsulta != null) {
            LocalDateTime fechaInicioDia = fechaConsulta.atStartOfDay();
            LocalDateTime fechaFinDia = fechaConsulta.atTime(LocalTime.MAX);
            resumenDia = estacionamientoService.obtenerResumenPorDiaYTurno(fechaInicioDia, fechaFinDia, "dia", pageable);
            model.addAttribute("fechaConsulta", fechaConsulta);
        } else {
            LocalDateTime now = LocalDateTime.now(estacionamientoService.getZonaHorariaArgentina());
            LocalDateTime inicioDia = now.toLocalDate().atStartOfDay();
            LocalDateTime finDia = now.toLocalDate().atTime(LocalTime.MAX);
            resumenDia = estacionamientoService.obtenerResumenPorDiaYTurno(inicioDia, finDia, "dia", pageable);
        }

        List<Estacionamiento> listaRegistrosDia = new ArrayList<>(resumenDia.getContent());
        listaRegistrosDia.removeIf(Objects::isNull);

        model.addAttribute("registros", listaRegistrosDia);
        model.addAttribute("totalRecaudado", estacionamientoService.calcularTotalRecaudadoPorTurnoYDia(LocalDateTime.now(estacionamientoService.getZonaHorariaArgentina()), "dia"));
        model.addAttribute("currentPage", resumenDia.getNumber());
        model.addAttribute("totalPages", resumenDia.getTotalPages());
        model.addAttribute("totalItems", resumenDia.getTotalElements());
        model.addAttribute("turno", "Día"); // Para identificar en la vista
        model.addAttribute("tituloResumen", "Resumen de Estacionamientos del Día");
        return "resumen"; // Reutilizamos la vista de resumen
    }

    @GetMapping("/salida/{id}")
    public String mostrarFormularioSalida(@PathVariable Long id, Model model) {
        Optional<Estacionamiento> estacionamiento = Optional.ofNullable(estacionamientoService.findById(id)); // Modifica esta línea
        if (estacionamiento.isPresent()) {
            model.addAttribute("estacionamiento", estacionamiento.get());
            return "formulario_salida";
        } else {
            model.addAttribute("error", "No se encontró el registro.");
            return "error";
        }
    }
    @GetMapping("/comprobante/salida/{id}")
    public String verComprobanteSalida(@PathVariable Long id, Model model) {
        Estacionamiento estacionamiento = estacionamientoService.findById(id);
        if (estacionamiento != null && estacionamiento.getHoraSalida() != null) {
            model.addAttribute("estacionamiento", estacionamiento);
            return "comprobante_salida"; // Creamos una nueva vista para el comprobante de salida
        } else {
            model.addAttribute("error", "No se encontró el registro de salida.");
            return "error";
        }
    }

    @PostMapping("/registrar_salida")
    public String registrarSalida(@RequestParam("id") Long id, Model model) {
        Estacionamiento estacionamientoSalida = estacionamientoService.registrarSalida(id);
        if (estacionamientoSalida != null && estacionamientoSalida.getHoraSalida() != null) {
            return "redirect:/comprobante/salida/" + id; // Redirigir al comprobante de salida por ID
        } else {
            model.addAttribute("error", "Error al registrar la salida.");
            return "error";
        }
    }
}