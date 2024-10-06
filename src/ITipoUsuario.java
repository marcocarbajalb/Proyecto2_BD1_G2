/* BASE DE DATOS 1 - Sección 10
 * Marco Carbajal, José Pablo Donado, José Roberto Rodríguez y Oscar Escribá
 * [Proyecto 2 - Desarrollo y consulta de BDs operativas]
 * Clase abstracta ITipoUsuario (para el factory pattern)
 */ 

//Importar las librerías que harán falta para el programa
import java.util.Scanner;

public abstract class ITipoUsuario {
    
    public abstract void setUsuario_id(int id);
    public abstract int getUsuario_id();
    
    public abstract void setNombres(String nombres);
    public abstract String getNombres();

    public abstract void setApellidos(String apellidos);
    public abstract String getApellidos();

    public abstract void setUsername(String username);
    public abstract String getUsername();

    public abstract void setPassword(String password);
    public abstract String getPassword();

    public abstract String getRol();

    public abstract void mostrarMenu(ITipoUsuario usuario_activo, GestionBD gestionBD, Scanner scanString, Scanner scanInt); // Menú que cada rol implementará
}