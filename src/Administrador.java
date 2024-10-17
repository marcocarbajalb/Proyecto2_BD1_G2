/* BASE DE DATOS 1 - Sección 10
 * Marco Carbajal, José Pablo Donado, José Roberto Rodríguez y Oscar Escribá
 * [Proyecto 2 - Desarrollo y consulta de BDs operativas]
 * Clase para los usuarios de tipo administrador
 */
 
import java.util.Scanner;
import java.util.List;
import java.util.Date;
import java.util.ArrayList;

public class Administrador extends ITipoUsuario {
    
    private int usuario_id;
    private String nombres;
    private String apellidos;
    private String username;
    private String password;
    public final String rol = "administrador";
        
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
    
    @Override
    public void mostrarMenu(ITipoUsuario usuario_activo, GestionBD gestionBD, TimeSimulator simulator, Scanner scanString, Scanner scanInt) {
        boolean menu_secundario = true;
		    while(menu_secundario) {
		        System.out.println("\n░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░");
                System.out.println("\n\t       ╔══════════════════════════════════════════╗\n\t       ║ Fecha y hora actual: " + simulator.getFechaFormateada(true, 0) + " ║\n\t       ╚══════════════════════════════════════════╝");
                
                System.out.println("\n[ADMINISTRADOR]\nBienvenido/a, "+ usuario_activo.getNombres() + " " + usuario_activo.getApellidos());
		        System.out.println("\nIngrese el número correspondiente a la opción que desea realizar:\n1. Desplegar auditoría de cambios\n2. Generar reportes\n3. Ver personal\n4. Gestionar inventario\n5. Ver disponibilidad de mesas\n6. Consultar reservas y pedidos\n7. Ver historial de clientes\n8. Cerrar sesión");

				int decision_secundaria = 0;
				try {decision_secundaria = scanInt.nextInt();}

				catch(Exception e) {//En caso de que el usuario ingrese texto en lugar de un número 
					System.out.println("\n**ERROR** La decision ingresada debe ser un número.");
					scanInt.nextLine();
					continue;}
				
				switch(decision_secundaria) {
					case 1:{//Desplegar auditoría de cambios
                        System.out.println("\n╠═════════════════════DESPLEGAR AUDITORIA DE CAMBIOS════════════════════╣");
                        int total_tuplas = gestionBD.total_cambios_bitacora();
                        if(total_tuplas <= 0) {
                            System.out.println("\nOPCION NO DISPONIBLE. \nNo hay cambios registrados en la bitácora.");
                            break;}
                        else{
                            boolean validar_cantidad = true;
                            while(validar_cantidad){
                                System.out.println("\n\t\t[Tuplas totales de la bitácora: " + total_tuplas + "]");
                                System.out.println("\nIngrese la cantidad de cambios a la que desea limitar la visualización: ");
                                int cantidad_cambios = 0;
                                try {cantidad_cambios = scanInt.nextInt();}

                                catch(Exception e) {//En caso de que el usuario ingrese texto en lugar de un número 
                                    System.out.println("\n**ERROR** La cantidad ingresada debe ser un número.");
                                    scanInt.nextLine();
                                    continue;}
                                
                                if(cantidad_cambios > 0 && cantidad_cambios <= total_tuplas) {
                                    gestionBD.desplegarAuditoriaCambios(cantidad_cambios);
                                    validar_cantidad = false;}
                                else {
                                    System.out.println("\n**ERROR** La cantidad ingresada no es válida (debe ser mayor a 0 y menor o igual al total de tuplas).");}}}
                        break;}

                    case 2:{//Generar reportes
                        menuReportes(gestionBD, scanString, scanInt, simulator);
                        break;}
                    
                    case 3:{//Ver personal
                        System.out.println("\n╠══════════════════════════════VER PERSONAL═════════════════════════════╣");
                        int restaurante_id;
                        String [] restaurantes = {"Campus Pizza UVG","Campus Pizza URL","Campus Pizza UFM","Campus Pizza UNIS", "Campus Pizza USAC"};

                        int decision_restaurante = 0;
                        boolean seleccion_restaurante = true;
                        while(seleccion_restaurante) {
                            System.out.println("\nIngrese el número de la sucursal de la que desea ver el personal: ");
                            for(int i=0;i<restaurantes.length;i++) {
                                System.out.println((i+1) + ". " + restaurantes[i]);}
                            
                            try {
                                decision_restaurante = scanInt.nextInt();} 
                            catch(Exception e) {
                                System.out.println("\n**ERROR** La decision ingresada debe ser un número.");
                                scanInt.nextLine();
                                continue;}
                            
                            if((decision_restaurante>=1)&&(decision_restaurante<=restaurantes.length)) {
                                seleccion_restaurante = false;} 
                            
                            else {
                                System.out.println("\n**ERROR** El número ingresado no se encuentra entre las opciones disponibles.");}}
                            
                        restaurante_id = decision_restaurante;
                        List<String> gerentes = gestionBD.obtener_gerentes(restaurante_id);
                        List<String> meseros = gestionBD.obtener_meseros(restaurante_id);
                        System.out.println("\n\t\t[Sucursal: " + restaurantes[restaurante_id-1] + "]");
                        System.out.println("GERENTE:");
                        for(int i=0;i<gerentes.size();i++) {
                            System.out.println((i+1) + ". " + gerentes.get(i));}
                        System.out.println("\nMESEROS:");
                        for(int i=0;i<meseros.size();i++) {
                            System.out.println((i+1) + ". " + meseros.get(i));}
                        break;}
                    
                    case 4:{//Gestionar inventario
                        System.out.println("\n╠══════════════════════════GESTIONAR INVENTARIO═════════════════════════╣");
                        int restaurante_id;
                        String[] restaurantes = {"Campus Pizza UVG", "Campus Pizza URL", "Campus Pizza UFM", "Campus Pizza UNIS", "Campus Pizza USAC"};
                    
                        int decision_restaurante = 0;
                        boolean seleccion_restaurante = true;
                        while(seleccion_restaurante) {
                            System.out.println("\nIngrese el número de la sucursal de la que desea gestionar el inventario: ");
                            for(int i = 0; i < restaurantes.length; i++) {
                                System.out.println((i + 1) + ". " + restaurantes[i]);}
                    
                            try {
                                decision_restaurante = scanInt.nextInt();} 
                            catch(Exception e) {
                                System.out.println("\n**ERROR** La decisión ingresada debe ser un número.");
                                scanInt.nextLine();
                                continue;}
                    
                            if((decision_restaurante >= 1) && (decision_restaurante <= restaurantes.length)) {
                                seleccion_restaurante = false;} 
                            else {
                                System.out.println("\n**ERROR** El número ingresado no se encuentra entre las opciones disponibles.");}}
                    
                        restaurante_id = decision_restaurante;
                        
                        boolean validar_menu_inventario = true;
                        while(validar_menu_inventario) {
                            System.out.println("\n╠═══════════════════════════════════════════════════════════════════════╣");
                            System.out.println("\t\t      [Sucursal: " + restaurantes[restaurante_id - 1] + "]");
                        
                            // Opciones de gestión de inventario
                            System.out.println("\nIngrese el número correspondiente a la opción que desea realizar:\n1. Ver inventario\n2. Modificar inventario\n3. Regresar al menú principal");
                                
                            int opcion = 0;
                            try {opcion = scanInt.nextInt();}
                            catch(Exception e) {//En caso de que el usuario ingrese texto en lugar de un número 
                                System.out.println("\n**ERROR** La decision ingresada debe ser un número.");
                                scanInt.nextLine();
                                    continue;}
                            
                            switch (opcion) {
                                case 1:{
                                    System.out.println("\n├─────────────────────────────VER INVENTARIO────────────────────────────┤");
                                    ver_inventario(restaurante_id, gestionBD);

                                    // Opción de ordenar
                                    boolean menu_inventario = true;
                                    int opcion_secundaria = 0;
                                    while (menu_inventario) {
                                        System.out.println("\nIngrese el número correspondiente al filtro que desea aplicar:\n1. Ordenar por fecha de caducidad \n2. Ordenar por mayor cantidad \n3. Regresar");
                                        try {opcion_secundaria = scanInt.nextInt();}
                                        catch(Exception e) {//En caso de que el usuario ingrese texto en lugar de un número 
                                            System.out.println("\n**ERROR** La decision ingresada debe ser un número.");
                                            scanInt.nextLine();
                                            continue;}

                                        switch(opcion_secundaria) {
                                            case 1:
                                                System.out.println("\n├───────────────────────────────────────────────────────────────────────┤");
                                                ver_inventario_ordenado(restaurante_id, gestionBD, "fecha");
                                                break;
                                            case 2:
                                                System.out.println("\n├───────────────────────────────────────────────────────────────────────┤");
                                                ver_inventario_ordenado(restaurante_id, gestionBD, "cantidad");
                                                break;
                                            case 3:
                                                menu_inventario = false;
                                                break;
                                            default:
                                                System.out.println("\n**ERROR** El número ingresado no se encuentra entre las opciones disponibles.");
                                                break;}}
                                    break;}
                                case 2:{
                                    System.out.println("\n├──────────────────────────MODIFICAR INVENTARIO─────────────────────────┤");
                                    modificar_inventario(restaurante_id, gestionBD, scanString, scanInt, simulator);
                                    break;}
                                case 3:{
                                    validar_menu_inventario = false;
                                    break;}
                                default:{
                                    System.out.println("\n**ERROR**\nEl número ingresado no se encuentra entre las opciones disponibles.");
                                    break;}}}
                        break;}
                    
                    case 5:{//Ver disponibilidad de mesas
                        System.out.println("\n╠══════════════════════VER DISPONIBILIDAD DE MESAS══════════════════════╣");
                        int restaurante_id;
                        String [] restaurantes = {"Campus Pizza UVG","Campus Pizza URL","Campus Pizza UFM","Campus Pizza UNIS", "Campus Pizza USAC"};

                        int decision_restaurante = 0;
                        boolean seleccion_restaurante = true;
                        while(seleccion_restaurante) {
                            System.out.println("\nIngrese el número de la sucursal de la que desea ver la disponibilidad de mesas: ");
                            for(int i=0;i<restaurantes.length;i++) {
                                System.out.println((i+1) + ". " + restaurantes[i]);}
                            
                            try {
                                decision_restaurante = scanInt.nextInt();} 
                            catch(Exception e) {
                                System.out.println("\n**ERROR** La decision ingresada debe ser un número.");
                                scanInt.nextLine();
                                continue;}
                            
                            if((decision_restaurante>=1)&&(decision_restaurante<=restaurantes.length)) {
                                seleccion_restaurante = false;} 
                            
                            else {
                                System.out.println("\n**ERROR** El número ingresado no se encuentra entre las opciones disponibles.");}}
                            
                        restaurante_id = decision_restaurante;
                        mostrarDistribucionMesas(restaurante_id, gestionBD, simulator);
                        break;}
                    
                    case 6:{//Consultar reservas y pedidos
                        System.out.println("\n╠═══════════════════════CONSULTAR RESERVAS Y PEDIDOS══════════════════════╣");
                        int restaurante_id;
                        String [] restaurantes = {"Campus Pizza UVG","Campus Pizza URL","Campus Pizza UFM","Campus Pizza UNIS", "Campus Pizza USAC"};

                        int decision_restaurante = 0;
                        boolean seleccion_restaurante = true;
                        while(seleccion_restaurante) {
                            System.out.println("\nIngrese el número de la sucursal de la que desea consultar las reservas y pedidos: ");
                            for(int i=0;i<restaurantes.length;i++) {
                                System.out.println((i+1) + ". " + restaurantes[i]);}
                            
                            try {
                                decision_restaurante = scanInt.nextInt();} 
                            catch(Exception e) {
                                System.out.println("\n**ERROR** La decision ingresada debe ser un número.");
                                scanInt.nextLine();
                                continue;}
                            
                            if((decision_restaurante>=1)&&(decision_restaurante<=restaurantes.length)) {
                                seleccion_restaurante = false;} 
                            
                            else {
                                System.out.println("\n**ERROR** El número ingresado no se encuentra entre las opciones disponibles.");}}
                            
                        restaurante_id = decision_restaurante;
                        System.out.println("\n\t\t[Sucursal: " + restaurantes[restaurante_id-1] + "]\n");
                        consultar_reservas_personal(restaurante_id, gestionBD);
                        break;}

                    case 7:{//Ver historial de clientes
                        System.out.println("\n╠═══════════════════════VER HISTORIAL DE CLIENTES═══════════════════════╣");
                        int restaurante_id;
                        String [] restaurantes = {"Campus Pizza UVG","Campus Pizza URL","Campus Pizza UFM","Campus Pizza UNIS", "Campus Pizza USAC"};

                        int decision_restaurante = 0;
                        boolean seleccion_restaurante = true;
                        while(seleccion_restaurante) {
                            System.out.println("\nIngrese el número de la sucursal de la que desea ver el historial de clientes: ");
                            for(int i=0;i<restaurantes.length;i++) {
                                System.out.println((i+1) + ". " + restaurantes[i]);}
                            
                            try {
                                decision_restaurante = scanInt.nextInt();} 
                            catch(Exception e) {
                                System.out.println("\n**ERROR** La decision ingresada debe ser un número.");
                                scanInt.nextLine();
                                continue;}
                            
                            if((decision_restaurante>=1)&&(decision_restaurante<=restaurantes.length)) {
                                seleccion_restaurante = false;} 
                            
                            else {
                                System.out.println("\n**ERROR** El número ingresado no se encuentra entre las opciones disponibles.");}}
                            
                        restaurante_id = decision_restaurante;
                        System.out.println("\n\t\t[Sucursal: " + restaurantes[restaurante_id-1] + "]\n");
                        ver_historial_clientes(restaurante_id, gestionBD);
                        break;}

                    case 8:{//Cerrar sesión
                        System.out.println("\t\t     ┌─────────────────────────────┐\n\t\t     " + "│ Sesión cerrada exitosamente │\n\t\t     └─────────────────────────────┘");
                        menu_secundario = false;
                        break;}
					
					default:{//Opción no disponible (programación defensiva)
						System.out.println("\n**ERROR**\nEl número ingresado no se encuentra entre las opciones disponibles.");}}}

                        
    }

