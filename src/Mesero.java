/* BASE DE DATOS 1 - Sección 10
 * Marco Carbajal, José Pablo Donado, José Roberto Rodríguez y Oscar Escribá
 * [Proyecto 2 - Desarrollo y consulta de BDs operativas]
 * Clase para los usuarios de tipo mesero
 */
 
 import java.util.Scanner;

public class Mesero extends ITipoUsuario {
    
    private int usuario_id;
    private String nombres;
    private String apellidos;
    private String username;
    private String password;
    public final String rol = "mesero";
    private int restaurante_id;

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

    public String getSede_restaurante() {
        String [] sedes_restaurante = {"Campus Pizza UVG","Campus Pizza URL","Campus Pizza UFM","Campus Pizza UNIS", "Campus Pizza USAC"};
        return sedes_restaurante[this.restaurante_id-1];
    }

    @Override
    public void mostrarMenu(ITipoUsuario usuario_activo, GestionBD gestionBD, TimeSimulator simulator, Scanner scanString, Scanner scanInt) {
        boolean menu_secundario = true;
		    while(menu_secundario) {
		        System.out.println("\n-------------------------------------------------------------------------");
                System.out.println("\n[MESERO | " +  ((Mesero) usuario_activo).getSede_restaurante() + "]\nBienvenido/a, "+ usuario_activo.getNombres() + " " + usuario_activo.getApellidos());
                System.out.println("\t\t[Fecha y hora actual: " + simulator.getFechaFormateada() + "]");
		        System.out.println("\nIngrese el numero correspondiente a la opcion que desea realizar:\n1. Ver disponibilidad de mesas\n2. Consultar reservas\n3. Consultar pedidos\n4. Ver inventario\n5. Cerrar sesión");

				int decision_secundaria = 0;
				try {decision_secundaria = scanInt.nextInt();}

				catch(Exception e) {//En caso de que el usuario ingrese texto en lugar de un número 
					System.out.println("\n**ERROR** La decision ingresada debe ser un numero.");
					scanInt.nextLine();
					continue;}
				
				switch(decision_secundaria) {
					case 1:{//Ver disponibilidad de mesas
						System.out.println("\n---------------VER DISPONIBILIDAD DE MESAS---------------");

						break;}

                    case 2:{//Consultar reservas
                        System.out.println("\n-----------------CONSULTAR RESERVAS-----------------");

                        break;}

                    case 3:{//Consultar pedidos
                        System.out.println("\n------------------CONSULTAR PEDIDOS------------------");

                        break;}
                    
                    case 4:{//Ver inventario
                        System.out.println("\n-------------------VER INVENTARIO-------------------");

                        break;}

                    case 5:{//Cerrar sesión
                        menu_secundario = false;
                        break;}
					
					default:{//Opción no disponible (programación defensiva)
						System.out.println("\n**ERROR**\nEl numero ingresado no se encuentra entre las opciones disponibles.");}}}
    }
}