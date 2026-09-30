package lab.flotavehicular.dto;

import lab.flotavehicular.model.TipoCarga;
import lab.flotavehicular.model.TipoVehiculo;

/**
 * Agrupa los datos ingresados en el formulario antes de crear un vehículo.
 */
public record DatosVehiculo(
        String placa,
        String marca,
        int kilometraje,
        TipoVehiculo tipo,
        double nivelEnergia,
        double tonelajeMaximo,
        TipoCarga tipoCarga
) {
}
