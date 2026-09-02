package lab.flotavehicular.model;

import java.util.LinkedList;
import java.util.Queue;
import java.util.List;
import java.util.ArrayList;

public class ColaMantenimiento<T extends Vehiculo> {
    private Queue<T> filaDeEspera;

    public ColaMantenimiento() {
        this.filaDeEspera = new LinkedList<>();
    }

    public void encolar(T vehiculo) {
        if (vehiculo.getEstado() == EstadoVehiculo.TALLER) {
            throw new IllegalStateException(
                    "El vehículo ya se encuentra en mantenimiento."
            );
        }

        if (vehiculo.getEstado() == EstadoVehiculo.EN_RUTA) {
            throw new IllegalStateException(
                    "No puede enviar a mantenimiento un vehículo que está en ruta."
            );
        }

        vehiculo.setEstado(EstadoVehiculo.TALLER);
        filaDeEspera.offer(vehiculo);
    }

    public T atenderSiguiente() {
        if (filaDeEspera.isEmpty()) {
            throw new IllegalStateException(
                    "No hay vehículos en la cola de mantenimiento."
            );
        }

        T vehiculoAtendido = filaDeEspera.poll();
        vehiculoAtendido.setEstado(EstadoVehiculo.DISPONIBLE);

        return vehiculoAtendido;
    }

    public boolean estaVacia() {
        return filaDeEspera.isEmpty();
    }

    public int cantidadPendientes() {
        return filaDeEspera.size();
    }

    public List<T> obtenerListaPendientes() {
        return new ArrayList<>(filaDeEspera);
    }
}
