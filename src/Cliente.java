/* BASE DE DATOS 1 - Sección 10
 * Marco Carbajal, José Pablo Donado, José Roberto Rodríguez y Oscar Escribá
 * [Proyecto 2 - Desarrollo y consulta de BDs operativas]
 * Clase para los usuarios de tipo cliente
 */
 
 import java.util.Scanner;

public class Cliente extends ITipoUsuario {
    
    private int usuario_id;
    private String nombres;
    private String apellidos;
    private String username;
    private String password;
    public final String rol = "cliente";

    @Override
    public void setUsuario_id(int usuario_id) {
        this.usuario_id = usuario_id;
    }

    @Override
    public int getUsuario_id() {
        return usuario_id;
    }

    @Override
    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    @Override
    public String getNombres() {
        return nombres;
    }

    @Override
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    @Override
    public String getApellidos() {
        return apellidos;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getRol() {
        return rol;
    }
    
    @Override
    public void mostrarMenu(ITipoUsuario usuario_activo, GestionBD gestionBD, Scanner scanString, Scanner scanInt) {
        // Menú del cliente
    }
}