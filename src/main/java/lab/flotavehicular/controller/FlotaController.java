package lab.flotavehicular.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import lab.flotavehicular.dto.DatosVehiculo;
import lab.flotavehicular.factory.VehiculoFactory;
import lab.flotavehicular.model.EstadoVehiculo;
import lab.flotavehicular.model.TipoCarga;
import lab.flotavehicular.model.TipoVehiculo;
import lab.flotavehicular.model.Vehiculo;
import lab.flotavehicular.model.VehiculoCombustion;
import lab.flotavehicular.model.VehiculoElectrico;
import lab.flotavehicular.model.VehiculoPesado;
import lab.flotavehicular.repository.RepositorioVehiculoTxt;
import lab.flotavehicular.service.FlotaService;

import java.nio.file.Path;
import java.util.Optional;

public class FlotaController {

    @FXML
    private TextField txtPlaca;

    @FXML
    private TextField txtMarca;

    @FXML
    private TextField txtKilometraje;

    @FXML
    private TextField txtCombustible;

    @FXML
    private TextField txtBateria;

    @FXML
    private TextField txtTonelaje;

    @FXML
    private ComboBox<TipoVehiculo> cbxTipoVehiculo;

    @FXML
    private ComboBox<TipoCarga> cbxTipoCarga;

    @FXML
    private Label lblCombustible;

    @FXML
    private Label lblBateria;

    @FXML
    private Label lblTonelaje;

    @FXML
    private Label lblTipoCarga;

    @FXML
    private Label lblMantenimiento;

    @FXML
    private TableView<Vehiculo> tablaVehiculos;

    @FXML
    private TableColumn<Vehiculo, String> colPlaca;

    @FXML
    private TableColumn<Vehiculo, String> colMarca;

    @FXML
    private TableColumn<Vehiculo, TipoVehiculo> colTipo;

    @FXML
    private TableColumn<Vehiculo, Integer> colKilometraje;

    @FXML
    private TableColumn<Vehiculo, EstadoVehiculo> colEstado;

    private final FlotaService flotaService;

    public FlotaController() {
        Path archivoDatos = Path.of("data", "vehiculos.txt");
        this.flotaService = new FlotaService(
                new RepositorioVehiculoTxt(archivoDatos)
        );
    }

