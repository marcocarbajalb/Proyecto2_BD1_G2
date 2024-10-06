/* BASE DE DATOS 1 - Sección 10
 * Marco Carbajal, José Pablo Donado, José Roberto Rodríguez y Oscar Escribá
 * [Proyecto 2 - Desarrollo y consulta de BDs operativas]
 * User Interface para el sistema de Campus Pizza
 */  

//Importar las librerías que harán falta para el programa
import java.sql.Connection;
import java.util.Scanner;

public class SistemaCampusPizzaUI {
   
    public static void main(String[] args) {

        //Conectar con la base de datos
        ConexionBD conexionBD = new ConexionBD();
        Connection conexion = conexionBD.conectar();

        if (conexion != null) {

            //Instanciar el objeto que gestionará la base de datos
            GestionBD gestionBD = new GestionBD(conexion);

            //Crear los scanners que registrarán los datos ingresados por el ususario
            Scanner scanInt = new Scanner(System.in);
            Scanner scanString = new Scanner(System.in);
            
            boolean menu_principal = true;
            while(menu_principal) {
                
                //Menú que se le mostrará al usuario
                System.out.println("\n\n-------------------BIENVENIDO/A AL SISTEMA DE CAMPUS PIZZA-------------------");
                System.out.println("\nIngrese el numero correspondiente a la opcion que desea realizar:\n1. Registrarse.\n2. Iniciar sesión.\n3. Salir del programa.");
                
                int decision_principal = 0;
                try {decision_principal = scanInt.nextInt();}

                catch(Exception e) {//En caso de que el usuario ingrese texto en lugar de un número 
                    System.out.println("\n**ERROR** La decision ingresada debe ser un numero.");
                    scanInt.nextLine();
                    continue;}
                    
                switch(decision_principal) {
                    case 1:{//Registrarse
                        System.out.println("\n----------------------------REGISTRARSE----------------------------");
                        registrarUsuario(gestionBD, scanString, scanInt);
                        break;}
                    
                    case 2:{//Iniciar sesión
                        System.out.println("\n---------------------------INICIAR SESION---------------------------");
                        iniciarSesion(gestionBD, scanString, scanInt);
                        break;}
                    
                    case 3:{//Salir del programa
                        //Terminar el bucle del menú principal
                        menu_principal = false;
                        
                        //Mostrar al ususario que ha abandonado el programa
                        System.out.println("\nHa abandonado el programa exitosamente.");
                        
                        //Cerrar todos los scanners
                        scanString.close();
                        scanInt.close();
                        
                        break;}
                    
                    default:{//Opción no disponible (programación defensiva)
                        System.out.println("\n**ERROR**\nEl numero ingresado no se encuentra entre las opciones disponibles.");}
                }
            }
        }
    }

    
    public static void registrarUsuario(GestionBD gestionBD, Scanner scanString, Scanner scanInt) {
		
		//Tipos de usuarios disponibles
		String [] tipos_de_perfiles = {"Cliente","Mesero","Gerente","Administrador"};

		int decision_perfil = 0;
	    boolean seleccion_perfil = true;
	    while(seleccion_perfil) {
	        System.out.println("\nIngrese el numero correspondiente a su categoria de perfil: ");
	        for(int i=0;i<tipos_de_perfiles.length;i++) {
	            System.out.println((i+1) + ". " + tipos_de_perfiles[i]);}
	        
	        try {
				decision_perfil = scanInt.nextInt();} 
	        catch(Exception e) {
	            System.out.println("\n**ERROR** La decision ingresada debe ser un numero.");
	            scanInt.nextLine();
	            continue;}
	        
	        if((decision_perfil>=1)&&(decision_perfil<=tipos_de_perfiles.length)) {
	            seleccion_perfil = false;} 
	        
	        else {
	            System.out.println("\n**ERROR** El numero ingresado no se encuentra entre las opciones disponibles.");}}
	    
        int tipo_perfil = decision_perfil;

	    //Aplicar el factory design para instanciar el usuario adecuado
        ITipoUsuario usuario = TipoUsuarioFactory.getTipoUsuarioInstance(tipo_perfil);
        
        //Atributos necesarios para la creación del usuario
	    String username="",password,nombres="",apellidos="";
        Integer restaurante_id = 0;
        
        //[Registro de datos para crear el usuario]

 		//Solicitar al usuario sus nombres y apellidos
        System.out.println("\nIngrese sus nombres separados por espacios (ej. 'Nombre1 Nombre2'):");
        nombres = scanString.nextLine().trim();
        System.out.println("\nIngrese sus apellidos separados por espacios (ej. 'Apellido1 Apellido2'):");
        apellidos = scanString.nextLine().trim();

        String [] sedes_restaurante = {"Campus Pizza UVG","Campus Pizza URL","Campus Pizza UFM","Campus Pizza UNIS", "Campus Pizza USAC"};
        int decision_restaurante = 0;
        if(tipo_perfil==2 || tipo_perfil==3) {
            boolean seleccion_restaurante = true;
            while(seleccion_restaurante) {
                System.out.println("\nIngrese el numero correspondiente a la sede de Campus Pizza a la que pertenece: ");
                for(int i=0;i<sedes_restaurante.length;i++) {
                    System.out.println((i+1) + ". " + sedes_restaurante[i]);}
                
                try {
                    decision_restaurante = scanInt.nextInt();} 
                catch(Exception e) {
                    System.out.println("\n**ERROR** La decision ingresada debe ser un numero.");
                    scanInt.nextLine();
                    continue;}
                
                if((decision_restaurante>=1)&&(decision_restaurante<=sedes_restaurante.length)) {
                    seleccion_restaurante = false;} 
                
                else {
                    System.out.println("\n**ERROR** El numero ingresado no se encuentra entre las opciones disponibles.");}}
            
            restaurante_id = decision_restaurante;}

        //Ciclo para validar el username del usuario
        boolean validar_username = true;
        while(validar_username){
            System.out.println("\nIngrese su nombre de usuario (este debe ser único y no puede contener espacios):");
            username = scanString.nextLine().trim().toLowerCase();

            if(username.contains(" ")) {
                System.out.println("**ERROR** El nombre de usuario ingresado no puede contener espacios.");}
            
            else if(gestionBD.existeUsername(username)) {//Aquí se debe verificar si el username ya existe
                System.out.println("**ERROR** El nombre de usuario ingresado ya se encuentra en uso.");}
            
            else {
                validar_username = false;}}
	        
		//Solicitar contraseña
		System.out.println("\nIngrese su password:");
		password = scanString.nextLine().trim();
        
        //Agregar los datos al usuario y agregarlo a la base de datos
        usuario.setNombres(nombres);
        usuario.setApellidos(apellidos);
        usuario.setUsername(username);
		usuario.setPassword(password);
        if(tipo_perfil==2) {
            ((Mesero) usuario).setRestaurante_id(restaurante_id);}
        else if(tipo_perfil==3) {
            ((Gerente) usuario).setRestaurante_id(restaurante_id);}
        
        //Insertar el usuario en la base de datos
        gestionBD.insertarUsuario(usuario);
        
        //Notificar al usuario y brindarle su información de registro.
        System.out.println("\nUSUARIO CREADO EXITOSAMENTE.");
        if(tipo_perfil==1 || tipo_perfil==4) {
            System.out.println(nombres + " " + apellidos + " (" + tipos_de_perfiles[tipo_perfil-1] + "), su nombre de usuario es: " + username + ", y su password es: " + password + ".");}
        else {
            System.out.println(nombres + " " + apellidos + " (" + tipos_de_perfiles[tipo_perfil-1] + "), su nombre de usuario es: " + username + ", y su password es: " + password + ".\nUsted trabaja en: " + sedes_restaurante[restaurante_id-1] + ".");}
        }


