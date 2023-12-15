package ServerHttp.dto;

public class Producto {
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

    public Producto() {
    }

    @Override
    public String toString() {
        return idProducto+"-"+nameProducto+"-"+detailProducto+"-"+priceProducto+"-"+stockProducto;
    }
}
