package lab.flotavehicular.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;

public class ColaMantenimiento<T extends Vehiculo> {
    private final Queue<T> filaDeEspera;

    public ColaMantenimiento() {
        this.filaDeEspera = new LinkedList<>();
    }

    public void encolar(T vehiculo) {
        Objects.requireNonNull(vehiculo, "El vehículo no puede ser nulo.");

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

    /**
     * Vuelve a colocar en la cola un vehículo cargado desde persistencia
     * que ya estaba marcado como TALLER.
     */
    public void restaurarPendiente(T vehiculo) {
        Objects.requireNonNull(vehiculo, "El vehículo no puede ser nulo.");

        if (vehiculo.getEstado() == EstadoVehiculo.TALLER
                && !filaDeEspera.contains(vehiculo)) {
            filaDeEspera.offer(vehiculo);
        }
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
