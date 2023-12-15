package ServerHttp.dto;

public class Venta {
    public String idVenta;
    public String RUC;
    public String name;
    public String costTotal;

    public Venta(String idVenta, String RUC, String name, String costTotal) {
        this.idVenta = idVenta;
        this.RUC = RUC;
        this.name = name;
        this.costTotal = costTotal;
    }

    public Venta() {
    }

    @Override
    public String toString() {
        return idVenta+"-"+RUC+"-"+name+"-"+costTotal;
    }
}
