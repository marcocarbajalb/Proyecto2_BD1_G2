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
        String sql = "SELECT r.reserva_id, r.fecha, r.hora, r.num_personas, ARRAY_AGG(m.num_mesa ORDER BY m.num_mesa) AS mesas " +
                     "FROM reserva r " +
                     "JOIN reserva_mesa rm ON r.reserva_id = rm.reserva_id " +
                     "JOIN mesas m ON rm.mesa_id = m.mesa_id " + 
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
                System.out.println("\tReserva #" + id + " | Sucursal: " + restaurantes[restaurante_id - 1]);
                System.out.println("\tFecha y hora: " + fecha + " " + hora);
                System.out.println("\tNúmero de personas: " + num_personas + " (total de mesas: " + mesas.length + ")");
                System.out.println("\tSu(s) número(s) de mesa: " + Arrays.toString(mesas).substring(1, Arrays.toString(mesas).length() - 1));
            } 
    
        } catch (SQLException e) {
            System.out.println("Error al obtener los detalles de la reserva: " + e.getMessage());
        }
    }

    public void detalles_reserva_personal(int cliente_id, int reserva_id, int restaurante_id) {
        String sql = "SELECT r.reserva_id, r.fecha, r.hora, r.num_personas, ARRAY_AGG(m.num_mesa) AS mesas " +
                     "FROM reserva r " +
                     "JOIN reserva_mesa rm ON r.reserva_id = rm.reserva_id " +
                     "JOIN mesas m ON rm.mesa_id = m.mesa_id " + 
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
                System.out.println("\tReserva #" + id + " | Sucursal: " + restaurantes[restaurante_id - 1]);
                System.out.println("\tA nombre de: " + obtenerNombreCompleto(cliente_id));
                System.out.println("\tFecha y hora: " + fecha + " " + hora);
                System.out.println("\tNúmero de personas: " + num_personas + " (total de mesas: " + mesas.length + ")");
                System.out.println("\tSu(s) número(s) de mesa: " + Arrays.toString(mesas).substring(1, Arrays.toString(mesas).length() - 1));
            } 
    
        } catch (SQLException e) {
            System.out.println("Error al obtener los detalles de la reserva: " + e.getMessage());
        }
    }

    public void datos_completos_reserva(int reserva_id) {
        String sqlReserva = "SELECT r.reserva_id, r.fecha, r.hora, r.num_personas, r.restaurante_id, " +
                            "ARRAY_AGG(m.num_mesa) AS mesas " +
                            "FROM reserva r " +
                            "JOIN reserva_mesa rm ON r.reserva_id = rm.reserva_id " +
                            "JOIN mesas m ON rm.mesa_id = m.mesa_id " +
                            "WHERE r.reserva_id = ? " +
                            "GROUP BY r.reserva_id, r.fecha, r.hora, r.num_personas, r.restaurante_id";
        
        String sqlPlatos = "SELECT p.nombre_plato, p.precio, COUNT(pd.plato_id) AS cantidad " +
                           "FROM pedidos pd " +
                           "JOIN platos p ON pd.plato_id = p.plato_id " +
                           "WHERE pd.reserva_id = ? " +
                           "GROUP BY p.nombre_plato, p.precio " +
                           "ORDER BY p.nombre_plato";
        
        String[] restaurantes = {"Campus Pizza UVG", "Campus Pizza URL", "Campus Pizza UFM", "Campus Pizza UNIS", "Campus Pizza USAC"};
        
        try (PreparedStatement statementReserva = conexion.prepareStatement(sqlReserva)) {
            statementReserva.setInt(1, reserva_id);
            ResultSet rsReserva = statementReserva.executeQuery();
            
            if (rsReserva.next()) {
                // Obtener detalles de la reserva
                int id = rsReserva.getInt("reserva_id");
                Date fecha = rsReserva.getDate("fecha");
                Time hora = rsReserva.getTime("hora");
                int num_personas = rsReserva.getInt("num_personas");
                int restauranteId = rsReserva.getInt("restaurante_id");
                Array mesasArray = rsReserva.getArray("mesas");
                Integer[] mesas = (Integer[]) (Object[]) mesasArray.getArray();
                
                // Mostrar detalles de la reserva
                System.out.println("\tReserva #" + id + " | Sucursal: " + restaurantes[restauranteId - 1]);
                System.out.println("\tFecha y hora: " + fecha + " " + hora);
                System.out.println("\tNúmero de personas: " + num_personas + " (total de mesas: " + mesas.length + ")");
                System.out.println("\tSu(s) número(s) de mesa: " + Arrays.toString(mesas).substring(1, Arrays.toString(mesas).length() - 1));
                System.out.println("\n\tPlatos pedidos:");
                
                // Segunda consulta para obtener los platos pedidos
                try (PreparedStatement statementPlatos = conexion.prepareStatement(sqlPlatos)) {
                    statementPlatos.setInt(1, reserva_id);
                    ResultSet rsPlatos = statementPlatos.executeQuery();
                    
                    double totalCuenta = 0.0;
                    
                    while (rsPlatos.next()) {
                        String nombrePlato = rsPlatos.getString("nombre_plato");
                        double precioPlato = rsPlatos.getDouble("precio");
                        int cantidad = rsPlatos.getInt("cantidad");
                        
                        System.out.println("\t\tx" + cantidad + " - " + nombrePlato);
                        totalCuenta += cantidad * precioPlato;
                    }
                    
                    // Mostrar el total de la cuenta
                    System.out.println("\tTotal de la cuenta: Q" + totalCuenta);
                }
            } else {
                System.out.println("No se encontraron detalles para la reserva con ID: " + reserva_id);
            }
            
        } catch (SQLException e) {
            System.out.println("Error al obtener los detalles de la reserva: " + e.getMessage());
        }
    }    

    public void agregarPlatoPedido(int reserva_id, int plato_id) {
        String sql = "INSERT INTO pedidos (reserva_id, plato_id) VALUES (?, ?)";
    
        try (PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setInt(1, reserva_id);
            statement.setInt(2, plato_id);
    
            statement.executeUpdate();
    
        } catch (SQLException e) {
            System.out.println("Error al agregar el plato al pedido: " + e.getMessage());
        }

    }

    public void registrarHistorialCliente(int reserva_id, int plato_favorito_id) {
        String sql = "INSERT INTO historial_cliente (reserva_id, plato_favorito, observaciones) VALUES (?, ?, '')";
    
        try (PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setInt(1, reserva_id);
            statement.setInt(2, plato_favorito_id);
    
            statement.executeUpdate();
    
        } catch (SQLException e) {
            System.out.println("Error al registrar el historial de visita: " + e.getMessage());
        }
    }

    public List<Integer> obtenerReservasCliente(int cliente_id){
        List<Integer> reservas = new ArrayList<>();
        String sql = "SELECT r.reserva_id " +
                     "FROM reserva r " +
                     "JOIN usuario_reserva ur ON r.reserva_id = ur.reserva_id " +
                     "WHERE ur.usuario_id = ?";
    
        try (PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setInt(1, cliente_id);
    
            ResultSet rs = statement.executeQuery();
    
            while (rs.next()) {
                reservas.add(rs.getInt("reserva_id"));
            }
    
        } catch (SQLException e) {
            System.out.println("Error al obtener las reservas del cliente: " + e.getMessage());
        }
    
        return reservas;
    }

    public List<Integer> obtenerReservasClienteRestaurante(int cliente_id, int restaurante_id){
        List<Integer> reservas = new ArrayList<>();
        String sql = "SELECT r.reserva_id " +
                     "FROM reserva r " +
                     "JOIN usuario_reserva ur ON r.reserva_id = ur.reserva_id " +
                     "WHERE ur.usuario_id = ? and r.restaurante_id = ?";
    
        try (PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setInt(1, cliente_id);
            statement.setInt(2, restaurante_id);
    
            ResultSet rs = statement.executeQuery();
    
            while (rs.next()) {
                reservas.add(rs.getInt("reserva_id"));
            }
    
        } catch (SQLException e) {
            System.out.println("Error al obtener las reservas del cliente: " + e.getMessage());
        }
    
        return reservas;
    }

    public void agregarObservacionesReserva(int reserva_id, String observaciones){
        String sql = "UPDATE historial_cliente " +
                     "SET observaciones = ? " +
                     "WHERE reserva_id = ?";
    
        try (PreparedStatement statement = conexion.prepareStatement(sql)) {
            
            statement.setString(1, observaciones);
            statement.setInt(2, reserva_id);
    
            statement.executeUpdate();
    
        } catch (SQLException e) {
            System.out.println("Error al agregar observaciones a la reserva: " + e.getMessage());
        }
    }

    public void datos_completos_historial(int reserva_id){
        String sql = "SELECT * " +
                     "FROM historial_cliente " +
                     "WHERE reserva_id = ?";

        String[] platos = {"Pizza hawaiana","Pizza de pepperoni","Pizza de queso","Pizza de vegetales","Pizza margarita","Pizza de jamón","Agua pura","Coca-cola","Coca-cola zero"};
        
        try (PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setInt(1, reserva_id);
    
            ResultSet rs = statement.executeQuery();
    
            if (rs.next()) {
                // Obtener detalles del historial
                int historia_id = rs.getInt("historial_id");
                int id = rs.getInt("reserva_id");
                int plato_favorito = rs.getInt("plato_favorito");
                String observaciones = rs.getString("observaciones");
    
                // Mostrar detalles del historial
                System.out.println("\tHistorial (ID " + historia_id + ") de la reserva #" + id);
                System.out.println("\tPlato favorito: " + platos[plato_favorito-1]);
                System.out.println("\tObservaciones: " + observaciones);
            } 
    
        } catch (SQLException e) {
            System.out.println("Error al obtener los detalles del historial: " + e.getMessage());
        }
    }

    public int obtenerIDUsuario(String username) {
        String sql = "SELECT usuario_id FROM usuarios WHERE username = ?";
        int usuario_id = 0;
    
        try (PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setString(1, username);
    
            ResultSet rs = statement.executeQuery();
    
            if (rs.next()) {
                usuario_id = rs.getInt("usuario_id");
            }
    
        } catch (SQLException e) {
            System.out.println("Error al obtener el ID del usuario: " + e.getMessage());
        }
    
        return usuario_id;
    }

    public String obtenerNombreCompleto (int usuario_id){
        String sql = "SELECT nombres, apellidos FROM usuarios WHERE usuario_id = ?";
        String nombre_completo = "";
    
        try (PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setInt(1, usuario_id);
    
            ResultSet rs = statement.executeQuery();
    
            if (rs.next()) {
                String nombres = rs.getString("nombres");
                String apellidos = rs.getString("apellidos");
                nombre_completo = nombres + " " + apellidos;
            }
    
        } catch (SQLException e) {
            System.out.println("Error al obtener el nombre completo del usuario: " + e.getMessage());
        }
    
        return nombre_completo;
    }

    public List<Integer> listaMesasDisponibles(int restaurante_id){
        List<Integer> mesas = new ArrayList<>();
        String sql = "SELECT num_mesa " +
                     "FROM mesas " +
                     "WHERE restaurante_id = ? " +
                     "AND estado = 'disponible'";
    
        try (PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setInt(1, restaurante_id);
    
            ResultSet rs = statement.executeQuery();
    
            while (rs.next()) {
                mesas.add(rs.getInt("num_mesa"));
            }
    
        } catch (SQLException e) {
            System.out.println("Error al obtener las mesas disponibles: " + e.getMessage());
        }
    
        return mesas;
    }
    
    public List<Integer> listaClientes(int restaurante_id) {
        String sql = "SELECT distinct u.usuario_id " + 
                     "FROM usuarios u join usuario_reserva ur ON u.usuario_id = ur.usuario_id " +
                     "JOIN reserva r ON ur.reserva_id = r.reserva_id " +
                     "WHERE restaurante_id = ?";
        List<Integer> ids_clientes = new ArrayList<>();
        try (PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setInt(1, restaurante_id);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                ids_clientes.add(rs.getInt("usuario_id"));
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener los clientes: " + e.getMessage());
        }
        return ids_clientes;
    }

    public List<String> obtener_meseros (int restaurante_id) {
        String sql = "SELECT u.nombres, u.apellidos, u.usuario_id " +
                     "FROM usuarios u " +
                     "JOIN personal p ON u.usuario_id = p.usuario_id " +
                     "WHERE p.restaurante_id = ? " +
                     "AND u.rol_id = 2";
        List<String> meseros = new ArrayList<>();
        try (PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setInt(1, restaurante_id);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                meseros.add(rs.getString("nombres") + " " + rs.getString("apellidos") + " (ID: " + rs.getInt("usuario_id") + ")");
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener los meseros: " + e.getMessage());
        }
        return meseros;
    }

    public List<String> obtener_gerentes (int restaurante_id) { 
        String sql = "SELECT u.nombres, u.apellidos, u.usuario_id " +
                     "FROM usuarios u " +
                     "JOIN personal p ON u.usuario_id = p.usuario_id " +
                     "WHERE p.restaurante_id = ? " +
                     "AND u.rol_id = 3";
        List<String> gerentes = new ArrayList<>();
        try (PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setInt(1, restaurante_id);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                gerentes.add(rs.getString("nombres") + " " + rs.getString("apellidos") + " (ID: " + rs.getInt("usuario_id") + ")");
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener los gerentes: " + e.getMessage());
        }
        return gerentes;
    }

    public int total_cambios_bitacora() {
        String sql = "SELECT COUNT(*) FROM bitacora_cambios";
        int total_cambios = 0;
        try (PreparedStatement statement = conexion.prepareStatement(sql)) {
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                total_cambios = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener el total de cambios en la auditoría: " + e.getMessage());
        }
        return total_cambios;
    }

    public void desplegarAuditoriaCambios(int limit) {
        String sql = "SELECT * FROM bitacora_cambios ORDER BY fecha_modificacion DESC LIMIT ?";
        try (PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setInt(1, limit);
    
            ResultSet rs = statement.executeQuery();
            
            // Encabezados de columna
            System.out.printf("\n%-7s %-14s %-20s %-24s %-30s\n", "ID", "Acción", "Tabla modificada", "Llave primaria", "Fecha de modificación");
            System.out.println("-----------------------------------------------------------------------------------------------");
            
            while (rs.next()) {
                // Imprimir cada fila con el formato especificado
                System.out.printf("%-7d %-14s %-20s %-24s %-30s\n",
                    rs.getInt("bitacora_id"),
                    rs.getString("accion"),
                    rs.getString("tabla_modificada"),
                    rs.getString("llave_primaria_modificacion"),
                    rs.getTimestamp("fecha_modificacion"));
            }
        } catch (SQLException e) {
            System.out.println("Error al desplegar la auditoría de cambios: " + e.getMessage());
        }
    }

    /*
     * 
     * 
     * 
     * MÉTODOS PARA OBTENER LOS QUERIES DEL REPORTE FINAL
     * 
     * 
     * 
     */

     //2. Top 10 clientes más Frecuentes
     //2. Top 10 clientes más Frecuentes
     //2. Top 10 clientes más Frecuentes
     public List<Object[]> obtenerTop10ClientesFrecuentes() {
        List<Object[]> topClientes = new ArrayList<>();
        String sql = "SELECT u.usuario_id as id, u.nombres, u.apellidos, COUNT(ur.reserva_id) AS frecuencia " +
                    "FROM usuario_reserva ur " +
                    "JOIN usuarios u ON ur.usuario_id = u.usuario_id " +
                    "GROUP BY u.usuario_id, u.nombres, u.apellidos " +
                    "ORDER BY frecuencia DESC " +
                    "LIMIT 10;";

    
        try (PreparedStatement statement = conexion.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Object[] cliente = new Object[4]; //con 4 posiciones de index
                cliente[0] = resultSet.getInt("id");
                cliente[1] = resultSet.getString("nombres");
                cliente[2] = resultSet.getString("apellidos");
                cliente[3] = resultSet.getInt("frecuencia");
                topClientes.add(cliente);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return topClientes;
    }
/* metodos para poder obtener los datos de inventario. */
public List<List<Object>> obtenerInventarioPorRestaurante(int restaurante_id) {
    String sql = "SELECT i.nombre, inv.cantidad, inv.fecha_caducidad " +
                 "FROM inventario inv " +
                 "JOIN insumos i ON inv.insumo_id = i.insumo_id " +
                 "WHERE inv.restaurante_id = ?";
    
    List<List<Object>> inventario = new ArrayList<>();
    
    try (PreparedStatement statement = conexion.prepareStatement(sql)) {
        statement.setInt(1, restaurante_id);
        ResultSet rs = statement.executeQuery();
        while (rs.next()) {
            List<Object> item = new ArrayList<>();
            item.add(rs.getString("nombre"));
            item.add(rs.getInt("cantidad"));
            item.add(rs.getDate("fecha_caducidad"));
            inventario.add(item);
        }
    } catch (SQLException e) {
        System.out.println("Error al obtener el inventario: " + e.getMessage());
    }
    return inventario;
}

// consulta para las alertas del 15% de insumos... 
public List<List<Object>> obtenerInsumosBajoPorcentaje(int porcentaje) {
    String sql = "SELECT i.nombre, inv.cantidad " +
                 "FROM inventario inv " +
                 "JOIN insumos i ON inv.insumo_id = i.insumo_id " +
                 "WHERE inv.cantidad < (? / 100.0) * 100";
    
    List<List<Object>> insumosBajos = new ArrayList<>();
    
    try (PreparedStatement statement = conexion.prepareStatement(sql)) {
        statement.setInt(1, porcentaje);
        ResultSet rs = statement.executeQuery();
        
        while (rs.next()) {
            List<Object> insumo = new ArrayList<>();
            insumo.add(rs.getString("nombre")); // Nombre del insumo
            insumo.add(rs.getInt("cantidad"));  // Cantidad restante
            insumosBajos.add(insumo);
        }
        
    } catch (SQLException e) {
        System.out.println("Error al obtener insumos bajos: " + e.getMessage());
    }
    
    return insumosBajos;
}

public void actualizarCantidadInsumo(int restauranteId, String nombreInsumo, int nuevaCantidad) {
    String insumoIdQuery = "SELECT insumo_id FROM insumos WHERE nombre = ?";
    String actualizarCantidadQuery = "UPDATE inventario " +
                                      "SET cantidad = ? " +
                                      "WHERE restaurante_id = ? " +
                                      "AND insumo_id = ?";

    try (PreparedStatement insumoStatement = conexion.prepareStatement(insumoIdQuery)) {
        // Obtener insumo_id
        insumoStatement.setString(1, nombreInsumo);
        ResultSet resultSet = insumoStatement.executeQuery();

        if (resultSet.next()) {
            long insumoId = resultSet.getLong("insumo_id");

            try (PreparedStatement updateStatement = conexion.prepareStatement(actualizarCantidadQuery)) {
                // Asignar parámetros
                updateStatement.setInt(1, nuevaCantidad);
                updateStatement.setInt(2, restauranteId);
                updateStatement.setLong(3, insumoId);

                // Ejecutar la actualización
                int filasActualizadas = updateStatement.executeUpdate();

                if (filasActualizadas > 0) {
                    System.out.println("Cantidad de insumo actualizada correctamente.");
                } else {
                    System.out.println("No se encontró el insumo en el inventario para el restaurante.");
                }
            }
        } else {
            System.out.println("No se encontró el insumo con ese nombre.");
        }
    } catch (SQLException e) {
        System.out.println("Error al actualizar la cantidad de insumo: " + e.getMessage());
    }
}

    // Método para obtener Top 5 de Clientes con Mayores Reservas y su Preferencia de Platos
    public List<Object[]> obtenerTop5ClientesConPreferencias() {
        List<Object[]> topClientesPlatos = new ArrayList<>();
        String sql = "WITH RankedDishes AS ( " +
                "  SELECT u.usuario_id, p.nombre_plato, COUNT(*) AS plato_count, " +
                "         DENSE_RANK() OVER (PARTITION BY u.usuario_id ORDER BY COUNT(*) DESC) AS rnk " +
                "  FROM usuario_reserva ur " +
                "  JOIN usuarios u ON ur.usuario_id = u.usuario_id " +
                "  JOIN pedidos pd ON ur.reserva_id = pd.reserva_id " +
                "  JOIN platos p ON pd.plato_id = p.plato_id " +
                "  GROUP BY u.usuario_id, p.nombre_plato " +
                ") " +
                "SELECT u.usuario_id, u.nombres, u.apellidos, STRING_AGG(rd.nombre_plato, ', ') AS platos_favoritos " +
                "FROM usuarios u " +
                "JOIN RankedDishes rd ON u.usuario_id = rd.usuario_id " +
                "WHERE rd.rnk = 1 " +
                "GROUP BY u.usuario_id, u.username " +
                "ORDER BY (SELECT COUNT(*) FROM usuario_reserva ur2 WHERE ur2.usuario_id = u.usuario_id) DESC " +
                "LIMIT 5;";
    
        try (PreparedStatement statement = conexion.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Object[] cliente = new Object[4]; // Con 4 index, yeah
                cliente[0] = resultSet.getInt("usuario_id");
                cliente[1] = resultSet.getString("nombres");
                cliente[2] = resultSet.getString("apellidos");
                 // Formateamos la cadena de platos favoritos
                String platosFavoritos = resultSet.getString("platos_favoritos");
                String salidaPlatos = String.format("Platos favoritos: %s", platosFavoritos);

                cliente[3] = salidaPlatos;
                topClientesPlatos.add(cliente);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return topClientesPlatos;
    }
    
}

    
    
