/* BASE DE DATOS 1 - Sección 10
 * Marco Carbajal, José Pablo Donado, José Roberto Rodríguez y Oscar Escribá
 * [Proyecto 2 - Desarrollo y consulta de BDs operativas]
 * Clase para los usuarios de tipo gerente
 */
 
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Date;
import java.util.Collections;

public class Gerente extends ITipoUsuario {
    
    private int usuario_id;
    private String nombres;
    private String apellidos;
    private String username;
    private String password;
    public final String rol = "gerente";
    private int restaurante_id;
    // variables locales para poder trabajar con lo del inventario...
    private List<String> nombresInsumos = new ArrayList<>();
    private List<Integer> cantidades = new ArrayList<>();
    private List<Date> fechasCaducidad = new ArrayList<>();

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

    public void setRestaurante_id(int restaurante_id) {
        this.restaurante_id = restaurante_id;
    }

    public int getRestaurante_id() {
        return restaurante_id;
    }

    public String getSucursal_restaurante() {
        String [] sucursales_restaurante = {"Campus Pizza UVG","Campus Pizza URL","Campus Pizza UFM","Campus Pizza UNIS", "Campus Pizza USAC"};
        return sucursales_restaurante[this.restaurante_id-1];
    }
    
    @Override
    public void mostrarMenu(ITipoUsuario usuario_activo, GestionBD gestionBD, TimeSimulator simulator, Scanner scanString, Scanner scanInt) {
        boolean menu_secundario = true;
		    while(menu_secundario) {
		        System.out.println("\n░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░");
                System.out.println("\n\t       ╔══════════════════════════════════════════╗\n\t       ║ Fecha y hora actual: " + simulator.getFechaFormateada() + " ║\n\t       ╚══════════════════════════════════════════╝");
                                
                System.out.println("\n[GERENTE | " +  ((Gerente) usuario_activo).getSucursal_restaurante() + "]\nBienvenido/a, "+ usuario_activo.getNombres() + " " + usuario_activo.getApellidos());
		        System.out.println("\nIngrese el número correspondiente a la opción que desea realizar:\n1. Hacer reserva\n2. Ver disponibilidad de mesas\n3. Consultar reservas y pedidos\n4. Gestionar inventario\n5. Ver historial de clientes\n6. Ver meseros\n7. Cerrar sesión");

				int decision_secundaria = 0;
				try {decision_secundaria = scanInt.nextInt();}

				catch(Exception e) {//En caso de que el usuario ingrese texto en lugar de un número 
					System.out.println("\n**ERROR** La decision ingresada debe ser un número.");
					scanInt.nextLine();
					continue;}
				
				switch(decision_secundaria) {
					case 1:{//Hacer reserva
                        System.out.println("\n╠═════════════════════════════HACER RESERVA═════════════════════════════╣");
                        boolean validar_username = true;
                        while(validar_username) {
                            System.out.println("\nIngrese el username del cliente para el que desea hacer la reserva (o 'SALIR' para regresar al menú principal) : ");
                            String username_cliente = scanString.nextLine().trim();
                            if(username_cliente.equals("SALIR")) {
                                validar_username = false;}
                            else if(gestionBD.existeUsername(username_cliente)) {
                                int cliente_id = gestionBD.obtenerIDUsuario(username_cliente);
                                hacer_reserva(cliente_id, gestionBD, simulator, scanString, scanInt);
                                validar_username = false;}
                            else {
                                System.out.println("\n**ERROR** El username ingresado no corresponde a un cliente registrado en el sistema.");}}
                                
                        
                        break;}
                    
                    case 2:{//Ver disponibilidad de mesas
						System.out.println("\n╠══════════════════════VER DISPONIBILIDAD DE MESAS══════════════════════╣");
                        mostrarDistribucionMesas(restaurante_id, gestionBD, simulator);
						break;}

                    case 3:{//Consultar reservas y pedidos
                        System.out.println("\n╠═══════════════════════CONSULTAR RESERVAS Y PEDIDOS══════════════════════╣");
                        consultar_reservas_personal(restaurante_id, gestionBD);
                        break;}
                    
                    case 4:{//Gestionar inventario
                        System.out.println("\n╠══════════════════════════GESTIONAR INVENTARIO═════════════════════════╣");
                        
                        boolean validar_menu_inventario = true;
                        while(validar_menu_inventario) {
                        
                            System.out.println("\nIngrese el número correspondiente a la opción que desea realizar:\n1. Mostrar inventario\n2. Cambiar cantidad de insumos\n3. Observar inventario en orden de cantidad\n4. Observar inventario en orden de caducidad\n5. Regresar al menú principal");
                                
                            int opcion = 0;
                            try {opcion = scanInt.nextInt();}
                            catch(Exception e) {//En caso de que el usuario ingrese texto en lugar de un número 
                                System.out.println("\n**ERROR** La decision ingresada debe ser un número.");
                                scanInt.nextLine();
                                    continue;}    
                            
                            switch (opcion) {
                                case 1:
                                    ver_inventario(restaurante_id, gestionBD);
                                    break;
                                case 2:
                                    System.out.println("Ingrese el nombre del insumo:");
                                    String nombreInsumo = scanString.nextLine().trim();
                                    System.out.println("Ingrese la nueva cantidad:");
                                    int nuevaCantidad = scanInt.nextInt();
                                    cambiarCantidadInsumo(restaurante_id, nombreInsumo, nuevaCantidad, gestionBD);
                                    break;
                                case 3:
                                    ordenarPorMayorCantidad();
                                    ver_inventario(restaurante_id, gestionBD); // Mostrar el inventario reordenado
                                    break;
                                case 4:
                                    ordenarPorFechaCaducidad();
                                    ver_inventario(restaurante_id, gestionBD); // Mostrar el inventario reordenado
                                    break;
                                case 5:
                                    validar_menu_inventario = false;
                                    break;
                                default:
                                    System.out.println("\n**ERROR**\nEl número ingresado no se encuentra entre las opciones disponibles.");
                                    break;}}
                        break;}

                    case 5:{//Ver historial de clientes
                        System.out.println("\n╠═══════════════════════VER HISTORIAL DE CLIENTES═══════════════════════╣");
                        ver_historial_clientes(gestionBD);
                        break;}

                    case 6:{//Ver meseros
                        System.out.println("\n╠══════════════════════════════VER MESEROS══════════════════════════════╣\n");
                        List<String> meseros = gestionBD.obtener_meseros(restaurante_id);
                        if(meseros.size()>0){
                            int num_mesero = 1;
                            for(String info_mesero : meseros) {
                                System.out.println((num_mesero) + ". " + info_mesero);
                                num_mesero++;}}
                        else {
                            System.out.println("\nNo hay meseros registrados en el sistema para la sucursal " + getSucursal_restaurante() + ".");}
                        break;}

                    case 7:{//Cerrar sesión
                        System.out.println("\t\t     ┌─────────────────────────────┐\n\t\t     " + "│ Sesión cerrada exitosamente │\n\t\t     └─────────────────────────────┘");
                        menu_secundario = false;
                        break;}
					
					default:{//Opción no disponible (programación defensiva)
						System.out.println("\n**ERROR**\nEl número ingresado no se encuentra entre las opciones disponibles.");}}}
    }

