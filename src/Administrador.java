/* BASE DE DATOS 1 - Sección 10
 * Marco Carbajal, José Pablo Donado, José Roberto Rodríguez y Oscar Escribá
 * [Proyecto 2 - Desarrollo y consulta de BDs operativas]
 * Clase para los usuarios de tipo administrador
 */
 
 import java.util.Scanner;

public class Administrador extends ITipoUsuario {
    
    private int usuario_id;
    private String nombres;
    private String apellidos;
    private String username;
    private String password;
    public final String rol = "administrador";

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
		        System.out.println("\n-------------------------------------------------------------------------");
                System.out.println("\n[ADMINISTRADOR]\nBienvenido/a, "+ usuario_activo.getNombres() + " " + usuario_activo.getApellidos());
		        System.out.println("\nIngrese el numero correspondiente a la opcion que desea realizar:\n1. Desplegar auditoría de cambios\n2. Generar reportes\n3. Ver personal\n4. Gestionar inventario\n5. Ver disponibilidad de mesas\n6. Consultar reservas\n7. Consultar pedidos\n8. Ver historial de clientes\n9. Cerrar sesión");

				int decision_secundaria = 0;
				try {decision_secundaria = scanInt.nextInt();}

				catch(Exception e) {//En caso de que el usuario ingrese texto en lugar de un número 
					System.out.println("\n**ERROR** La decision ingresada debe ser un numero.");
					scanInt.nextLine();
					continue;}
				
				switch(decision_secundaria) {
					case 1:{//Desplegar auditoría de cambios
                        System.out.println("\n-----------------DESPLEGAR AUDITORIA DE CAMBIOS-----------------");

                        break;}

                    case 2:{//Generar reportes
                        System.out.println("\n----------------------GENERAR REPORTES----------------------");
                        menuReportes(gestionBD, scanString, scanInt);
                        break;}
                    
                    case 3:{//Ver personal
                        System.out.println("\n----------------------VER PERSONAL----------------------");

                        break;}
                    
                    case 4:{//Gestionar inventario
                        System.out.println("\n-----------------GESTIONAR INVENTARIO-----------------");

                        break;}
                    
                    case 5:{//Ver disponibilidad de mesas
                        System.out.println("\n---------------VER DISPONIBILIDAD DE MESAS---------------");

                        break;}
                    
                    case 6:{//Consultar reservas
                        System.out.println("\n-----------------CONSULTAR RESERVAS-----------------");

                        break;}
                    
                    case 7:{//Consultar pedidos
                        System.out.println("\n------------------CONSULTAR PEDIDOS------------------");

                        break;}

                    case 8:{//Ver historial de clientes
                        System.out.println("\n-------------------VER HISTORIAL DE CLIENTES-------------------");

                        break;}

                    case 9:{//Cerrar sesión
                        menu_secundario = false;
                        break;}
					
					default:{//Opción no disponible (programación defensiva)
						System.out.println("\n**ERROR**\nEl numero ingresado no se encuentra entre las opciones disponibles.");}}}
    }

    public void menuReportes(GestionBD gestionBD, Scanner scanString, Scanner scanInt) {
        boolean menu_reportes = true;
        while(menu_reportes) {
            System.out.println("\nIngrese el numero correspondiente al reporte que sea visualizar:\n1. Top 10 de los platos más vendidos\n2. Top 10 de los clientes más frecuentes\n3. Top 5 de los clientes con mayores reservas y su preferencia de platos\n4. Reporte mensual de insumos a punto de terminarse o caducar\n5. Comportamiento de sucursales con mayor cantidad de reservas y ventas\n6. Regresar al menú principal");

            int decision_reporte = 0;
            try {decision_reporte = scanInt.nextInt();}

            catch(Exception e) {//En caso de que el usuario ingrese texto en lugar de un número 
                System.out.println("\n**ERROR** La decision ingresada debe ser un numero.");
                scanInt.nextLine();
                continue;}
            
            switch(decision_reporte) {
                case 1:{//Top 10 de los platos más vendidos
                    System.out.println("\n-----------------TOP 10 DE LOS PLATOS MÁS VENDIDOS-----------------");

                    break;}

                case 2:{//Top 10 de los clientes más frecuentes
                    System.out.println("\n-----------------TOP 10 DE LOS CLIENTES MÁS FRECUENTES-----------------");

                    break;}
                
                case 3:{//Top 5 de los clientes con mayores reservas y su preferencia de platos
                    System.out.println("\n-----------------TOP 5 DE LOS CLIENTES CON MAYORES RESERVAS Y SU PREFERENCIA DE PLATOS-----------------");

                    break;}
                
                case 4:{//Reporte mensual de insumos a punto de terminarse o caducar
                    System.out.println("\n-----------------REPORTE MENSUAL DE INSUMOS A PUNTO DE TERMINARSE O CADUCAR-----------------");

                    break;}
                
                case 5:{//Comportamiento de sucursales con mayor cantidad de reservas y ventas
                    System.out.println("\n-----------------COMPORTAMIENTO DE SUCURSALES CON MAYOR CANTIDAD DE RESERVAS Y VENTAS-----------------");

                    break;}
                
                case 6:{//Regresar al menú principal
                    menu_reportes = false;
                    break;}
                
                default:{//Opción no disponible (programación defensiva)
                    System.out.println("\n**ERROR**\nEl numero ingresado no se encuentra entre las opciones disponibles.");}}}
    }
}