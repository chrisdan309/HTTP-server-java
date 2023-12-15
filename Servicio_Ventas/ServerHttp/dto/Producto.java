package ServerHttp.dto;

public class Producto {
    public String idVenta;
    public String idProducto;
    public String nameProducto;
    public String detailProducto;
    public String priceProducto;
    public String stockProducto;

    public Producto(String idProducto, String nameProducto, String detailProducto, String priceProducto, String stockProducto) {
        this.idProducto = idProducto;
        this.nameProducto = nameProducto;
        this.detailProducto = detailProducto;
        this.priceProducto = priceProducto;
        this.stockProducto = stockProducto;
    }

    public Producto(String idVenta,String idProducto, String nameProducto, String detailProducto, String priceProducto, String stockProducto) {
        this.idVenta = idVenta;
        this.idProducto = idProducto;
        this.nameProducto = nameProducto;
        this.detailProducto = detailProducto;
        this.priceProducto = priceProducto;
        this.stockProducto = stockProducto;
    }

    public Producto() {
    }

    @Override
    public String toString() {
        return idVenta+"-"+idProducto+"-"+nameProducto+"-"+detailProducto+"-"+priceProducto+"-"+stockProducto;
    }
}
