package lab.flotavehicular.service;

import lab.flotavehicular.model.ColaMantenimiento;
import lab.flotavehicular.model.EstadoVehiculo;
import lab.flotavehicular.model.Vehiculo;
import lab.flotavehicular.model.VehiculoPesado;
import lab.flotavehicular.repository.RepositorioVehiculo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Capa de lógica de aplicación: aplica reglas y coordina la persistencia.
 */
public class FlotaService {

    private final List<Vehiculo> vehiculos;
    private final RepositorioVehiculo repositorio;
    private final ColaMantenimiento<Vehiculo> colaMantenimiento;

    public FlotaService(RepositorioVehiculo repositorio) {
        this.repositorio = Objects.requireNonNull(
                repositorio, "El repositorio no puede ser nulo."
        );
        this.vehiculos = new ArrayList<>(repositorio.cargarTodos());
        this.colaMantenimiento = new ColaMantenimiento<>();

        for (Vehiculo vehiculo : vehiculos) {
            colaMantenimiento.restaurarPendiente(vehiculo);
        }
    }

    public void agregarVehiculo(Vehiculo vehiculo) {
        Objects.requireNonNull(vehiculo, "El vehículo no puede ser nulo.");

        boolean existe = vehiculos.stream()
                .anyMatch(v -> v.getPlaca().equalsIgnoreCase(vehiculo.getPlaca()));

        if (existe) {
            throw new IllegalArgumentException("Ya existe un vehículo con esa placa.");
        }

        vehiculos.add(vehiculo);
        guardarCambios();
    }

    public List<Vehiculo> obtenerVehiculos() {
        return new ArrayList<>(vehiculos);
    }

    public Vehiculo buscarVehiculo(String placa) {
        if (placa == null || placa.isBlank()) {
            throw new IllegalArgumentException("Debe indicar una placa.");
        }

        return vehiculos.stream()
                .filter(v -> v.getPlaca().equalsIgnoreCase(placa))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró el vehículo."
                ));
    }

    public void actualizarVehiculo(Vehiculo vehiculoActualizado) {
        Objects.requireNonNull(
                vehiculoActualizado, "El vehículo actualizado no puede ser nulo."
        );

        for (int i = 0; i < vehiculos.size(); i++) {
            Vehiculo actual = vehiculos.get(i);
            if (actual.getPlaca().equalsIgnoreCase(vehiculoActualizado.getPlaca())) {
                if (actual.getEstado() != EstadoVehiculo.DISPONIBLE) {
                    throw new IllegalStateException(
                            "Solo se puede editar un vehículo disponible."
                    );
                }

                vehiculos.set(i, vehiculoActualizado);
                guardarCambios();
                return;
            }
        }

        throw new IllegalArgumentException("No se encontró el vehículo.");
    }

    public void eliminarVehiculo(String placa) {
        Vehiculo encontrado = buscarVehiculo(placa);

        if (encontrado.getEstado() != EstadoVehiculo.DISPONIBLE) {
            throw new IllegalStateException(
                    "Solo se puede eliminar un vehículo disponible."
            );
        }

        vehiculos.remove(encontrado);
        guardarCambios();
    }

    public void iniciarRuta(String placa) {
        Vehiculo vehiculo = buscarVehiculo(placa);
        vehiculo.iniciarRuta();
        guardarCambios();
    }

    public void finalizarRuta(String placa) {
        Vehiculo vehiculo = buscarVehiculo(placa);
        vehiculo.finalizarRuta();
        guardarCambios();
    }

    public void cargarMercancia(String placa, double toneladas) {
        Vehiculo vehiculo = buscarVehiculo(placa);

        if (!(vehiculo instanceof VehiculoPesado pesado)) {
            throw new IllegalArgumentException(
                    "La carga de mercancía solo aplica a vehículos pesados."
            );
        }

        if (vehiculo.getEstado() != EstadoVehiculo.DISPONIBLE) {
            throw new IllegalStateException(
                    "Solo puede cargar mercancía en un vehículo disponible."
            );
        }

        pesado.cargarMercancia(toneladas);
        guardarCambios();
    }

    public void enviarMantenimiento(String placa) {
        Vehiculo vehiculo = buscarVehiculo(placa);
        colaMantenimiento.encolar(vehiculo);
        guardarCambios();
    }

    public Vehiculo atenderSiguienteMantenimiento() {
        Vehiculo vehiculo = colaMantenimiento.atenderSiguiente();
        guardarCambios();
        return vehiculo;
    }

    public int cantidadPendientesMantenimiento() {
        return colaMantenimiento.cantidadPendientes();
    }

    public void guardarCambios() {
        repositorio.guardarTodos(vehiculos);
    }
}
