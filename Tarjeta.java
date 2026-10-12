package edu.itson.cu6.model;

/** Datos de una tarjeta ficticia del directorio de demostración. */
public final class Tarjeta {
    private final String numero;
    private final String banco;
    private final String ciudad;

    public Tarjeta(String numero, String banco, String ciudad) {
        this.numero = numero;
        this.banco = banco;
        this.ciudad = ciudad;
    }

    public String getBanco() { return banco; }
    public String getCiudad() { return ciudad; }
    public String getNumeroEnmascarado() {
        return "•••• •••• •••• " + numero.substring(numero.length() - 4);
    }
}
