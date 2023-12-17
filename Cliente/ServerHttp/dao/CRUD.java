package ServerHttp.dao;

import ServerHttp.dto.Producto;

import java.io.*;

public class CRUD {

    public String path = "ServerHttp/database/baseDatosAlmacen.txt";

    public Producto createProducto(String idProducto, String nameProducto, String detailProducto, String priceProducto, String stockProducto) {
        boolean existID = false;
        try {
            FileReader fr = new FileReader(path);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split("-");
                if (data[0].equals(idProducto)) {
                    System.out.println(data[0]);
                    existID = true;
                    break;
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(existID){
            System.out.println("Failed to create product with same ID " + idProducto + ".");
            return null;
        }

        Producto producto = new Producto(idProducto, nameProducto, detailProducto, priceProducto, stockProducto);
        try {
            FileWriter fw = new FileWriter(path, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(producto.toString());
            bw.newLine();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return producto;
    }


    public Producto readProducto(String idProducto) {
        Producto producto = null;
        try {
            FileReader fr = new FileReader(path);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split("-");
                if (data[0].equals(idProducto)) {
                    System.out.println(data[0]);
                    producto = new Producto();
                    producto.idProducto = data[0];
                    producto.nameProducto = data[1];
                    producto.detailProducto = data[2];
                    producto.priceProducto = data[3];
                    producto.stockProducto = data[4];
                    break;
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return producto;
    }


    public Producto updateProducto(String idProducto, String nameProducto, String detailProducto, String priceProducto, String stockProducto) {
        Producto producto = new Producto(idProducto, nameProducto, detailProducto, priceProducto, stockProducto);
        try {
            File inputFile = new File(path);
            File tempFile = new File("temp.txt");
            boolean isUpdate = false;
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String lineToUpdate = idProducto;
            String currentLine;

            while ((currentLine = reader.readLine()) != null) {
                String[] data = currentLine.split("-");
                if (!data[0].equals(lineToUpdate)) {
                    writer.write(currentLine);
                    writer.newLine();
                }
                else {
                    writer.write(producto.toString());
                    writer.newLine();
                    isUpdate = true;
                }
            }

            writer.close();
            reader.close();

            if (inputFile.delete() && isUpdate) {
                tempFile.renameTo(inputFile);
                System.out.println("Product with ID " + idProducto + " updated successfully.");
            } else if (!isUpdate) {
                System.out.println("Failed to found product with ID " + idProducto + ".");
            }
            else{
                System.out.println("Failed to update product with ID " + idProducto + ".");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return producto;
    }

    public void deleteProducto(String idProducto) {
        try {
            File inputFile = new File(path);
            File tempFile = new File("ServerHttp/database/temp.txt");
            int cont = 0;
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String lineToRemove = idProducto;
            String currentLine;

            while ((currentLine = reader.readLine()) != null) {
                String[] data = currentLine.split("-");
                if (!data[0].equals(lineToRemove)) {
                    writer.write(currentLine);
                    writer.newLine();
                }
                else {
                    cont++;
                }
            }

            writer.close();
            reader.close();

            if (inputFile.delete() && cont != 0) {
                tempFile.renameTo(inputFile);
                System.out.println("Product with ID " + idProducto + " deleted successfully.");
            } else if (cont == 0) {
                System.out.println("Failed to found product with ID " + idProducto + ".");
            }
            else{
                System.out.println("Failed to delete product with ID " + idProducto + ".");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        CRUD crud = new CRUD();
        crud.createProducto("1", "nameProducto", "detailProducto", "priceProducto", "stockProducto");
        crud.createProducto("2", "nameProducto", "detailProducto", "priceProducto", "stockProducto");

        Producto producto = crud.readProducto("1");
        System.out.println(producto);
        crud.deleteProducto("1");
        crud.updateProducto("2", "nameeeeeee", "detailProducto", "priceProducto", "stockProducto");
    }
}