    public void hacer_reserva(int usuario_id, GestionBD gestionBD, TimeSimulator simulator, Scanner scanString, Scanner scanInt){

        System.out.println("\n\nREGISTRO DE DATOS DE LA RESERVA\nPara el cliente " + gestionBD.obtenerNombreCompleto(usuario_id) + " en " + getSucursal_restaurante() + ".");
        
        //Atributos necesarios para hacer una reserva
        Integer cliente_id, restaurante_id, num_personas=0, dia=0, mes=0, year=0, hora=0;
        String fecha_reserva = "", hora_reserva="";
        
        //[Registro de datos para hacer la reserva]
        cliente_id = usuario_id;

        restaurante_id = this.restaurante_id;

        //Solicitar al usuario el número de personas para la reserva
        boolean seleccion_num_personas = true;
        while(seleccion_num_personas) {
            System.out.println("\nIngrese el número de personas para la reserva: ");
            try {
                num_personas = scanInt.nextInt();} 
            catch(Exception e) {
                System.out.println("\n**ERROR** La decision ingresada debe ser un número.");
                scanInt.nextLine();
                continue;}
            
            if(num_personas>0) {
                seleccion_num_personas = false;} 
            
            else {
                System.out.println("\n**ERROR** El número de personas debe ser mayor a 0.");}}
        
        //Solicitar al usuario la fecha de la reserva
        boolean seleccion_fecha = true;
        while(seleccion_fecha) {
            System.out.println("\nIngrese la fecha de la reserva (utilice el formato 'DD/MM/AAAA'): ");
            try {
                fecha_reserva = scanString.nextLine().trim();
                String[] fecha_reserva_separada = fecha_reserva.split("/");
                dia = Integer.parseInt(fecha_reserva_separada[0]);
                mes = Integer.parseInt(fecha_reserva_separada[1]);
                year = Integer.parseInt(fecha_reserva_separada[2]);} 
            catch(Exception e) {
                System.out.println("\n**ERROR** Ingrese la fecha en el formato solicitado.");
                scanString.nextLine();
                continue;}
            
            if((mes==2)&&(dia==29)&&(((year%4==0)&&(year%100!=0))||(year%400==0))) { //Único caso para años bisiestos
                seleccion_fecha = false;}
            
            else if((mes==2)&&(dia>28)) {
                if(((year%4==0)&&(year%100!=0))||(year%400==0)){
                    System.out.println("\n**ERROR** El mes de febrero tiene 29 días para el año ingresado.");
                    continue;}
                else{
                    System.out.println("\n**ERROR** El mes de febrero solo tiene 28 días para el año ingresado.");
                    continue;}}
            
            else if((mes==4||mes==6||mes==9||mes==11)&&(dia>30)) {
                System.out.println("\n**ERROR** El mes ingresado solo tiene 30 días.");
                continue;}
            
            else if((dia>0)&&(dia<=31)&&(mes>0)&&(mes<=12)&&(year>=2024)) {
                seleccion_fecha = false;} 
            
            else {
                System.out.println("\n**ERROR** La fecha ingresada no es válida.");}}

        //Solicitar al usuario la hora de la reserva
        boolean seleccion_hora = true;
        while(seleccion_hora) {
            System.out.println("\n[HORARIO RESERVAS: 07:00-20:00 todos los días]\nIngrese la hora puntual de la reserva (utilice el formato 'HH'): ");
            try {
                hora = scanInt.nextInt();} 
            catch(Exception e) {
                System.out.println("\n**ERROR** Ingrese la hora en el formato solicitado.");
                scanInt.nextLine();
                continue;}
            
            if((hora>=7)&&(hora<=20)) {
                seleccion_hora = false;} 
            
            else {
                System.out.println("\n**ERROR** La hora ingresada no es válida o no se encuentra dentro del horario disponible.");}}

        String mes_formateado = String.format("%02d", mes);
        String dia_formateado = String.format("%02d", dia);
        String hora_formateada = String.format("%02d", hora);
            
        fecha_reserva = year + "-" + mes_formateado + "-" + dia_formateado;
        hora_reserva = hora_formateada + ":00:00";

        int cantidad_mesas = Math.ceilDiv(num_personas, 4);

        //Verificar que la fecha y hora de la reserva no estén en el pasado
        if(simulator.fechaPasada(fecha_reserva + " " + hora_reserva)) {
            System.out.println("\n**ERROR** \nLa fecha y hora de la reserva no pueden ser anteriores a la fecha y hora actual (" + simulator.getFechaFormateada() + ").");
            return;}
        //Si hay mesas disponibles, hacer la reserva
        else if (gestionBD.verificarMesasDisponibles(restaurante_id, cantidad_mesas, fecha_reserva, hora_reserva)==false) {
            System.out.println("\nESPACIO NO DISPONIBLE. \nNo hay/habrá suficientes mesas disponibles en el restaurante para hacer la reserva en la fecha y hora especificadas.");
            return;}
        else {
            int reserva_id = gestionBD.realizarReserva(cliente_id, restaurante_id, num_personas, cantidad_mesas, fecha_reserva, hora_reserva);
            if(reserva_id>0) {
                System.out.println("\nRESERVA REALIZADA CON ÉXITO.\nA continuación se presentan los detalles de la reserva:\n");
                gestionBD.detalles_reserva_personal(cliente_id, reserva_id, restaurante_id);
                int plato_favorito_id = establecer_pedido_personal(cliente_id, reserva_id, gestionBD, scanInt);
                gestionBD.registrarHistorialCliente(reserva_id, plato_favorito_id);}}
    }

