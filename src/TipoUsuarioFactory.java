/* BASE DE DATOS 1 - Sección 10
 * Marco Carbajal, José Pablo Donado, José Roberto Rodríguez y Oscar Escribá
 * [Proyecto 2 - Desarrollo y consulta de BDs operativas]
 * Factory pattern para el tipo de usuario
 */ 

public class TipoUsuarioFactory {
    
    public static final int cliente = 1;
    public static final int mesero = 2;
    public static final int gerente = 3;
    public static final int administrador = 4;
    
    public static ITipoUsuario getTipoUsuarioInstance(int tipo_perfil) {
        
        switch (tipo_perfil) {
            case cliente:
                return new Cliente();
            case mesero:
                return new Mesero();
            case gerente:
                return new Gerente();
            case administrador:
                return new Administrador();
            default:
                return null;
        }
    }
}