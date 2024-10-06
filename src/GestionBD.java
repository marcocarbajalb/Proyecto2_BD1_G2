/* BASE DE DATOS 1 - Sección 10
 * Marco Carbajal, José Pablo Donado, José Roberto Rodríguez y Oscar Escribá
 * [Proyecto 2 - Desarrollo y consulta de BDs operativas]
 * Gestionar la base de datos
 */

//Importar las librerías que harán falta para el programa
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class GestionBD {
    private Connection conexion;

    // Constructor que recibe la conexión a la base de datos
    public GestionBD(Connection conexion) {
        this.conexion = conexion;}

    // [REGISTRAR USUARIOS E INICIAR SESIÓN]

    // Método para verificar si un username existe en la tabla usuarios
    public boolean existeUsername(String username) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE username = ?";
        try (PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0;}}} // Si count es mayor a 0, el username ya está registrado en la BD
            catch (SQLException e) {
            e.printStackTrace();}
        return false;}

    // Método para verificar si la password coincide con el username
    public boolean validarPassword(String username, String password) {
        String sql = "SELECT password FROM usuarios WHERE username = ?";
        try (PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String passwordBD = resultSet.getString("password");
                    return passwordBD.equals(password);}}}  // Compara las contraseñas y regresa true si coinciden
                catch (SQLException e) {
            e.printStackTrace();}
        return false;}

    // Método para insertar un usuario a la base de datos
    public void insertarUsuario(ITipoUsuario usuario) {
        String sql = "INSERT INTO usuarios (username, password, nombres, apellidos, rol_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setString(1, usuario.getUsername());
            statement.setString(2, usuario.getPassword());
            statement.setString(3, usuario.getNombres());
            statement.setString(4, usuario.getApellidos());
            List<String> roles = Arrays.asList("cliente", "mesero", "gerente", "administrador");
            statement.setInt(5, roles.indexOf(usuario.getRol())+1);  // Inserta el rol_id según el rol del usuario
            statement.executeUpdate();}  // Ejecuta la inserción
        catch (SQLException e) {
            e.printStackTrace();}
        
        // Si el usuario es mesero o gerente, se debe insertar también en la tabla de personal
        if(usuario.getRol().equals("mesero")||usuario.getRol().equals("gerente")) {
            String sql2 = "SELECT usuario_id FROM usuarios WHERE username = ?";
            try (PreparedStatement statement = conexion.prepareStatement(sql2)) {
                statement.setString(1, usuario.getUsername());
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        int usuario_id = resultSet.getInt("usuario_id");
                        usuario.setUsuario_id(usuario_id);}}}  // Obtiene el usuario_id del usuario insertado
                catch (SQLException e) {
                e.printStackTrace();}
            
            String sql3 = "INSERT INTO personal (usuario_id, restaurante_id) VALUES (?, ?)";
            try (PreparedStatement statement = conexion.prepareStatement(sql3)) {
                statement.setInt(1, usuario.getUsuario_id());
                if(usuario.getRol().equals("mesero")){
                    statement.setInt(2, ((Mesero)usuario).getRestaurante_id());}
                else {
                    statement.setInt(2, ((Gerente)usuario).getRestaurante_id());}
                statement.setInt(2, ((Mesero)usuario).getRestaurante_id());
                statement.executeUpdate();}  // Inserta el usuario en la tabla personal
            catch (SQLException e) {
                e.printStackTrace();}
        }
    }

    // Método para instanciar un usuario de la base de datos
    public ITipoUsuario obtenerUsuario(String username) {
        String sql = "SELECT * FROM usuarios WHERE username = ?";
        try (PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setString(1, username);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Integer rol_id = resultSet.getInt("rol_id");
                    ITipoUsuario usuario = TipoUsuarioFactory.getTipoUsuarioInstance(rol_id);
                    usuario.setUsuario_id(resultSet.getInt("usuario_id"));
                    usuario.setNombres(resultSet.getString("nombres"));
                    usuario.setApellidos(resultSet.getString("apellidos"));
                    usuario.setUsername(resultSet.getString("username"));
                    usuario.setPassword(resultSet.getString("password"));
                    
                    if (usuario.getRol().equals("mesero") || usuario.getRol().equals("gerente")) {
                        String sql2 = "SELECT restaurante_id FROM personal WHERE usuario_id = ?";
                        try (PreparedStatement statement2 = conexion.prepareStatement(sql2)) {
                            statement2.setInt(1, usuario.getUsuario_id());
                            
                            try (ResultSet resultSet2 = statement2.executeQuery()) {
                                if (resultSet2.next()) {
                                    if (usuario.getRol().equals("mesero")) {
                                        ((Mesero) usuario).setRestaurante_id(resultSet2.getInt("restaurante_id"));
                                    } else {
                                        ((Gerente) usuario).setRestaurante_id(resultSet2.getInt("restaurante_id"));
                                    }
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    return usuario;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}