public class ClienteFrecuente {
    private int id;
    private String cliente;
    private int frecuencia;

    // Constructor
    public ClienteFrecuente(int id, String cliente, int frecuencia) {
        this.id = id;
        this.cliente = cliente;
        this.frecuencia = frecuencia;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public String getCliente() {
        return cliente;
    }

    public int getFrecuencia() {
        return frecuencia;
    }

    @Override
    public String toString() {
        return "ClienteFrecuente{" +
               "id=" + id +
               ", cliente='" + cliente + '\'' +
               ", frecuencia=" + frecuencia +
               '}';
    }
}
