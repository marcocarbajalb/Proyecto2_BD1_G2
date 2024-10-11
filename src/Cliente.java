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
    public void mostrarMenu(ITipoUsuario usuario_activo, GestionBD gestionBD, TimeSimulator simulator, Scanner scanString, Scanner scanInt) {
        boolean menu_secundario = true;
		    while(menu_secundario) {
		        System.out.println("\n░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░");
                System.out.println("\n\t\t╔══════════════════════════════════════════╗\n\t\t║ Fecha y hora actual: " + simulator.getFechaFormateada() + " ║\n\t\t╚══════════════════════════════════════════╝");
                
                System.out.println("\n[CLIENTE]\nBienvenido/a, "+ usuario_activo.getNombres() + " " + usuario_activo.getApellidos());
		        System.out.println("\nIngrese el numero correspondiente a la opcion que desea realizar:\n1. Reservar en restaurante\n2. Consultar mis reservas\n3. Consultar mi historial de visitas\n4. Cerrar sesión");

				int decision_secundaria = 0;
				try {decision_secundaria = scanInt.nextInt();}

				catch(Exception e) {//En caso de que el usuario ingrese texto en lugar de un número 
					System.out.println("\n**ERROR** La decision ingresada debe ser un numero.");
					scanInt.nextLine();
					continue;}
				
				switch(decision_secundaria) {
					case 1:{//Reservar en restaurante
						System.out.println("\n╠════════════════════════RESERVAR EN RESTAURANTE════════════════════════╣");
                        hacer_reserva(usuario_activo, gestionBD, simulator, scanString, scanInt);
						break;}
					
					case 2:{//Consultar mis reservas
						System.out.println("\n╠═════════════════════════CONSULTAR MIS RESERVAS════════════════════════╣");
											
						break;}
					
					case 3:{//Consultar mi historial de visitas
						System.out.println("\n╠════════════════════CONSULTAR MI HISTORIAL DE VISITAS══════════════════╣");

						break;}
					
					case 4:{//Cerrar sesión
						menu_secundario = false;
						break;}
					
					default:{//Opción no disponible (programación defensiva)
						System.out.println("\n**ERROR**\nEl numero ingresado no se encuentra entre las opciones disponibles.");}}}
    }

    public void hacer_reserva(ITipoUsuario usuario_activo, GestionBD gestionBD, TimeSimulator simulator, Scanner scanString, Scanner scanInt){

        //Atributos necesarios para hacer una reserva
        Integer cliente_id, restaurante_id, num_personas=0, dia=0, mes=0, year=0, hora=0;
        String fecha_reserva = "", hora_reserva="";
        
        //[Registro de datos para hacer la reserva]
        
        cliente_id = usuario_activo.getUsuario_id();

        //Solicitar al usuario la sede del restaurante en el que desea reservar
		String [] restaurantes = {"Campus Pizza UVG","Campus Pizza URL","Campus Pizza UFM","Campus Pizza UNIS", "Campus Pizza USAC"};

        int decision_restaurante = 0;
        boolean seleccion_restaurante = true;
        while(seleccion_restaurante) {
            System.out.println("\nIngrese el numero correspondiente al restaurante en el que desea reservar: ");
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

        System.out.println(fecha_reserva + " " + hora_reserva);

        int cantidad_mesas = Math.ceilDiv(num_personas, 4);

        //Verificar que la fecha y hora de la reserva no estén en el pasado
        if(simulator.fechaPasada(fecha_reserva + " " + hora_reserva)) {
            System.out.println("\n**ERROR** La fecha y hora de la reserva no pueden ser anteriores a la actual (" + simulator.getFechaFormateada() + ").");
            return;}
        //Si hay mesas disponibles, hacer la reserva
        else if (gestionBD.verificarMesasDisponibles(restaurante_id, cantidad_mesas, fecha_reserva, hora_reserva)==false) {
            System.out.println("\n**ERROR** No hay/habrá suficientes mesas disponibles en el restaurante para hacer la reserva en la fecha y hora especificadas.");
            return;}
        else {
            int reserva_id = gestionBD.realizarReserva(cliente_id, restaurante_id, num_personas, cantidad_mesas, fecha_reserva, hora_reserva);
            if(reserva_id>0) {
                System.out.println("\nRESERVA REALIZADA CON ÉXITO.\nA continuación se presentan los detalles de su reserva:\n");
                gestionBD.detalles_reserva(reserva_id, restaurante_id);}}
        }
}