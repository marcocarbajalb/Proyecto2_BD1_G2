import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.Duration;
import java.sql.Connection;
import java.util.List;


public class TimeSimulator {
    private LocalDateTime simulatedTime;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private double timeSpeed = 120; // El tiempo pasa 120 veces más rápido (cada minuto en la vida real son dos horas en el simulador)
    private boolean isRunning = false;
    private long lastUpdate;
    private GestionBD gestionBD;
    
    // Formato SQL para la fecha
    private static final DateTimeFormatter SQL_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Constructor
    public TimeSimulator(Connection conexion) {
        this.simulatedTime = LocalDateTime.of(2024, 10, 1, 7, 0); // Fecha inicial predeterminada
        this.lastUpdate = System.currentTimeMillis();
        this.gestionBD = new GestionBD(conexion);
    }

    public void start() {
        this.isRunning = true;
        this.lastUpdate = System.currentTimeMillis();
    }

    public void stop() {
        this.isRunning = false;
    }

    public LocalDateTime getTiempoSimuladoActual(boolean mostrar_alerta, int restaurante_id) {
        if (isRunning) {
            long now = System.currentTimeMillis();
            long elapsedMillis = now - lastUpdate;
            long simulatedMillis = (long) (elapsedMillis * timeSpeed);
            long halfHoursPassed = simulatedMillis / (1000 * 60 * 30); // Calcula cuántos bloques de media hora han pasado

            if (halfHoursPassed > 0) {
                simulatedTime = simulatedTime.plus(Duration.ofMinutes(halfHoursPassed * 30));
                lastUpdate = now;
                actualizarMesas();
                verificarInsumosBajos(mostrar_alerta, restaurante_id); 
            }
        }
        return simulatedTime;
    }

    public String getFechaFormateada(boolean mostrar_alerta, int restaurante_id) {
        return getTiempoSimuladoActual(mostrar_alerta, restaurante_id).format(SQL_FORMATTER);
    }

    public String getFechaProximaSemana() {
        LocalDateTime fechaProximaSemana = getTiempoSimuladoActual(false, 0).plusDays(7);
        return fechaProximaSemana.format(SQL_FORMATTER);
    }

    public boolean fechaPasada(String fecha) {
        LocalDateTime fechaComparar = LocalDateTime.parse(fecha, FORMATTER);
        return getTiempoSimuladoActual(false, 0).isAfter(fechaComparar);
    }

    private void actualizarMesas() {
        LocalDateTime tiempoActual = simulatedTime;
        String fecha = tiempoActual.toLocalDate().toString();
        String hora = tiempoActual.toLocalTime().format(DateTimeFormatter.ofPattern("HH"));
        String hora_modificada = hora + ":00:00";

        gestionBD.actualizarEstadoMesas(fecha, hora_modificada);
    }

    // alerta para verificar los insumos con cantidades bajas... 
    private void verificarInsumosBajos(boolean mostrar_alerta, int restaurante_id) {

        if (mostrar_alerta) {
            // Llamar a la función en GestionBD para obtener los insumos por debajo del 15%
            List<List<Object>> insumosBajos = gestionBD.obtenerInsumosBajoPorcentaje(15, restaurante_id);

            LocalDateTime tiempoActual = simulatedTime;
            String fecha = tiempoActual.toLocalDate().toString();
            LocalDateTime tiempoProximaSemana = tiempoActual.plusDays(7);
            String fechaProximaSemana = tiempoProximaSemana.toLocalDate().toString();

            List<List<Object>> insumosPorCaducar = gestionBD.obtenerInsumosProximosACaducar(fecha, fechaProximaSemana, restaurante_id);
            
            if (insumosBajos.isEmpty()) {
                System.out.println("\n\t    [No hay insumos con menos del 15% de su capacidad]");
            } else {
                System.out.println("\n╠════════════════════════¡ALERTA: INSUMOS BAJOS!════════════════════════╣\n");
                System.out.printf("%-30s %-20s %-25s%n", "Nombre del insumo", "Und. restantes", "Sucursal");
                System.out.println("-------------------------------------------------------------------------");
                for (List<Object> insumo : insumosBajos) {
                    String nombreInsumo = (String) insumo.get(0);
                    int cantidad = (int) insumo.get(1);
                    String nombreRestaurante = (String) insumo.get(2);
                    System.out.printf("%-30s %-20s %-25s%n", nombreInsumo, cantidad, nombreRestaurante);
                }}
            
            if (insumosPorCaducar.isEmpty()) {
                System.out.println("\n\t    [No hay insumos a una semana o menos de caducar]");
            } else {
                System.out.println("\n╠══════════════════════¡ALERTA: INSUMOS POR CADUCAR!════════════════════╣\n");
                System.out.printf("%-30s %-20s %-25s%n", "Nombre del insumo", "Fecha caducidad", "Sucursal");
                System.out.println("-------------------------------------------------------------------------");
                for (List<Object> insumo : insumosPorCaducar) {
                    String nombreInsumo = (String) insumo.get(0);
                    String fechaCaducidad = insumo.get(3).toString();
                    String nombreRestaurante = (String) insumo.get(2);
                    System.out.printf("%-30s %-20s %-25s%n", nombreInsumo, fechaCaducidad, nombreRestaurante);
                }}}}
}