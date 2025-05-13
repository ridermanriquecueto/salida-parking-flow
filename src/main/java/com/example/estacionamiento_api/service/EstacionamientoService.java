package com.example.estacionamiento_api.service;

import com.example.estacionamiento_api.model.Estacionamiento;
import com.example.estacionamiento_api.repository.EstacionamientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Duration;
import java.time.ZoneId;
import java.util.List;

@Service
public class EstacionamientoService {

    @Autowired
    private EstacionamientoRepository estacionamientoRepository;
    private final double tarifaDiaPorHora = 1000;
    private final double tarifaNochePorHora = 800;
    private final ZoneId zonaHorariaArgentina = ZoneId.of("America/Argentina/Buenos_Aires");


        public Estacionamiento registrarEntrada(Estacionamiento estacionamiento) {
        estacionamiento.setHoraEntrada(LocalDateTime.now(zonaHorariaArgentina));

        double precioBasePorHora;
        if ("noche".equals(estacionamiento.getTurno())) {
            precioBasePorHora = tarifaNochePorHora;
        } else {
            precioBasePorHora = tarifaDiaPorHora;
        }

        double totalEstimado = 0.0;
        double descuento = 0.0;

        if ("hora".equals(estacionamiento.getTipoServicio())) {
            totalEstimado = estacionamiento.getHoras() * precioBasePorHora;
        } else if ("mediaJornada".equals(estacionamiento.getTipoServicio())) {
            estacionamiento.setHoras(5); // Establecer horas para media jornada
            totalEstimado = 5 * precioBasePorHora;
            descuento = 0.05; // 5% de descuento para media jornada
        } else if ("jornadaCompleta".equals(estacionamiento.getTipoServicio())) {
            estacionamiento.setHoras(10); // Establecer horas para jornada completa
            totalEstimado = 10 * precioBasePorHora;
            descuento = 0.10; // 10% de descuento para jornada completa
        }

        totalEstimado -= (totalEstimado * descuento);
        estacionamiento.setTotal(totalEstimado);
        estacionamiento.setDescuento(descuento * 100); // Guardar el porcentaje de descuento

        return estacionamientoRepository.save(estacionamiento);
    }
     public Estacionamiento registrarSalida(Long id) {
        Estacionamiento estacionamiento = estacionamientoRepository.findById(id).orElse(null);
        if (estacionamiento != null && estacionamiento.getHoraSalida() == null) {
            LocalDateTime horaSalida = LocalDateTime.now(zonaHorariaArgentina);
            System.out.println("Hora de Entrada (Manual): " + estacionamiento.getHoraEntrada());
            System.out.println("Hora de Salida (Manual): " + horaSalida);
            estacionamiento.setHoraSalida(horaSalida);
            Duration duracion = Duration.between(estacionamiento.getHoraEntrada(), estacionamiento.getHoraSalida());
            long duracionEnMinutos = duracion.toMinutes();
            int horasEstacionadas = (int) Math.ceil((double) duracionEnMinutos / 60);
            estacionamiento.setHoras(horasEstacionadas > 0 ? horasEstacionadas : 1); // Aseguramos al menos 1 hora si la duración es mayor que cero minutos
            double tarifaPorHora = calcularTarifa(estacionamiento.getHoraEntrada());
            estacionamiento.setTotal(horasEstacionadas * tarifaPorHora);
            return estacionamientoRepository.save(estacionamiento);
        }
        return null;
    }
     
    private double calcularTarifa(LocalDateTime hora) {
        LocalTime nocheInicio = LocalTime.of(20, 0);
        LocalTime nocheFin = LocalTime.of(6, 0);
        LocalTime horaLocal = hora.toLocalTime();
        if ((horaLocal.isAfter(nocheInicio) || horaLocal.equals(nocheInicio)) || horaLocal.isBefore(nocheFin)) {
            return tarifaNochePorHora;
        } else {
            return tarifaDiaPorHora;
        }
    }

    public Page<Estacionamiento> obtenerResumenPorDia(LocalDateTime fecha, int page, int size) {
        LocalDateTime startOfDay = fecha.with(LocalTime.MIN);
        LocalDateTime endOfDay = fecha.with(LocalTime.MAX);
        Pageable pageable = PageRequest.of(page, size);
        return estacionamientoRepository.findByHoraEntradaBetween(startOfDay, endOfDay, pageable);
    }

    public double calcularTotalRecaudadoPorDia(LocalDateTime fecha) {
        LocalDateTime startOfDay = fecha.with(LocalTime.MIN);
        LocalDateTime endOfDay = fecha.with(LocalTime.MAX);
        List<Estacionamiento> registrosDelDia = estacionamientoRepository.findAllByHoraEntradaBetween(startOfDay, endOfDay, Pageable.unpaged());
        return registrosDelDia.stream().mapToDouble(Estacionamiento::getTotal).sum();
    }

