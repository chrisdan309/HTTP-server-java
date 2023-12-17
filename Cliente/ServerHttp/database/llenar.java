package ServerHttp.database;

import ServerHttp.dto.Producto;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class llenar {
    public static void main(String[] args) {
        String idProducto;
        String nameProducto;
        String detailProducto;
        String priceProducto;
        String stockProducto;

        for (int i = 1; i <= 100; i++) {
            idProducto = String.valueOf(i);
            nameProducto = "Producto"+i;
            detailProducto = "Detalle"+i;
            priceProducto = String.valueOf(i*10);
            stockProducto = String.valueOf(i*100);

            Producto producto = new Producto(idProducto, nameProducto, detailProducto, priceProducto, stockProducto);
            // Leer archivo baseDatosAlmacen
            // Escribir archivo baseDatosAlmacen
            try{
                FileWriter fw = new FileWriter("ServerHttp/database/baseDatosAlmacen.txt", true);
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(producto.toString());
                bw.newLine();
                bw.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            System.out.println(producto.toString());
        }
    }
}