    @FXML
    private void initialize() {
        cbxTipoVehiculo.getItems().setAll(TipoVehiculo.values());
        cbxTipoCarga.getItems().setAll(TipoCarga.values());

        colPlaca.setCellValueFactory(new PropertyValueFactory<>("placa"));
        colMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colKilometraje.setCellValueFactory(new PropertyValueFactory<>("kilometraje"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        cbxTipoVehiculo.setValue(TipoVehiculo.COMBUSTION);
        cambiarTipoVehiculo();
        actualizarTabla();
        actualizarEstadoMantenimiento();
    }

    @FXML
    private void cambiarTipoVehiculo() {
        TipoVehiculo tipo = cbxTipoVehiculo.getValue();

        boolean combustion = tipo == TipoVehiculo.COMBUSTION
                || tipo == TipoVehiculo.PESADO;
        boolean electrico = tipo == TipoVehiculo.ELECTRICO;
        boolean pesado = tipo == TipoVehiculo.PESADO;

        mostrarControl(lblCombustible, txtCombustible, combustion);
        mostrarControl(lblBateria, txtBateria, electrico);
        mostrarControl(lblTonelaje, txtTonelaje, pesado);
        mostrarControl(lblTipoCarga, cbxTipoCarga, pesado);
    }

    @FXML
    private void registrarVehiculo() {
        ejecutarAccion(() -> {
            Vehiculo vehiculo = VehiculoFactory.crear(leerFormulario());
            flotaService.agregarVehiculo(vehiculo);
            actualizarTabla();
            limpiarVehiculo();
            mostrarInfo("Vehículo registrado correctamente.");
        });
    }

    @FXML
    private void editarVehiculo() {
        ejecutarAccion(() -> {
            obtenerSeleccionado();
            Vehiculo actualizado = VehiculoFactory.crear(leerFormulario());
            flotaService.actualizarVehiculo(actualizado);
            actualizarTabla();
            limpiarVehiculo();
            mostrarInfo("Vehículo actualizado correctamente.");
        });
    }

    @FXML
    private void eliminarVehiculo() {
        ejecutarAccion(() -> {
            Vehiculo seleccionado = obtenerSeleccionado();

            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Eliminar vehículo");
            confirmacion.setHeaderText("¿Desea eliminar el vehículo "
                    + seleccionado.getPlaca() + "?");

            Optional<ButtonType> respuesta = confirmacion.showAndWait();
            if (respuesta.isPresent() && respuesta.get() == ButtonType.OK) {
                flotaService.eliminarVehiculo(seleccionado.getPlaca());
                actualizarTabla();
                limpiarVehiculo();
                mostrarInfo("Vehículo eliminado correctamente.");
            }
        });
    }

    @FXML
    private void limpiarVehiculo() {
        txtPlaca.clear();
        txtMarca.clear();
        txtKilometraje.clear();
        txtCombustible.clear();
        txtBateria.clear();
        txtTonelaje.clear();
        cbxTipoCarga.setValue(null);
        cbxTipoVehiculo.setValue(TipoVehiculo.COMBUSTION);
        tablaVehiculos.getSelectionModel().clearSelection();
        txtPlaca.setDisable(false);
        cambiarTipoVehiculo();
    }

    @FXML
    private void cargarSeleccion() {
        Vehiculo vehiculo = tablaVehiculos.getSelectionModel().getSelectedItem();
        if (vehiculo == null) {
            return;
        }

        txtPlaca.setText(vehiculo.getPlaca());
        txtMarca.setText(vehiculo.getMarca());
        txtKilometraje.setText(String.valueOf(vehiculo.getKilometraje()));
        cbxTipoVehiculo.setValue(vehiculo.getTipo());
        txtPlaca.setDisable(true);

        txtCombustible.clear();
        txtBateria.clear();
        txtTonelaje.clear();
        cbxTipoCarga.setValue(null);

        if (vehiculo instanceof VehiculoPesado pesado) {
            txtCombustible.setText(String.valueOf(pesado.getNivelCombustible()));
            txtTonelaje.setText(String.valueOf(pesado.getTonelajeMaximo()));
            cbxTipoCarga.setValue(pesado.getTipoCarga());
        } else if (vehiculo instanceof VehiculoElectrico electrico) {
            txtBateria.setText(String.valueOf(electrico.getPorcentajeBateria()));
        } else if (vehiculo instanceof VehiculoCombustion combustion) {
            txtCombustible.setText(String.valueOf(combustion.getNivelCombustible()));
        }

        cambiarTipoVehiculo();
    }

    @FXML
    private void iniciarRuta() {
        ejecutarOperacionSeleccionada("Ruta iniciada correctamente.",
                vehiculo -> flotaService.iniciarRuta(vehiculo.getPlaca()));
    }

    @FXML
    private void finalizarRuta() {
        ejecutarOperacionSeleccionada("Ruta finalizada correctamente.",
                vehiculo -> flotaService.finalizarRuta(vehiculo.getPlaca()));
    }

    @FXML
    private void cargarMercancia() {
        ejecutarAccion(() -> {
            Vehiculo seleccionado = obtenerSeleccionado();
            if (!(seleccionado instanceof VehiculoPesado)) {
                throw new IllegalArgumentException(
                        "Debe seleccionar un vehículo pesado para cargar mercancía."
                );
            }

            TextInputDialog dialogo = new TextInputDialog();
            dialogo.setTitle("Cargar mercancía");
            dialogo.setHeaderText("Vehículo: " + seleccionado.getPlaca());
            dialogo.setContentText("Toneladas a cargar:");

            Optional<String> respuesta = dialogo.showAndWait();
            if (respuesta.isEmpty()) {
                return;
            }

            double toneladas = convertirDouble(respuesta.get(), "toneladas");
            flotaService.cargarMercancia(seleccionado.getPlaca(), toneladas);
            actualizarTabla();
            mostrarInfo("Mercancía cargada correctamente.");
        });
    }

    @FXML
    private void enviarMantenimiento() {
        ejecutarOperacionSeleccionada("Vehículo enviado a mantenimiento.",
                vehiculo -> flotaService.enviarMantenimiento(vehiculo.getPlaca()));
    }

    @FXML
    private void atenderMantenimiento() {
        ejecutarAccion(() -> {
            Vehiculo atendido = flotaService.atenderSiguienteMantenimiento();
            actualizarTabla();
            actualizarEstadoMantenimiento();
            mostrarInfo("Mantenimiento finalizado para " + atendido.getPlaca() + ".");
        });
    }

    private DatosVehiculo leerFormulario() {
        String placa = txtPlaca.getText().trim();
        String marca = txtMarca.getText().trim();
        int kilometraje = convertirEntero(txtKilometraje.getText(), "kilometraje");
        TipoVehiculo tipo = cbxTipoVehiculo.getValue();

        if (tipo == null) {
            throw new IllegalArgumentException("Debe seleccionar el tipo de vehículo.");
        }

        double nivelEnergia;
        double tonelaje = 0.0;
        TipoCarga tipoCarga = null;

        if (tipo == TipoVehiculo.ELECTRICO) {
            nivelEnergia = convertirDouble(txtBateria.getText(), "batería");
        } else {
            nivelEnergia = convertirDouble(txtCombustible.getText(), "combustible");
        }

        if (tipo == TipoVehiculo.PESADO) {
            tonelaje = convertirDouble(txtTonelaje.getText(), "tonelaje máximo");
            tipoCarga = cbxTipoCarga.getValue();
        }

        return new DatosVehiculo(
                placa,
                marca,
                kilometraje,
                tipo,
                nivelEnergia,
                tonelaje,
                tipoCarga
        );
    }

    private Vehiculo obtenerSeleccionado() {
        Vehiculo seleccionado = tablaVehiculos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            throw new IllegalStateException("Debe seleccionar un vehículo de la tabla.");
        }
        return seleccionado;
    }

    private void ejecutarOperacionSeleccionada(
            String mensaje,
            OperacionVehiculo operacion
    ) {
        ejecutarAccion(() -> {
            Vehiculo seleccionado = obtenerSeleccionado();
            operacion.ejecutar(seleccionado);
            actualizarTabla();
            actualizarEstadoMantenimiento();
            cargarSeleccion();
            mostrarInfo(mensaje);
        });
    }

    private void actualizarTabla() {
        tablaVehiculos.getItems().setAll(flotaService.obtenerVehiculos());
        tablaVehiculos.refresh();
    }

    private void actualizarEstadoMantenimiento() {
        lblMantenimiento.setText(
                "Pendientes de mantenimiento: "
                        + flotaService.cantidadPendientesMantenimiento()
        );
    }

    private void mostrarControl(Label etiqueta, javafx.scene.Node control, boolean mostrar) {
        etiqueta.setVisible(mostrar);
        etiqueta.setManaged(mostrar);
        control.setVisible(mostrar);
        control.setManaged(mostrar);
    }

    private int convertirEntero(String texto, String campo) {
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "El campo " + campo + " debe ser un número entero."
            );
        }
    }

    private double convertirDouble(String texto, String campo) {
        try {
            return Double.parseDouble(texto.trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "El campo " + campo + " debe ser numérico."
            );
        }
    }

    private void ejecutarAccion(Accion accion) {
        try {
            accion.ejecutar();
        } catch (RuntimeException e) {
            mostrarError(e.getMessage() == null ? "Ocurrió un error." : e.getMessage());
        }
    }

    private void mostrarInfo(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Flota vehicular");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void mostrarError(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Error");
        alerta.setHeaderText("No se pudo completar la operación");
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    @FunctionalInterface
    private interface Accion {
        void ejecutar();
    }

    @FunctionalInterface
    private interface OperacionVehiculo {
        void ejecutar(Vehiculo vehiculo);
    }
}
