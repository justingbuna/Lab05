package lab.flotavehicular.model;

public abstract class Vehiculo implements Mantenible {
    protected String placa;
    protected String marca;
    protected int kilometraje;
    protected EstadoVehiculo estado;

    public Vehiculo(String placa, String marca, int kilometraje) {
        this.placa = placa;
        this.marca = marca;
        this.kilometraje = kilometraje;
        this.estado = EstadoVehiculo.DISPONIBLE;
    }

    public abstract void iniciarRuta();

    public void finalizarRuta() {
        if (estado != EstadoVehiculo.EN_RUTA) {
            throw new IllegalStateException(
                    "El vehículo [" + placa + "] no se encuentra en ruta."
            );
        }
        estado = EstadoVehiculo.DISPONIBLE;
    }

    public abstract TipoVehiculo getTipo();

    public String getPlaca() {
        return placa;
    }

    public String getMarca() {
        return marca;
    }

    public int getKilometraje() {
        return kilometraje;
    }

    public EstadoVehiculo getEstado() {
        return estado;
    }

    public void setEstado(EstadoVehiculo estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return marca + " [" + placa + "]";
    }
}