    public int establecer_pedido_personal(int cliente_id, int reserva_id, GestionBD gestionBD, Scanner scanInt) {
        
        List<String> platos = new ArrayList<>(Arrays.asList("Pizza hawaiana","Pizza de pepperoni","Pizza de queso","Pizza de vegetales","Pizza margarita","Pizza de jamón","Agua pura","Coca-cola","Coca-cola zero"));
        Double[] precios = {70.00,65.00,65.00,70.00,70.00,65.00,15.00,10.00,10.00};
        ArrayList<Integer> platos_pedido = new ArrayList<Integer>();

        boolean terminar_pedido = false;
        while(terminar_pedido==false){
            System.out.println("\n╔════════════════════════════════════════════════╗\n║                MENU CAMPUS PIZZA               ║\n╠════════════════════════════════════════════════╣\n║ 1. Pizza hawaiana...................... Q70.00 ║\n║ 2. Pizza de pepperoni.................. Q65.00 ║\n║ 3. Pizza de queso...................... Q65.00 ║\n║ 4. Pizza de vegetales.................. Q70.00 ║\n║ 5. Pizza margarita..................... Q70.00 ║\n║ 6. Pizza de jamón...................... Q65.00 ║\n╠════════════════════════════════════════════════╣\n║ 7. Agua pura.......................... Q15.00  ║\n║ 8. Coca-cola.......................... Q10.00  ║\n║ 9. Coca-cola zero..................... Q10.00  ║\n╚════════════════════════════════════════════════╝");
            System.out.println("\nIngrese el número correspondiente al plato que desea agregar al pedido de la reserva (o '0' para dar por finalizado el pedido): ");

            int decision_pedido = -1;
            try {decision_pedido = scanInt.nextInt();}

            catch(Exception e) {//En caso de que el usuario ingrese texto en lugar de un número 
                System.out.println("\n**ERROR** La decision ingresada debe ser un número.");
                scanInt.nextLine();
                continue;}
            
            if((decision_pedido>=0)&&(decision_pedido<=9)) {
                
                if(decision_pedido==0 && platos_pedido.size()<=0) {
                    System.out.println("\n**ERROR** Debe agregar al menos un plato al pedido.");}
                
                else if(decision_pedido==0 && platos_pedido.size()>0) {
                    terminar_pedido = true;}

                else {
                    System.out.println("\n¿Cuántas unidades de '" + platos.get(decision_pedido-1) + "' desea agregar al pedido? ");
                    int cantidad_pedido = 0;
                    try {cantidad_pedido = scanInt.nextInt();}

                    catch(Exception e) {//En caso de que el usuario ingrese texto en lugar de un número 
                        System.out.println("\n**ERROR** La cantidad ingresada debe ser un número.");
                        scanInt.nextLine();
                        continue;}
                    
                    if(cantidad_pedido>0) {
                        for(int i=0;i<cantidad_pedido;i++) {
                            platos_pedido.add(decision_pedido-1);
                            gestionBD.agregarPlatoPedido(reserva_id, decision_pedido);
                            }}
                    else {
                        System.out.println("\n**ERROR** La cantidad ingresada debe ser mayor a 0.");}}}
            else {
                System.out.println("\n**ERROR** El número ingresado no se encuentra entre las opciones disponibles.");}}

        Map<String, Integer> contadorPlatos = new LinkedHashMap<>();

        // Recorrer la lista de platos pedidos
        for (Integer indice : platos_pedido) {
            String plato = platos.get(indice); 
            contadorPlatos.put(plato, contadorPlatos.getOrDefault(plato, 0) + 1);
        }

        // Extraer los platos únicos y las cantidades a dos listas
        List<String> platos_unicos = new ArrayList<>(contadorPlatos.keySet());
        List<Integer> cantidades = new ArrayList<>(contadorPlatos.values());
        List<Double> precios_unitarios = new ArrayList<>();
        List<Double> precios_totales = new ArrayList<>();
        double total_general = 0.0;

        for (String plato : platos_unicos) {
            int indice = platos.indexOf(plato);
            double precio_unitario = precios[indice];
            precios_unitarios.add(precio_unitario);
            int cantidad = contadorPlatos.get(plato);
            double precio_total = precio_unitario * cantidad;
            precios_totales.add(precio_total);
            total_general += precio_total;
        }

        System.out.println("\nPEDIDO REGISTRADO EXITOSAMENTE.");
        System.out.println("A continuación se presentan los detalles del pedido:\n");

        System.out.printf("%-21s %-10s %-10s %-10s\n", "Plato", "Cantidad", "Precio", "Total");
        System.out.println("-----------------------------------------------------");
        for (int p = 0; p < platos_unicos.size(); p++) {
            System.out.printf("%-21s %-10d Q%-9.2f Q%-9.2f\n", platos_unicos.get(p), cantidades.get(p), precios_unitarios.get(p), precios_totales.get(p));}
        System.out.println("-----------------------------------------------------");
        System.out.printf("Total de la cuenta: Q%-9.2f", total_general);
        System.out.println(("\nA nombre de: " + gestionBD.obtenerNombreCompleto(cliente_id) + "\n"));

        return (platos.indexOf(determinarPlatoFavorito(platos_unicos, cantidades)) + 1);
    }

