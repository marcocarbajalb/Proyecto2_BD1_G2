/* BASE DE DATOS 1 - Sección 10
 * Marco Carbajal, José Pablo Donado, José Roberto Rodríguez y Oscar Escribá
 * [Proyecto 2 - Desarrollo y consulta de BDs operativas]
 * Clase para los usuarios de tipo cliente
 */
 
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Arrays;

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
    public void mostrarMenu(ITipoUsuario usuario_activo, GestionBD gestionBD, TimeSimulator simulator, Scanner scanString, Scanner scanInt) {
        boolean menu_secundario = true;
		    while(menu_secundario) {
		        System.out.println("\n░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░");
                System.out.println("\n\t\t╔══════════════════════════════════════════╗\n\t\t║ Fecha y hora actual: " + simulator.getFechaFormateada() + " ║\n\t\t╚══════════════════════════════════════════╝");
                
                System.out.println("\n[CLIENTE]\nBienvenido/a, "+ usuario_activo.getNombres() + " " + usuario_activo.getApellidos());
		        System.out.println("\nIngrese el numero correspondiente a la opcion que desea realizar:\n1. Reservar en restaurante\n2. Consultar mis reservas\n3. Dejar observaciones/comentarios \n4. Consultar mi historial de visitas\n5. Cerrar sesión");

				int decision_secundaria = 0;
				try {decision_secundaria = scanInt.nextInt();}

				catch(Exception e) {//En caso de que el usuario ingrese texto en lugar de un número 
					System.out.println("\n**ERROR** La decision ingresada debe ser un numero.");
					scanInt.nextLine();
					continue;}
				
				switch(decision_secundaria) {
					case 1:{//Reservar en restaurante
						System.out.println("\n╠════════════════════════RESERVAR EN RESTAURANTE════════════════════════╣");
                        int cliente_id = usuario_activo.getUsuario_id();
                        hacer_reserva(cliente_id, gestionBD, simulator, scanString, scanInt);
						break;}
					
					case 2:{//Consultar mis reservas
						System.out.println("\n╠═════════════════════════CONSULTAR MIS RESERVAS════════════════════════╣");
						int cliente_id = usuario_activo.getUsuario_id();
                        consultar_reservas(cliente_id, gestionBD);					
						break;}
					
                    case 3:{//Dejar observaciones/comentarios
                        System.out.println("\n╠═════════════════════DEJAR OBERVACIONES/COMENTARIOS════════════════════╣");
                        int cliente_id = usuario_activo.getUsuario_id();
                        dejar_observaciones(cliente_id, gestionBD, scanString, scanInt);
                        break;}
					
                    case 4:{//Consultar mi historial de visitas
						System.out.println("\n╠════════════════════CONSULTAR MI HISTORIAL DE VISITAS══════════════════╣");
                        int cliente_id = usuario_activo.getUsuario_id();
                        consultar_historial(cliente_id, gestionBD);
						break;}
					
					case 5:{//Cerrar sesión
						menu_secundario = false;
						break;}
					
					default:{//Opción no disponible (programación defensiva)
						System.out.println("\n**ERROR**\nEl numero ingresado no se encuentra entre las opciones disponibles.");}}}
    }

    public void hacer_reserva(int usuario_id, GestionBD gestionBD, TimeSimulator simulator, Scanner scanString, Scanner scanInt){

        //Atributos necesarios para hacer una reserva
        Integer cliente_id, restaurante_id, num_personas=0, dia=0, mes=0, year=0, hora=0;
        String fecha_reserva = "", hora_reserva="";
        
        //[Registro de datos para hacer la reserva]
        
        cliente_id = usuario_id;

        //Solicitar al usuario la sede del restaurante en el que desea reservar
		String [] restaurantes = {"Campus Pizza UVG","Campus Pizza URL","Campus Pizza UFM","Campus Pizza UNIS", "Campus Pizza USAC"};

        int decision_restaurante = 0;
        boolean seleccion_restaurante = true;
        while(seleccion_restaurante) {
            System.out.println("\nIngrese el numero correspondiente a la sede en la que desea reservar: ");
            for(int i=0;i<restaurantes.length;i++) {
                System.out.println((i+1) + ". " + restaurantes[i]);}
            
            try {
                decision_restaurante = scanInt.nextInt();} 
            catch(Exception e) {
                System.out.println("\n**ERROR** La decision ingresada debe ser un numero.");
                scanInt.nextLine();
                continue;}
            
            if((decision_restaurante>=1)&&(decision_restaurante<=restaurantes.length)) {
                seleccion_restaurante = false;} 
            
            else {
                System.out.println("\n**ERROR** El numero ingresado no se encuentra entre las opciones disponibles.");}}
            
        restaurante_id = decision_restaurante;

        //Solicitar al usuario el número de personas para la reserva
        boolean seleccion_num_personas = true;
        while(seleccion_num_personas) {
            System.out.println("\nIngrese el numero de personas para la reserva: ");
            try {
                num_personas = scanInt.nextInt();} 
            catch(Exception e) {
                System.out.println("\n**ERROR** La decision ingresada debe ser un numero.");
                scanInt.nextLine();
                continue;}
            
            if(num_personas>0) {
                seleccion_num_personas = false;} 
            
            else {
                System.out.println("\n**ERROR** El numero de personas debe ser mayor a 0.");}}
        
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
            
            if((dia>0)&&(dia<=31)&&(mes>0)&&(mes<=12)&&(year>=2024)) {
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
                System.out.println("\nRESERVA REALIZADA CON ÉXITO.\nA continuación se presentan los detalles de su reserva:\n");
                gestionBD.detalles_reserva(reserva_id, restaurante_id);
                int plato_favorito_id = establecer_pedido(reserva_id, gestionBD, scanInt);
                gestionBD.registrarHistorialCliente(reserva_id, plato_favorito_id);}}
        }

    public int establecer_pedido(int reserva_id, GestionBD gestionBD, Scanner scanInt) {
        
        List<String> platos = new ArrayList<>(Arrays.asList("Pizza hawaiana","Pizza de pepperoni","Pizza de queso","Pizza de vegetales","Pizza margarita","Pizza de jamón","Agua pura","Coca-cola","Coca-cola zero"));
        Double[] precios = {70.00,65.00,65.00,70.00,70.00,65.00,15.00,10.00,10.00};
        ArrayList<Integer> platos_pedido = new ArrayList<Integer>();

        boolean terminar_pedido = false;
        while(terminar_pedido==false){
            System.out.println("\n╔════════════════════════════════════════════════╗\n║                MENU CAMPUS PIZZA               ║\n╠════════════════════════════════════════════════╣\n║ 1. Pizza hawaiana...................... Q70.00 ║\n║ 2. Pizza de pepperoni.................. Q65.00 ║\n║ 3. Pizza de queso...................... Q65.00 ║\n║ 4. Pizza de vegetales.................. Q70.00 ║\n║ 5. Pizza margarita..................... Q70.00 ║\n║ 6. Pizza de jamón...................... Q65.00 ║\n╠════════════════════════════════════════════════╣\n║ 7. Agua pura.......................... Q15.00  ║\n║ 8. Coca-cola.......................... Q10.00  ║\n║ 9. Coca-cola zero..................... Q10.00  ║\n╚════════════════════════════════════════════════╝");
            System.out.println("\nIngrese el numero correspondiente al plato que desea agregar al pedido de su reserva (o '0' para dar por finalizado el pedido): ");

            int decision_pedido = -1;
            try {decision_pedido = scanInt.nextInt();}

            catch(Exception e) {//En caso de que el usuario ingrese texto en lugar de un número 
                System.out.println("\n**ERROR** La decision ingresada debe ser un numero.");
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
                        System.out.println("\n**ERROR** La cantidad ingresada debe ser un numero.");
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
                System.out.println("\n**ERROR** El numero ingresado no se encuentra entre las opciones disponibles.");}}

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
            System.out.println("A continuación se presentan los detalles de su pedido:\n");

            System.out.printf("%-21s %-10s %-10s %-10s\n", "Plato", "Cantidad", "Precio", "Total");
            System.out.println("-----------------------------------------------------");
            for (int p = 0; p < platos_unicos.size(); p++) {
                System.out.printf("%-21s %-10d Q%-9.2f Q%-9.2f\n", platos_unicos.get(p), cantidades.get(p), precios_unitarios.get(p), precios_totales.get(p));}
            System.out.println("-----------------------------------------------------");
            System.out.printf("Total de la cuenta: Q%-9.2f\n", total_general);

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

    public void consultar_reservas(int cliente_id, GestionBD gestionBD) {
        List<Integer> reservas_ids = gestionBD.obtenerReservasCliente(cliente_id);
        if(reservas_ids.size()>0) {
            for(int i=0;i<reservas_ids.size();i++) {
                System.out.println("\n(" + (i+1) + ")");
                gestionBD.datos_completos_reserva(reservas_ids.get(i));}
        }
        else {
            System.out.println("\nOPCION NO DISPONIBLE. \nNo se han encontrado reservas asociadas a su cuenta.");}
    }

    public void dejar_observaciones(int cliente_id, GestionBD gestionBD, Scanner scanString, Scanner scanInt) {
        List<Integer> reservas_ids = gestionBD.obtenerReservasCliente(cliente_id);
        if(reservas_ids.size()>0) {
            int i;
            System.out.println("\nIngrese el numero correspondiente a la reserva en la que desea dejar observaciones/comentarios sobre su experiencia: ");
            for(i=0;i<reservas_ids.size();i++) {
                System.out.println((i+1) + ". Reserva #" + reservas_ids.get(i));}
            System.out.println((i+1) + ". Regresar al menú principal");
            int decision_reserva = 0;
            try {decision_reserva = scanInt.nextInt();}

            catch(Exception e) {//En caso de que el usuario ingrese texto en lugar de un número 
                System.out.println("\n**ERROR** La decision ingresada debe ser un numero.");
                scanInt.nextLine();
                return;}
            
            if((decision_reserva>=1)&&(decision_reserva<=reservas_ids.size())) {
                System.out.println("\nIngrese las observaciones/comentarios sobre su experiencia que desea dejar para la reserva #" + reservas_ids.get(decision_reserva-1) + ": ");
                String observaciones = scanString.nextLine();
                gestionBD.agregarObservacionesReserva(reservas_ids.get(decision_reserva-1), observaciones);
                System.out.println("\nOBSERVACIONES/COMENTARIOS REGISTRADOS EXITOSAMETE.\n¡Gracias por compartir tu opinión con nosotros! Campus Pizza agradece tu preferencia.");}
            
            else if(decision_reserva==(i+1)) {
                return;}
            
            else {
                System.out.println("\n**ERROR** El numero ingresado no se encuentra entre las opciones disponibles.");}
        }
        else {
            System.out.println("\nOPCION NO DISPONIBLE. \nNo se han encontrado reservas asociadas a su cuenta.");}
    }

    public void consultar_historial(int cliente_id, GestionBD gestionBD) {
        List<Integer> reservas_ids = gestionBD.obtenerReservasCliente(cliente_id);
        if(reservas_ids.size()>0) {
            for(int i=0;i<reservas_ids.size();i++) {
                System.out.println("\n(" + (i+1) + ")");
                gestionBD.datos_completos_historial(reservas_ids.get(i));}
        }
        else {
            System.out.println("\nHISTORIAL VACIO. \nTodavía no ha realizado ninguna reserva.");}
    }
}