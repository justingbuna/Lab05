package lab.flotavehicular.model;

public class VehiculoCombustion extends Vehiculo {
    private double nivelCombustible;

    public VehiculoCombustion(
            String placa,
            String marca,
            int kilometraje,
            double nivelCombustible) {
        super(placa, marca, kilometraje);
        this.nivelCombustible = nivelCombustible;
    }

    public double getNivelCombustible() {
        return nivelCombustible;
    }

    @Override
    public void iniciarRuta() {
        if (estado == EstadoVehiculo.TALLER) {
            throw new IllegalStateException(
                    "El vehículo [" + placa + "] está en el taller. No puede salir."
            );
        }

        if (estado == EstadoVehiculo.EN_RUTA) {
            throw new IllegalStateException(
                    "El vehículo [" + placa + "] ya se encuentra en ruta."
            );
        }

        if (nivelCombustible < 10) {
            throw new IllegalStateException(
                    "Combustible insuficiente. Debe ir a la gasolinera."
            );
        }

        nivelCombustible -= 10;
        kilometraje += 50;
        estado = EstadoVehiculo.EN_RUTA;
    }

    @Override
    public TipoVehiculo getTipo() {
        return TipoVehiculo.COMBUSTION;
    }

    @Override
    public String evaluarEstadoGeneral() {
        return nivelCombustible < 15
                ? "Requiere ir a la gasolinera"
                : "Niveles óptimos";
    }
}