    public static String determinarPlatoFavorito(List<String> platos_unicos, List<Integer> cantidades) {
        String favorito = null;
        int maxCantidad = 0;

        for (int i=0; i < platos_unicos.size(); i++) {
            if (cantidades.get(i) > maxCantidad) {
                maxCantidad = cantidades.get(i);
                favorito = platos_unicos.get(i);
            }
        }

        return favorito;
    }

    public static void mostrarDistribucionMesas(int restaurante_id, GestionBD gestionBD, TimeSimulator simulator) {
        String[] restaurantes = {"Campus Pizza UVG","Campus Pizza URL","Campus Pizza UFM","Campus Pizza UNIS", "Campus Pizza USAC"};
        System.out.println("\nSucursal: " + restaurantes[restaurante_id-1] + "\nFecha y hora actual: " + simulator.getFechaFormateada() + "\n");
        System.out.println("\t       Mesa disponible: O | Mesa ocupada: X\n");
        List<Integer> mesas_disponibles = gestionBD.listaMesasDisponibles(restaurante_id);
        Integer[] mesas = new Integer[30];
        String[] estados_disponibilidad = new String[30];

        for(int i=0; i<30; i++) {
                mesas[i] = i+1;
                if(mesas_disponibles.contains(i+1)) {
                        estados_disponibilidad[i] = "O";}
                else {
                        estados_disponibilidad[i] = "X";}}

        construirMatriz(6, 5, mesas, estados_disponibilidad);
    }