	public static void iniciarSesion(GestionBD gestionBD, Scanner scanString, Scanner scanInt) {
	    	
        //En esta variable se registrará el nombre de usuario ingresado por el ususario
	    String username_ingresado = "";
	   
	    System.out.println("\nIngrese su nombre de usuario: ");
	    username_ingresado = scanString.nextLine().trim().toLowerCase();
	        	
		if(gestionBD.existeUsername(username_ingresado)) {//Si el username ingresado se encuentra en el sistema
			
			//Obtener y validar la contraseña del usuario
			System.out.println("Ingrese su password: ");
	    	String password = scanString.nextLine().trim();

			if(gestionBD.validarPassword(username_ingresado, password)) {//Si la contraseña ingresada es correcta 
		    
            ITipoUsuario usuario_activo = gestionBD.obtenerUsuario(username_ingresado);

			usuario_activo.mostrarMenu(usuario_activo,gestionBD,scanString,scanInt);}
			    
	    else {//Si la contraseña ingresada es incorrecta
			System.out.println("\nCONTRASEÑA INCORRECTA.\nLa contraseña ingresada no es correcta.");}}
	    
	    else {//Si el nombre de usuario ingresado no se encuentra entre los usernames
			System.out.println("\nUSUARIO NO ENCONTRADO.\nNo hay ningun usuario con el username ingresado. Verifique que el username sea correcto o que su usuario ya exista.");}
    }
}