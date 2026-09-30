package lab.flotavehicular.repository;

import lab.flotavehicular.model.EstadoVehiculo;
import lab.flotavehicular.model.TipoCarga;
import lab.flotavehicular.model.TipoVehiculo;
import lab.flotavehicular.model.Vehiculo;
import lab.flotavehicular.model.VehiculoCombustion;
import lab.flotavehicular.model.VehiculoElectrico;
import lab.flotavehicular.model.VehiculoPesado;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Implementación del Repository que guarda una línea por vehículo en un TXT.
 */
public class RepositorioVehiculoTxt implements RepositorioVehiculo {

    private static final String ENCABEZADO =
            "tipo;placa;marca;kilometraje;estado;energia;ciclos;tonelaje;carga;tipoCarga";

    private final Path archivo;

    public RepositorioVehiculoTxt(Path archivo) {
        this.archivo = Objects.requireNonNull(
                archivo, "La ruta del archivo no puede ser nula."
        );
    }

    @Override
    public List<Vehiculo> cargarTodos() {
        if (Files.notExists(archivo)) {
            return new ArrayList<>();
        }

        try {
            List<Vehiculo> vehiculos = new ArrayList<>();

            List<String> lineas = Files.readAllLines(archivo, StandardCharsets.UTF_8);

            for (int i = 0; i < lineas.size(); i++) {
                String linea = lineas.get(i);

                if (linea.isBlank() || linea.equals(ENCABEZADO)) {
                    continue;
                }

                try {
                    vehiculos.add(convertirDesdeLinea(linea));
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException(
                            "Línea " + (i + 1) + " inválida: " + linea, e
                    );
                }
            }

            return vehiculos;
        } catch (IOException | IllegalArgumentException e) {
            throw new PersistenciaException(
                    "No se pudieron cargar los vehículos desde " + archivo + ".", e
            );
        }
    }

    @Override
    public void guardarTodos(List<Vehiculo> vehiculos) {
        try {
            Objects.requireNonNull(
                    vehiculos, "La lista de vehículos no puede ser nula."
            );

            Path carpeta = archivo.getParent();
            if (carpeta != null) {
                Files.createDirectories(carpeta);
            }

            List<String> lineas = new ArrayList<>();
            lineas.add(ENCABEZADO);
            for (Vehiculo vehiculo : vehiculos) {
                lineas.add(convertirALinea(vehiculo));
            }

            Files.write(
                    archivo,
                    lineas,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE
            );
        } catch (IOException | RuntimeException e) {
            throw new PersistenciaException(
                    "No se pudieron guardar los vehículos en " + archivo + ".", e
            );
        }
    }

    private String convertirALinea(Vehiculo vehiculo) {
        Objects.requireNonNull(vehiculo, "La lista contiene un vehículo nulo.");

        String energia = "";
        String ciclos = "";
        String tonelaje = "";
        String carga = "";
        String tipoCarga = "";

        if (vehiculo instanceof VehiculoPesado pesado) {
            energia = String.valueOf(pesado.getNivelCombustible());
            tonelaje = String.valueOf(pesado.getTonelajeMaximo());
            carga = String.valueOf(pesado.getCargaActual());
            if (pesado.getTipoCarga() == null) {
                throw new IllegalArgumentException(
                        "El vehículo pesado " + pesado.getPlaca()
                                + " no tiene un tipo de carga."
                );
            }
            tipoCarga = pesado.getTipoCarga().name();
        } else if (vehiculo instanceof VehiculoElectrico electrico) {
            energia = String.valueOf(electrico.getPorcentajeBateria());
            ciclos = String.valueOf(electrico.getCiclosDeCarga());
        } else if (vehiculo instanceof VehiculoCombustion combustion) {
            energia = String.valueOf(combustion.getNivelCombustible());
        }

        return String.join(";",
                vehiculo.getTipo().name(),
                validarTexto(vehiculo.getPlaca(), "placa"),
                validarTexto(vehiculo.getMarca(), "marca"),
                String.valueOf(vehiculo.getKilometraje()),
                vehiculo.getEstado().name(),
                energia,
                ciclos,
                tonelaje,
                carga,
                tipoCarga
        );
    }

    private String validarTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(
                    "El campo " + campo + " no puede estar vacío."
            );
        }

        if (valor.contains(";") || valor.contains("\n") || valor.contains("\r")) {
            throw new IllegalArgumentException(
                    "El campo " + campo + " contiene caracteres no permitidos."
            );
        }

        return valor;
    }

    private Vehiculo convertirDesdeLinea(String linea) {
        String[] datos = linea.split(";", -1);
        if (datos.length != 10) {
            throw new IllegalArgumentException("Línea inválida: " + linea);
        }

        TipoVehiculo tipo = TipoVehiculo.valueOf(datos[0].trim());
        String placa = validarTexto(datos[1], "placa");
        String marca = validarTexto(datos[2], "marca");
        int kilometraje = Integer.parseInt(datos[3].trim());
        EstadoVehiculo estado = EstadoVehiculo.valueOf(datos[4].trim());
        double energia = Double.parseDouble(datos[5].trim());

        Vehiculo vehiculo = switch (tipo) {
            case COMBUSTION -> new VehiculoCombustion(
                    placa, marca, kilometraje, energia
            );
            case ELECTRICO -> new VehiculoElectrico(
                    placa, marca, kilometraje, energia,
                    Integer.parseInt(datos[6].trim())
            );
            case PESADO -> {
                VehiculoPesado pesado = new VehiculoPesado(
                        placa, marca, kilometraje, energia,
                        Double.parseDouble(datos[7].trim()),
                        TipoCarga.valueOf(datos[9].trim())
                );
                double cargaActual = Double.parseDouble(datos[8].trim());
                if (cargaActual < 0) {
                    throw new IllegalArgumentException(
                            "La carga actual no puede ser negativa."
                    );
                }
                if (cargaActual > 0) {
                    pesado.cargarMercancia(cargaActual);
                }
                yield pesado;
            }
        };

        vehiculo.setEstado(estado);
        return vehiculo;
    }
}