    public static void construirMatriz(int m, int n, Integer[] numeros, String[] letras) {
        int indexNumero = 0;
        int indexLetra = 0;
    
        for (int row = 0; row < n; row++) {
            StringBuilder lineaSuperior = new StringBuilder("\t");
            StringBuilder lineaMedia = new StringBuilder("\t");
            StringBuilder lineaInferior = new StringBuilder("\t");
    
            for (int col = 0; col < m; col++) {
                // Cada elemento de la matriz será una mesa (que tendrá el número de mesa y la letra de disponible u ocupada)
                lineaSuperior.append("┌────┐   "); 
                String numeroFormateado = String.format("%02d", indexNumero < numeros.length ? numeros[indexNumero] : 0);
                lineaMedia.append("│ ").append(numeroFormateado).append(" │");
    
                if (indexLetra < letras.length) {
                    lineaMedia.append(letras[indexLetra]).append("  ");
                } else {
                    lineaMedia.append("   ");}
    
                lineaInferior.append("└────┘   ");
    
                indexNumero++;
                indexLetra++;}
    
            System.out.println(lineaSuperior);
            System.out.println(lineaMedia);
            System.out.println(lineaInferior);
        }
    }

    public void consultar_reservas_personal(int restaurante_id, GestionBD gestionBD) {
        
        List<Integer> clientes = gestionBD.listaClientes(restaurante_id);
        if(clientes.size()>0){
            int num_cliente = 1;
            int num_reserva_pedido = 1;
            for(int cliente_id : clientes) {
                System.out.println("\n[CLIENTE #" + num_cliente + ": " + gestionBD.obtenerNombreCompleto(cliente_id)+ " (ID " + cliente_id + ")]");
                List<Integer> reservas_ids = gestionBD.obtenerReservasClienteRestaurante(cliente_id, restaurante_id);
                for(int i=0;i<reservas_ids.size();i++) {
                    System.out.println("\n(" + num_reserva_pedido + ")");
                    gestionBD.datos_completos_reserva(reservas_ids.get(i));
                    num_reserva_pedido++;}
                num_cliente++;}}
        else {
            System.out.println("\nNo hay reservas ni pedidos registrados en el sistema para la sucursal " + getSucursal_restaurante() + ".");}
    }

