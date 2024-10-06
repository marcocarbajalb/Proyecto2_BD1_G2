/* BASE DE DATOS 1 - Sección 10
 * Marco Carbajal, José Pablo Donado, José Roberto Rodríguez y Oscar Escribá
 * [Proyecto 2 - Desarrollo y consulta de BDs operativas]
 * Conexión a la base de datos
 */

//Importar las librerías que harán falta para el programa
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {
    
    private final String url = "jdbc:postgresql://localhost:5432/Proyecto2"; // URL de la base de datos
    private final String usuario = "postgres"; // Usuario de la base de datos
    private final String password = "admin123"; // Contraseña de la base de datos
    private Connection conexion = null;

    public Connection conectar() {
        
        Connection conexion = null;

        try {
            // Cargar el controlador de PostgreSQL
            Class.forName("org.postgresql.Driver");

            // Establecer la conexión
            conexion = DriverManager.getConnection(url, usuario, password);
            System.out.println("\n[Conexión exitosa a la base de datos]");

        } catch (ClassNotFoundException e) {
            System.out.println("\n[Error: No se encontró el controlador de PostgreSQL]");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("\n[Error al conectar con la base de datos] \n" + e.getMessage());
            e.printStackTrace();
        }
        return conexion;
    }

    public void cerrarConexion() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
                System.out.println("\n[Conexión con la base de datos cerrada]");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}