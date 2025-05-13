
package com.example.estacionamiento_api.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "estacionamiento")
public class Estacionamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String placa;
    private String nombreCliente;

    private LocalDateTime horaEntrada;
    private LocalDateTime horaSalida;
    private LocalDateTime horaSalidaEsperada; // Nuevo campo

    private String tipoServicio;
    private Integer horas;
    private Double total;
    private Double descuento;
    private String turno; // Aquí está la propiedad

    // Constructor sin parámetros
    public Estacionamiento() {
    }

    // Constructor con parámetros
    public Estacionamiento(String placa, String nombreCliente, String tipoServicio, Integer horas,
                             LocalDateTime horaEntrada, LocalDateTime horaSalidaEsperada, Double total, Double descuento) {
        this.placa = placa;
        this.nombreCliente = nombreCliente;
        this.tipoServicio = tipoServicio;
        this.horas = horas;
        this.horaEntrada = horaEntrada;
        this.horaSalidaEsperada = horaSalidaEsperada;
        this.total = total;
        this.descuento = descuento;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        if (placa == null || placa.isEmpty()) {
            throw new IllegalArgumentException("La placa no puede estar vacía.");
        }
        this.placa = placa;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        if (nombreCliente == null || nombreCliente.isEmpty()) {
            throw new IllegalArgumentException("El nombre del cliente no puede estar vacío.");
        }
        this.nombreCliente = nombreCliente;
    }

    public LocalDateTime getHoraEntrada() {
        return horaEntrada;
    }

    public void setHoraEntrada(LocalDateTime horaEntrada) {
        if (horaEntrada == null) {
            throw new IllegalArgumentException("La hora de entrada no puede ser nula.");
        }
        this.horaEntrada = horaEntrada;
    }

    public LocalDateTime getHoraSalida() {
        return horaSalida;
    }

    public void setHoraSalida(LocalDateTime horaSalida) {
        this.horaSalida = horaSalida;
    }

    public LocalDateTime getHoraSalidaEsperada() {
        return horaSalidaEsperada;
    }

    public void setHoraSalidaEsperada(LocalDateTime horaSalidaEsperada) {
        this.horaSalidaEsperada = horaSalidaEsperada;
    }

    public String getTipoServicio() {
        return tipoServicio;
    }

    public void setTipoServicio(String tipoServicio) {
        if (tipoServicio == null || tipoServicio.isEmpty()) {
            throw new IllegalArgumentException("El tipo de servicio no puede estar vacío.");
        }
        this.tipoServicio = tipoServicio;
    }

    public Integer getHoras() {
        return horas;
    }

    public void setHoras(Integer horas) {
        if (horas <= 0) {
            throw new IllegalArgumentException("Las horas deben ser un valor positivo.");
        }
        this.horas = horas;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        if (total < 0) {
            throw new IllegalArgumentException("El total no puede ser un valor negativo.");
        }
        this.total = total;
    }

    public Double getDescuento() {
        return descuento;
    }

    public void setDescuento(Double descuento) {
        if (descuento < 0) {
            throw new IllegalArgumentException("El descuento no puede ser un valor negativo.");
        }
        this.descuento = descuento;
    }

    // Getters y setters para 'turno'
    public String getTurno() {
        return turno;
    }

    public void setTurno(String turno) {
        this.turno = turno;
    }

    @Override
    public String toString() {
        return "Estacionamiento{" +
               "id=" + id +
               ", placa='" + placa + '\'' +
               ", nombreCliente='" + nombreCliente + '\'' +
               ", horaEntrada=" + horaEntrada +
               ", horaSalida=" + horaSalida +
               ", horaSalidaEsperada=" + horaSalidaEsperada +
               ", tipoServicio='" + tipoServicio + '\'' +
               ", horas=" + horas +
               ", total=" + total +
               ", descuento=" + descuento +
               ", turno='" + turno + '\'' + // Incluimos el turno en el toString
               '}';
    }
}