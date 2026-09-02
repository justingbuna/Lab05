package lab.flotavehicular.model;

public class VehiculoElectrico extends Vehiculo {
    private double porcentajeBateria;
    private int ciclosDeCarga;

    public VehiculoElectrico(
            String placa,
            String marca,
            int kilometraje,
            double porcentajeBateria,
            int ciclosDeCarga) {
        super(placa, marca, kilometraje);
        this.porcentajeBateria = porcentajeBateria;
        this.ciclosDeCarga = ciclosDeCarga;
    }

    public double getPorcentajeBateria() {
        return porcentajeBateria;
    }

    @Override
    public void iniciarRuta() {
        if (estado == EstadoVehiculo.EN_RUTA) {
            throw new IllegalStateException(
                    "El vehículo [" + placa + "] ya se encuentra en ruta."
            );
        }

        if (this.estado == EstadoVehiculo.TALLER) {
            throw new IllegalStateException(
                    "El vehículo [" + placa + "] está en el taller. No puede salir."
            );
        }

        if (this.porcentajeBateria < 15.0) {
            throw new IllegalStateException(
                    "Batería insuficiente (" + porcentajeBateria + "%). Conecte al cargador."
            );
        }

        this.porcentajeBateria -= 15.0;
        this.kilometraje += 50;
        this.estado = EstadoVehiculo.EN_RUTA;
    }

    @Override
    public TipoVehiculo getTipo() {
        return TipoVehiculo.ELECTRICO;
    }

    @Override
    public String evaluarEstadoGeneral() {
        if (ciclosDeCarga > 1000) {
            return "Alerta: Batería muy degradada, requiere reemplazo";
        }
        else if (porcentajeBateria < 20) {
            return "Requiere conexión a estación de carga";
        }
        else {
            return "Batería y sistemas óptimos";
        }
    }
}
