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

    public LocalDateTime getTiempoSimuladoActual() {
        if (isRunning) {
            long now = System.currentTimeMillis();
            long elapsedMillis = now - lastUpdate;
            long simulatedMillis = (long) (elapsedMillis * timeSpeed);
            long halfHoursPassed = simulatedMillis / (1000 * 60 * 30); // Calcula cuántos bloques de media hora han pasado

            if (halfHoursPassed > 0) {
                simulatedTime = simulatedTime.plus(Duration.ofMinutes(halfHoursPassed * 30));
                lastUpdate = now;
                actualizarMesas();
                verificarInsumosBajos(); 
            }
        }
        return simulatedTime;
    }

    public String getFechaFormateada() {
        return getTiempoSimuladoActual().format(SQL_FORMATTER);
    }

    public boolean fechaPasada(String fecha) {
        LocalDateTime fechaComparar = LocalDateTime.parse(fecha, FORMATTER);
        return getTiempoSimuladoActual().isAfter(fechaComparar);
    }

    private void actualizarMesas() {
        LocalDateTime tiempoActual = simulatedTime;
        String fecha = tiempoActual.toLocalDate().toString();
        String hora = tiempoActual.toLocalTime().format(DateTimeFormatter.ofPattern("HH"));
        String hora_modificada = hora + ":00:00";

        gestionBD.actualizarEstadoMesas(fecha, hora_modificada);
    }

    // alerta para verificar los insumos con cantidades bajas... 
    private void verificarInsumosBajos() {
        
        // Llamar a la función en GestionBD para obtener los insumos por debajo del 15%
        List<List<Object>> insumosBajos = gestionBD.obtenerInsumosBajoPorcentaje(15);
        
        if (insumosBajos.isEmpty()) {
            System.out.println("\n\t\tNo hay insumos con menos del 15% de su capacidad.");
        } else {
            System.out.println("\n╠═════════════════════════¡ALERTA! INSUMOS BAJOS════════════════════════╣");
            System.out.printf("%-30s %-10s%n", "Nombre del insumo", "Cantidad restante");
            System.out.println("--------------------------------------------");
            for (List<Object> insumo : insumosBajos) {
                String nombreInsumo = (String) insumo.get(0);
                int cantidad = (int) insumo.get(1);
                System.out.printf("%-30s %-10d%n", nombreInsumo, cantidad);
            }
        }
    }
}