package com.example.estacionamiento_api.request;

public class EstacionamientoRequest {
    private String placa;
    private String nombreCliente;
    private String tipoServicio;
    private Integer horas;
    private String turno; // Nuevo campo

    // Getters y setters con validación
    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        if (placa == null || placa.isEmpty()) {
            throw new IllegalArgumentException("La placa no puede estar vacía");
        }
        this.placa = placa;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        if (nombreCliente == null || nombreCliente.isEmpty()) {
            throw new IllegalArgumentException("El nombre del cliente no puede estar vacío");
        }
        this.nombreCliente = nombreCliente;
    }

    public String getTipoServicio() {
        return tipoServicio;
    }

    public void setTipoServicio(String tipoServicio) {
        if (tipoServicio == null || tipoServicio.isEmpty()) {
            throw new IllegalArgumentException("El tipo de servicio no puede estar vacío");
        }
        this.tipoServicio = tipoServicio;
    }

    public Integer getHoras() {
        return horas;
    }

    public void setHoras(Integer horas) {
        if (horas == null || horas < 0) {
            throw new IllegalArgumentException("Las horas deben ser un número positivo");
        }
        this.horas = horas;
    }

    public String getTurno() {
        return turno;
    }

    public void setTurno(String turno) {
        this.turno = turno;
    }

    @Override
    public String toString() {
        return "EstacionamientoRequest{" +
                "placa='" + placa + '\'' +
                ", nombreCliente='" + nombreCliente + '\'' +
                ", tipoServicio='" + tipoServicio + '\'' +
                ", horas=" + horas +
                ", turno='" + turno + '\'' +
                '}';
    }
}