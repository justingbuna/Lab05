package lab.flotavehicular.model;

import java.util.ArrayList;
import java.util.List;

public class Flota {
    private final List<Vehiculo> vehiculos;

    public Flota() {
        this.vehiculos = new ArrayList<>();
    }

    public void agregarVehiculo(Vehiculo vehiculo) {
        boolean existe = vehiculos.stream()
                .anyMatch(v ->
                        v.getPlaca().equalsIgnoreCase(
                                vehiculo.getPlaca()
                        )
                );

        if (existe) {
            throw new IllegalArgumentException(
                    "Ya existe un vehículo con esa placa."
            );
        }

        vehiculos.add(vehiculo);
    }

    public List<Vehiculo> obtenerVehiculos() {
        return new ArrayList<>(vehiculos);
    }

    public void actualizarVehiculo(Vehiculo vehiculoActualizado) {
        for (int i = 0; i < vehiculos.size(); i++) {
            if (vehiculos.get(i).getPlaca()
                    .equalsIgnoreCase(vehiculoActualizado.getPlaca())) {
                vehiculos.set(i, vehiculoActualizado);
                return;
            }
        }

        throw new IllegalArgumentException(
                "No se encontró el vehículo."
        );
    }

    public void eliminarVehiculo(String placa) {
        boolean eliminado = vehiculos.removeIf(
                vehiculo ->
                        vehiculo.getPlaca().equalsIgnoreCase(placa)
        );

        if (!eliminado) {
            throw new IllegalArgumentException(
                    "No se encontró el vehículo."
            );
        }
    }
}
