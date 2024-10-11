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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.sql.Date;
import java.sql.Time;
import java.sql.Array;
import java.time.LocalDate;
import java.time.LocalTime;

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

    public boolean verificarMesasDisponibles(int restaurante_id, int cantidad_mesas, String fecha, String hora) {
        String sql = "SELECT COUNT(m.mesa_id) " +
                    "FROM mesas m " +
                    "WHERE m.restaurante_id = ? " +
                    "AND m.estado = 'disponible' " +
                    "AND m.mesa_id NOT IN ( " +
                    "    SELECT rm.mesa_id " +
                    "    FROM reserva r " +
                    "    JOIN reserva_mesa rm ON r.reserva_id = rm.reserva_id " +
                    "    WHERE r.restaurante_id = ? " +
                    "    AND r.fecha = ?::DATE " +
                    "    AND r.hora = ?::TIME " +
                    ")";
        
        try (PreparedStatement statement = conexion.prepareStatement(sql)) {
            
            LocalDate localDate = LocalDate.parse(fecha);
            Date sqlDate = Date.valueOf(localDate);
            LocalTime localTime = LocalTime.parse(hora);
            Time sqlTime = Time.valueOf(localTime);
    
            statement.setInt(1, restaurante_id);
            statement.setInt(2, restaurante_id);
            statement.setDate(3, sqlDate);
            statement.setTime(4, sqlTime);
        
            ResultSet rs = statement.executeQuery();
        
            if (rs.next()) {
                int mesasDisponibles = rs.getInt(1);
                // Verifica si hay suficientes mesas disponibles
                if (mesasDisponibles >= cantidad_mesas) {
                    return true;
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }    

    public List<Integer> obtenerMesasDisponibles(int restaurante_id, String fecha, String hora) {
        List<Integer> mesasDisponibles = new ArrayList<>();
        String sql = "SELECT m.mesa_id " +
                     "FROM mesas m " +
                     "WHERE m.restaurante_id = ? " +
                     "AND m.estado = 'disponible' " +
                     "AND m.mesa_id NOT IN ( " +
                     "    SELECT rm.mesa_id " +
                     "    FROM reserva r " +
                     "    JOIN reserva_mesa rm ON r.reserva_id = rm.reserva_id " +
                     "    WHERE r.restaurante_id = ? " +
                     "    AND r.fecha = ?::DATE " +
                     "    AND r.hora = ?::TIME " +
                     ") ORDER BY m.mesa_id ASC";
        
        try (PreparedStatement statement = conexion.prepareStatement(sql)) {
            // Convertir fecha y hora
            LocalDate localDate = LocalDate.parse(fecha);
            Date sqlDate = Date.valueOf(localDate);
            LocalTime localTime = LocalTime.parse(hora);
            Time sqlTime = Time.valueOf(localTime);
    
            statement.setInt(1, restaurante_id);
            statement.setInt(2, restaurante_id);
            statement.setDate(3, sqlDate);
            statement.setTime(4, sqlTime);
        
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                mesasDisponibles.add(rs.getInt("mesa_id"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
        return mesasDisponibles; // Retorna la lista de mesas disponibles
    }

    public int realizarReserva(int usuario_id, int restaurante_id, int num_personas, int cantidad_mesas, String fecha, String hora) {
        String sqlReserva = "INSERT INTO reserva (fecha, hora, num_personas, restaurante_id) VALUES (?, ?, ?, ?) RETURNING reserva_id";
        String sqlReservaMesa = "INSERT INTO reserva_mesa (reserva_id, mesa_id) VALUES (?, ?)";
        String sqlUsuarioReserva = "INSERT INTO usuario_reserva (usuario_id, reserva_id) VALUES (?, ?)";
        int reserva_id = 0;

        try (PreparedStatement pstmtReserva = conexion.prepareStatement(sqlReserva);
             PreparedStatement pstmtReservaMesa = conexion.prepareStatement(sqlReservaMesa);
             PreparedStatement pstmtUsuarioReserva = conexion.prepareStatement(sqlUsuarioReserva)) {
        
            LocalDate localDate = LocalDate.parse(fecha);
            java.sql.Date sqlDate = java.sql.Date.valueOf(localDate);
            LocalTime localTime = LocalTime.parse(hora);
            java.sql.Time sqlTime = java.sql.Time.valueOf(localTime);
    
            // 1. Insertar en la tabla reserva
            pstmtReserva.setDate(1, sqlDate);
            pstmtReserva.setTime(2, sqlTime);
            pstmtReserva.setInt(3, num_personas);
            pstmtReserva.setInt(4, restaurante_id);
        
            // Obtener el ID de la reserva recién creada
            ResultSet rs = pstmtReserva.executeQuery();
            if (rs.next()) {
                reserva_id = rs.getInt(1);
        
                // 2. Insertar en la tabla reserva_mesa para cada mesa seleccionada
                List<Integer> mesasSeleccionadas = obtenerMesasDisponibles(restaurante_id, fecha, hora);
                int mesas_insertadas = 0;
                for (int mesa_id : mesasSeleccionadas) {
                    if (mesas_insertadas == cantidad_mesas) {
                        break;
                    }
                    pstmtReservaMesa.setInt(1, reserva_id);
                    pstmtReservaMesa.setInt(2, mesa_id);
                    pstmtReservaMesa.executeUpdate();
                    mesas_insertadas++;
                }
        
                // 3. Insertar en la tabla usuario_reserva
                pstmtUsuarioReserva.setInt(1, usuario_id);
                pstmtUsuarioReserva.setInt(2, reserva_id);
                pstmtUsuarioReserva.executeUpdate();
        
                return reserva_id;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return reserva_id; // Si falló, regresará un 0
    }
    
    public void actualizarEstadoMesas(String fecha, String hora) {
        String sql = "UPDATE mesas m " +
                     "SET estado = CASE " +
                     "    WHEN m.mesa_id IN ( " +
                     "        SELECT rm.mesa_id " +
                     "        FROM reserva r " +
                     "        JOIN reserva_mesa rm ON r.reserva_id = rm.reserva_id " +
                     "        WHERE r.fecha = ?::DATE " +
                     "        AND r.hora = ?::TIME " +
                     "        AND r.restaurante_id = m.restaurante_id " +
                     "    ) THEN 'ocupada' " +
                     "    ELSE 'disponible' " +
                     "END";
    
        try (PreparedStatement statement = conexion.prepareStatement(sql)) {
            
            LocalDate localDate = LocalDate.parse(fecha);
            java.sql.Date sqlDate = java.sql.Date.valueOf(localDate);
            LocalTime localTime = LocalTime.parse(hora);
            java.sql.Time sqlTime = java.sql.Time.valueOf(localTime);
    
            // Asignar parámetros
            statement.setDate(1, sqlDate);
            statement.setTime(2, sqlTime);
    
            // Ejecutar la actualización
            statement.executeUpdate();
    
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }    
    
    public void detalles_reserva(int reserva_id, int restaurante_id) {
        String sql = "SELECT r.reserva_id, r.fecha, r.hora, r.num_personas, ARRAY_AGG(m.num_mesa) AS mesas " +
                     "FROM reserva r " +
                     "JOIN reserva_mesa rm ON r.reserva_id = rm.reserva_id " +
                     "JOIN mesas m ON rm.mesa_id = m.mesa_id " +  // Unir con la tabla mesas
                     "WHERE r.reserva_id = ? " +
                     "GROUP BY r.reserva_id, r.fecha, r.hora, r.num_personas";
        String[] restaurantes = {"Campus Pizza UVG", "Campus Pizza URL", "Campus Pizza UFM", "Campus Pizza UNIS", "Campus Pizza USAC"};
    
        try (PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setInt(1, reserva_id);
    
            ResultSet rs = statement.executeQuery();
    
            if (rs.next()) {
                // Obtener los detalles de la reserva
                int id = rs.getInt("reserva_id");
                Date fecha = rs.getDate("fecha");
                Time hora = rs.getTime("hora");
                int num_personas = rs.getInt("num_personas");
    
                Array mesasArray = rs.getArray("mesas");
                Integer[] mesas = (Integer[]) (Object[]) mesasArray.getArray();
    
                // Mostrar detalles de la reserva
                System.out.println("\tReserva #" + id + " | Sede: " + restaurantes[restaurante_id - 1]);
                System.out.println("\tFecha y hora: " + fecha + " " + hora);
                System.out.println("\tNúmero de personas: " + num_personas + " (total de mesas: " + mesas.length + ")");
                System.out.println("\tSu(s) número(s) de mesa: " + Arrays.toString(mesas).substring(1, Arrays.toString(mesas).length() - 1));
            } else {
                System.out.println("No se encontró una reserva con el ID proporcionado.");
            }
    
        } catch (SQLException e) {
            System.out.println("Error al obtener los detalles de la reserva: " + e.getMessage());
        }
    }    
    
}