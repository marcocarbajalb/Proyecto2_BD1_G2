import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.Duration;

public class TimeSimulator {
    private LocalDateTime simulatedTime;
    private double timeSpeed = 1.0; // Velocidad de tiempo (1.0 es tiempo real)
    private boolean isRunning = false;
    private long lastUpdate;
    
    // Formato SQL para la fecha
    private static final DateTimeFormatter SQL_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Constructor
    public TimeSimulator() {
        this.simulatedTime = LocalDateTime.of(2024, 10, 1, 7, 0); // Fecha inicial predeterminada
        this.lastUpdate = System.currentTimeMillis();
    }

    public void start() {
        this.isRunning = true;
        this.lastUpdate = System.currentTimeMillis();
    }

    public void stop() {
        this.isRunning = false;
    }

    public void setTimeSpeed(double speed) {
        this.timeSpeed = speed;
    }

    public LocalDateTime getCurrentSimulatedTime() {
        if (isRunning) {
            long now = System.currentTimeMillis();
            long elapsedMillis = now - lastUpdate;
            long simulatedMillis = (long) (elapsedMillis * timeSpeed);
            long halfHoursPassed = simulatedMillis / (1000 * 60 * 30); // Calcula cuántos bloques de media hora han pasado

            if (halfHoursPassed > 0) {
                simulatedTime = simulatedTime.plus(Duration.ofMinutes(halfHoursPassed * 30));
                lastUpdate = now;
            }
        }
        return simulatedTime;
    }

    public String getCurrentSimulatedTimeFormatted() {
        return getCurrentSimulatedTime().format(SQL_FORMATTER);
    }
}