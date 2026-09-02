package lab.flotavehicular.model;

public class VehiculoPesado extends VehiculoCombustion {
    private double tonelajeMaximo;
    private double cargaActual;
    private TipoCarga tipoCarga;

    public VehiculoPesado(
            String placa,
            String marca,
            int kilometraje,
            double nivelCombustible,
            double tonelajeMaximo,
            TipoCarga tipoCarga) {
        super(placa, marca, kilometraje, nivelCombustible);
        this.tonelajeMaximo = tonelajeMaximo;
        this.cargaActual = 0.0;
        this.tipoCarga = tipoCarga;
    }

    public double getTonelajeMaximo() {
        return tonelajeMaximo;
    }

    public double getCargaActual() {
        return cargaActual;
    }

    public TipoCarga getTipoCarga() {
        return tipoCarga;
    }

    public void cargarMercancia(double pesoEnToneladas) {
        if (pesoEnToneladas <= 0) {
            throw new IllegalArgumentException(
                    "El peso de la mercancía debe ser mayor que cero."
            );
        }

        if (pesoEnToneladas > tonelajeMaximo) {
            throw new IllegalArgumentException(
                    "Sobrecarga: el camión solo soporta " + tonelajeMaximo + " toneladas."
            );
        }

        this.cargaActual = pesoEnToneladas;
    }

    @Override
    public void iniciarRuta() {
        if (cargaActual == 0.0) {
            throw new IllegalStateException(
                    "El camión [" + placa + "] no puede salir a ruta vacío."
            );
        }

        super.iniciarRuta();
        this.kilometraje += 150;
    }

    @Override
    public void finalizarRuta() {
        super.finalizarRuta();
        this.cargaActual = 0.0;
    }

    @Override
    public TipoVehiculo getTipo() {
        return TipoVehiculo.PESADO;
    }

    @Override
    public String evaluarEstadoGeneral() {
        String estadoPadre = super.evaluarEstadoGeneral();

        if (kilometraje > 150000) {
            return estadoPadre
                    + " | Alerta: Requiere revisión de ejes y frenos de aire.";
        }

        return estadoPadre;
    }
}