    @Scheduled(fixedRate = 60000)
    public void verificarSalidasExcedidas() {
        LocalDateTime now = LocalDateTime.now(zonaHorariaArgentina);
        List<Estacionamiento> estacionamientosActivos = estacionamientoRepository.findByHoraSalidaIsNull();

        for (Estacionamiento estacionamiento : estacionamientosActivos) {
            if (estacionamiento.getHoraSalidaEsperada() != null && now.isAfter(estacionamiento.getHoraSalidaEsperada())) {
                registrarSalidaAutomatica(estacionamiento.getId());
            }
        }
    }

        public void registrarSalidaAutomatica(Long id) {
        Estacionamiento estacionamiento = estacionamientoRepository.findById(id).orElse(null);
        if (estacionamiento != null && estacionamiento.getHoraSalida() == null) {
            LocalDateTime horaSalida = LocalDateTime.now(zonaHorariaArgentina);
            System.out.println("Salida Automática - Hora de Entrada: " + estacionamiento.getHoraEntrada());
            System.out.println("Salida Automática - Hora de Salida: " + horaSalida);
            estacionamiento.setHoraSalida(horaSalida);

            if ("hora".equals(estacionamiento.getTipoServicio())) {
                Duration duracion = Duration.between(estacionamiento.getHoraEntrada(), horaSalida);
                long duracionEnMinutos = duracion.toMinutes();
                int horasEstacionadas = (int) Math.ceil((double) duracionEnMinutos / 60);
                estacionamiento.setHoras(horasEstacionadas > 0 ? horasEstacionadas : 1);
                double tarifaPorHoraActual = calcularTarifa(estacionamiento.getHoraEntrada());
                estacionamiento.setTotal(tarifaPorHoraActual * horasEstacionadas);
            }
            estacionamientoRepository.save(estacionamiento);
            System.out.println("Salida automática registrada para el ID: " + id + " a las: " + horaSalida);
        }
    }

     public Page<Estacionamiento> obtenerResumenPorNoche(LocalDateTime fecha, int page, int size) {
        LocalTime nocheInicio = LocalTime.of(20, 0);
        LocalTime nocheFinManana = LocalTime.of(6, 0);
        LocalDateTime inicioNoche = fecha.with(nocheInicio);
        LocalDateTime finNoche;
        if (nocheFinManana.isAfter(nocheInicio)) {
            finNoche = fecha.plusDays(1).with(nocheFinManana);
        } else {
            finNoche = fecha.with(nocheFinManana);
        }
        Pageable pageable = PageRequest.of(page, size);
        return estacionamientoRepository.findByHoraEntradaBetween(inicioNoche, finNoche, pageable);
    }
    public double calcularTotalRecaudadoPorNoche(LocalDateTime fecha) {
        LocalTime nocheInicio = LocalTime.of(20, 0);
        LocalTime nocheFinManana = LocalTime.of(6, 0);
        LocalDateTime inicioNoche = fecha.with(nocheInicio);
        LocalDateTime finNoche;
        if (nocheFinManana.isAfter(nocheInicio)) {
            finNoche = fecha.plusDays(1).with(nocheFinManana);
        } else {
            finNoche = fecha.with(nocheFinManana);
        }
        List<Estacionamiento> registrosDeNoche = estacionamientoRepository.findAllByHoraEntradaBetween(inicioNoche, finNoche, Pageable.unpaged());
        return registrosDeNoche.stream().mapToDouble(Estacionamiento::getTotal).sum();
    }


    public Estacionamiento findById(Long id) {
        return estacionamientoRepository.findById(id).orElse(null);
    }
    public Page<Estacionamiento> obtenerResumenPorDiaYTurno(LocalDateTime inicioDia, LocalDateTime finDia, String turno, Pageable pageable) {
    return estacionamientoRepository.findByHoraEntradaBetweenAndTurno(inicioDia, finDia, turno, pageable);
    }
    public double calcularTotalRecaudadoPorTurnoYDia(LocalDateTime fecha, String turno) {
    LocalDateTime startOfDay = fecha.with(LocalTime.MIN);
    LocalDateTime endOfDay = fecha.with(LocalTime.MAX);
    List<Estacionamiento> registrosDelDia = estacionamientoRepository.findAllByHoraEntradaBetweenAndTurno(startOfDay, endOfDay, turno, Pageable.unpaged());
    return registrosDelDia.stream().mapToDouble(Estacionamiento::getTotal).sum();
    }

    public ZoneId getZonaHorariaArgentina() {
        return zonaHorariaArgentina;
    }
    public Page<Estacionamiento> obtenerResumenPorNochePaginado(LocalDateTime inicioNoche, LocalDateTime finNoche, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return estacionamientoRepository.findByHoraEntradaBetweenAndTurno(inicioNoche, finNoche, "noche", pageable);
    }

    public Double calcularTotalRecaudadoPorNoche(LocalDateTime inicioNoche, LocalDateTime finNoche) {
        List<Estacionamiento> estacionamientos = estacionamientoRepository.findAllByHoraEntradaBetweenAndTurno(inicioNoche, finNoche, "noche", Pageable.unpaged());
        return estacionamientos.stream().mapToDouble(Estacionamiento::getTotal).sum();
    }
}