    public void menuReportes(GestionBD gestionBD, Scanner scanString, Scanner scanInt, TimeSimulator simulator) {
        boolean menu_reportes = true;
        while(menu_reportes) {
            System.out.println("\n╠════════════════════════════GENERAR REPORTES═══════════════════════════╣");
            System.out.println("\nIngrese el número correspondiente al reporte que sea visualizar:\n1. Top 10 de los platos más vendidos\n2. Top 10 de los clientes más frecuentes\n3. Top 5 de los clientes con mayores reservas y su preferencia de platos\n4. Reporte mensual de insumos a punto de terminarse o caducar\n5. Comportamiento de sucursales con mayor cantidad de reservas y ventas\n6. Regresar al menú principal");

            int decision_reporte = 0;
            try {decision_reporte = scanInt.nextInt();}

            catch(Exception e) {//En caso de que el usuario ingrese texto en lugar de un número 
                System.out.println("\n**ERROR** La decision ingresada debe ser un número.");
                scanInt.nextLine();
                continue;}
            
            switch(decision_reporte) {
                case 1:{//Top 10 de los platos más vendidos
                    System.out.println("\n├───────────────────TOP 10 DE LOS PLATOS MAS VENDIDOS───────────────────┤");
                    List<Object[]> topPlatos = gestionBD.platos_mas_vendidos();

                    if(topPlatos.size()>0){
                        int contador = 1;
                        for(Object[] plato : topPlatos) {
                            System.out.printf("%d. %s - Ventas: %d\n",
                                              contador,
                                              plato[0], // nombre del plato
                                              plato[1]); // cantidad de ventas
                            contador++;}}
                    else {
                        System.out.println("\nREPORTE NO DISPONIBLE. \nTodavía no se cuenta con información de ventas de platos.");}
                    
                    break;}

                    case 2: {//Top 10 de los clientes más frecuentes
                        System.out.println("\n├─────────────────TOP 10 DE LOS CLIENTES MAS FRECUENTES─────────────────┤");
                        List<Cliente> topClientes = gestionBD.obtenerTop10ClientesFrecuentes();
                    
                        // Verifica si la lista tiene elementos
                        if (!topClientes.isEmpty()) {
                            int contador = 1;
                            for (Cliente cliente : topClientes) {
                                System.out.printf("%d. %s %s (ID: %d) - Reservas: %d\n",
                                                    contador,
                                                    cliente.getNombres(), // su nombre
                                                    cliente.getApellidos(), // apellido
                                                    cliente.getUsuario_id(), // id
                                                    cliente.getFrecuencia()); //la cantidad de reservas
                                contador++; // se va aumentando con cada iteración
                            }
                        } else {
                            System.out.println("\nREPORTE NO DISPONIBLE. \nTodavía no se cuenta con información de reservas de clientes.");
                        }
                        break;
                    }
                
                case 3:{//Top 5 de los clientes con mayores reservas y su preferencia de platos
                    System.out.println("\n├─TOP 5 DE LOS CLIENTES CON MAYORES RESERVAS Y SU PREFERENCIA DE PLATOS─┤");
                    List<Cliente> topClientesPlatos = gestionBD.obtenerTop5ClientesConPreferencias();

                    // A verificar si tenemos elementos en la lista
                    if (!topClientesPlatos.isEmpty()) {
                        int contador = 1;
                        for (Cliente cliente : topClientesPlatos) {
                            System.out.printf("%d %s %s (ID: %d) %s\n",
                                                contador,
                                                cliente.getNombres(), // su nombre
                                                cliente.getApellidos(), // apellido
                                                cliente.getUsuario_id(), // id
                                                cliente.getPlatosFavoritos()); //plato fav
                            contador++;                        
                        }
                    } else {
                        System.out.println("\nREPORTE NO DISPONIBLE. \nTodavía no se cuenta con información de reservas de clientes.");}

                    break;}
                
                case 4:{//Reporte mensual de insumos a punto de terminarse o caducar
                    System.out.println("\n├───────REPORTE MENSUAL DE INSUMOS A PUNTO DE TERMINARSE O CADUCAR──────┤");
                    List<Object[]> reporte_insumos = gestionBD.reporte_insumos(simulator.getFechaFormateada(false, 0), simulator.getFechaProximaSemana());
                    if(reporte_insumos.size()>0){
                        for(Object[] insumo : reporte_insumos) {
                            System.out.printf("Restaurante: %s - Insumo: %s - Cantidad restante: %d - Fecha de caducidad: %s\n",
                                              insumo[0], // nombre del restaurante
                                              insumo[1], // nombre del insumo
                                              insumo[2], // cantidad restante
                                              insumo[3]); // fecha de caducidad
                        }}
                    else {
                        System.out.println("\nREPORTE NO DISPONIBLE. \nNo hay insumos a punto de terminarse o caducar en el inventario.");}
                    break;}
                
                case 5:{//Comportamiento de sucursales con mayor cantidad de reservas y ventas
                    System.out.println("\n├──COMPORTAMIENTO DE SUCURSALES CON MAYOR CANTIDAD DE RESERVAS Y VENTAS─┤");
                    List<Object[]> comportamiento_sucursales = gestionBD.comportamiento_sucursales();
                    if(comportamiento_sucursales.size()>0){
                        for(Object[] sucursal : comportamiento_sucursales) {
                            System.out.printf("Sucursal: %s - Reservas: %d - Ventas: Q%.2f\n",
                                              sucursal[0], // nombre de la sucursal
                                              sucursal[1], // cantidad de reservas
                                              sucursal[2]); // cantidad de ventas
                        }}
                    else {
                        System.out.println("\nREPORTE NO DISPONIBLE. \nNo hay información de reservas y ventas en las sucursales.");}
                    break;}
                
                case 6:{//Regresar al menú principal
                    menu_reportes = false;
                    break;}
                
                default:{//Opción no disponible (programación defensiva)
                    System.out.println("\n**ERROR**\nEl número ingresado no se encuentra entre las opciones disponibles.");}}}
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
            String [] restaurantes = {"Campus Pizza UVG","Campus Pizza URL","Campus Pizza UFM","Campus Pizza UNIS", "Campus Pizza USAC"};
            System.out.println("\nNo hay reservas ni pedidos registrados en el sistema para la sucursal " + restaurantes[restaurante_id-1] + ".");}
    }

    public void ver_historial_clientes(int restaurante_id, GestionBD gestionBD) {
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
            String [] restaurantes = {"Campus Pizza UVG","Campus Pizza URL","Campus Pizza UFM","Campus Pizza UNIS", "Campus Pizza USAC"};
            System.out.println("\nNo hay visitas de clientes registradas en el sistema para la sucursal " + restaurantes[restaurante_id-1] + ".");}
    }

    public static void mostrarDistribucionMesas(int restaurante_id, GestionBD gestionBD, TimeSimulator simulator) {
        String[] restaurantes = {"Campus Pizza UVG","Campus Pizza URL","Campus Pizza UFM","Campus Pizza UNIS", "Campus Pizza USAC"};
        System.out.println("\nSucursal: " + restaurantes[restaurante_id-1] + "\nFecha y hora actual: " + simulator.getFechaFormateada(false, 0) + "\n");
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
    
    // GESTIÓN DE INVENTARIOS

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

    // Función para ver el inventario ordenado
    public void ver_inventario_ordenado(int restaurante_id, GestionBD gestionBD, String filtro) {
        // Obtener el inventario del restaurante
        nombresInsumos.clear();
        cantidades.clear();
        fechasCaducidad.clear();
    
        // Obtener los datos del inventario
        List<List<Object>> inventario = gestionBD.obtenerInventarioPorRestaurante(restaurante_id, filtro);
        
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

    public void modificar_inventario(int restaurante_id, GestionBD gestionBD, Scanner scanString, Scanner scanInt, TimeSimulator simulator) {
        
        //Atributos necesarios para modificar el inventario
        Integer insumo_id = 0, nuevaCantidad = 0, dia=0, mes=0, year=0;
        String fecha_caducidad = "";

        String[] insumos = {"masa", "bote de salsa de tomate", "bolsa de queso mozzarella", "porción de jamón", "porción de piña", "porción de pepperoni", "porción de champiñones", "porción de cebolla", "porción de albahaca", "porción de tomate", "porción de espinaca", "botella de agua pura", "lata de coca-cola", "lata de coca-cola zero"};
        int decision_insumo = 0;
        boolean seleccion_insumo = true;
        while(seleccion_insumo) {
            System.out.println("\nIngrese el número correspondiente al insumo que desea modificar: ");
            for(int i=0;i<insumos.length;i++) {
                System.out.println((i+1) + ". " + insumos[i]);}
            
            try {
                decision_insumo = scanInt.nextInt();} 
            catch(Exception e) {
                System.out.println("\n**ERROR** La decision ingresada debe ser un número.");
                scanInt.nextLine();
                continue;}
            
            if((decision_insumo>=1)&&(decision_insumo<=insumos.length)) {
                seleccion_insumo = false;} 
            else {
                System.out.println("\n**ERROR** El número ingresado no se encuentra entre las opciones disponibles.");}}

        insumo_id = decision_insumo;

        boolean seleccion_cantidad = true;
        while(seleccion_cantidad) {
            System.out.println("\nIngrese la nueva cantidad del insumo: ");
            try {
                nuevaCantidad = scanInt.nextInt();} 
            catch(Exception e) {
                System.out.println("\n**ERROR** La cantidad ingresada debe ser un número.");
                scanInt.nextLine();
                continue;}
            
            if(nuevaCantidad>0 && nuevaCantidad<=100) {
                seleccion_cantidad = false;} 
            else {
                System.out.println("\n**ERROR** La cantidad ingresada debe ser mayor a 0 y menor o igual a 100.");}}

        boolean seleccion_fecha = true;
        while(seleccion_fecha) {
            System.out.println("\nIngrese la fecha de caducidad del insumo (utilice el formato 'DD/MM/AAAA'): ");
            try {
                fecha_caducidad = scanString.nextLine().trim();
                String[] fecha_caducidad_separada = fecha_caducidad.split("/");
                dia = Integer.parseInt(fecha_caducidad_separada[0]);
                mes = Integer.parseInt(fecha_caducidad_separada[1]);
                year = Integer.parseInt(fecha_caducidad_separada[2]);} 
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
                String mes_formateado = String.format("%02d", mes);
                String dia_formateado = String.format("%02d", dia);
                String fecha_final = year + "-" + mes_formateado + "-" + dia_formateado;
                String fecha_final_completa = year + "-" + mes_formateado + "-" + dia_formateado + " 23:59:59";
                
                if(simulator.fechaPasada(fecha_final_completa)) {
                    System.out.println("\n**ERROR** \nLa fecha de caducidad no puede ser anterior o igual a la fecha actual (" + simulator.getFechaFormateada(false, 0) + ").");}
                else {
                    fecha_caducidad = fecha_final;
                    seleccion_fecha = false;}} 
            else {
                System.out.println("\n**ERROR** La fecha ingresada no es válida.");}}

        gestionBD.actualizar_insumo(restaurante_id, insumo_id, nuevaCantidad, fecha_caducidad);
        System.out.println("\nINSUMO ACTUALIZADO. \nEl insumo ha sido actualizado exitosamente.");
    }
}