    public void ver_historial_clientes(GestionBD gestionBD) {
        List<Integer> clientes = gestionBD.listaClientes(restaurante_id);
        if(clientes.size()>0){
            int num_cliente = 1;
            int num_historial = 1;
            for(int cliente_id : clientes) {
                System.out.println("\n[CLIENTE #" + num_cliente + ": " + gestionBD.obtenerNombreCompleto(cliente_id)+ " (ID " + cliente_id + ")]");
                List<Integer> reservas_ids = gestionBD.obtenerReservasClienteRestaurante(cliente_id, restaurante_id);
                for(int i=0;i<reservas_ids.size();i++) {
                    System.out.println("\n(" + num_historial + ")");
                    gestionBD.datos_completos_historial(reservas_ids.get(i));
                    num_historial++;}
                num_cliente++;}}
        else {
            System.out.println("\nNo hay visitas de clientes registradas en el sistema para la sucursal " + getSucursal_restaurante() + ".");}
    }

    // -------- GESTION DE INVENTARIOS ---------------- 
        public void ver_inventario(int restaurante_id, GestionBD gestionBD) {
            // Obtener el inventario del restaurante
            nombresInsumos.clear();
            cantidades.clear();
            fechasCaducidad.clear();
        
            // Obtener los datos del inventario
            List<List<Object>> inventario = gestionBD.obtenerInventarioPorRestaurante(restaurante_id);
            
            for (List<Object> item : inventario) {
                nombresInsumos.add((String) item.get(0)); // Nombre del insumo
                cantidades.add((Integer) item.get(1));    // Cantidad
                fechasCaducidad.add((Date) item.get(2));  // Fecha de caducidad
            }
        
            // Mostrar inventario
            System.out.println();
            System.out.printf("%-30s %-10s %-15s%n", "Nombre del insumo", "Cantidad", "Fecha de caducidad");
            System.out.println("--------------------------------------------------------------");
            for (int i = 0; i < nombresInsumos.size(); i++) {
                System.out.printf("%-30s %-10d %-15s%n", nombresInsumos.get(i), cantidades.get(i), fechasCaducidad.get(i));
            }
        }

        public void cambiarCantidadInsumo(int restauranteId, String nombreInsumo, int nuevaCantidad, GestionBD gestionBD) {
            gestionBD.actualizarCantidadInsumo(restauranteId, nombreInsumo, nuevaCantidad);
        }

        //ordenar inventario por cantidad... 
    public void ordenarPorMayorCantidad() {
        // Crear una lista de índices para ordenar
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < nombresInsumos.size(); i++) {
            indices.add(i);
        }
    
        // Ordenar los índices por cantidad
        Collections.sort(indices, (i1, i2) -> Integer.compare(cantidades.get(i2), cantidades.get(i1)));
    
        // Reordenar las listas
        List<String> nombresInsumosOrdenados = new ArrayList<>();
        List<Integer> cantidadesOrdenadas = new ArrayList<>();
    
        for (int index : indices) {
            nombresInsumosOrdenados.add(nombresInsumos.get(index));
            cantidadesOrdenadas.add(cantidades.get(index));
        }
    
        // Actualizar las listas originales
        nombresInsumos.clear();
        cantidades.clear();
        nombresInsumos.addAll(nombresInsumosOrdenados);
        cantidades.addAll(cantidadesOrdenadas);
    }

    // ordenar el inventario por fechas de caducidad...
    public void ordenarPorFechaCaducidad() {
        // Crear una lista de índices para ordenar
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < nombresInsumos.size(); i++) {
            indices.add(i);
        }
    
        // Ordenar los índices por fecha de caducidad
        Collections.sort(indices, (i1, i2) -> fechasCaducidad.get(i1).compareTo(fechasCaducidad.get(i2)));
    
        // Reordenar las listas
        List<String> nombresInsumosOrdenados = new ArrayList<>();
        List<Integer> cantidadesOrdenadas = new ArrayList<>();
        List<Date> fechasCaducidadOrdenadas = new ArrayList<>();
    
        for (int index : indices) {
            nombresInsumosOrdenados.add(nombresInsumos.get(index));
            cantidadesOrdenadas.add(cantidades.get(index));
            fechasCaducidadOrdenadas.add(fechasCaducidad.get(index));
        }
    
        // Actualizar las listas originales
        nombresInsumos.clear();
        cantidades.clear();
        fechasCaducidad.clear();
        nombresInsumos.addAll(nombresInsumosOrdenados);
        cantidades.addAll(cantidadesOrdenadas);
        fechasCaducidad.addAll(fechasCaducidadOrdenadas);
    }
}