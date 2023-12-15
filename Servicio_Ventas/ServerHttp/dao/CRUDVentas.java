package ServerHttp.dao;

import ServerHttp.dto.Venta;

import java.io.*;

public class CRUDVentas {

    public String path = "ServerHttp/database/baseDatosVenta.txt";

    public Venta createVenta(String idVenta, String RUC, String name, String costTotal) {
        boolean existID = false;
        try {
            FileReader fr = new FileReader(path);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split("-");
                if (data[0].equals(idVenta)) {
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
            System.out.println("Failed to create venta with same ID " + idVenta + ".");
            return null;
        }

        Venta venta = new Venta(idVenta, RUC, name, costTotal);
        try {
            FileWriter fw = new FileWriter(path, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(venta.toString());
            bw.newLine();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return venta;
    }


    public Venta readVenta(String idVenta) {
        Venta venta = null;
        try {
            FileReader fr = new FileReader(path);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split("-");
                if (data[0].equals(idVenta)) {
                    System.out.println(data[0]);
                    venta = new Venta();
                    venta.idVenta = data[0];
                    venta.RUC = data[1];
                    venta.name = data[2];
                    venta.costTotal = data[3];
                    break;
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return venta;
    }


    public Venta updateVenta(String idVenta, String RUC, String name, String costTotal) {
        Venta venta = new Venta(idVenta, RUC, name, costTotal);
        try {
            File inputFile = new File(path);
            File tempFile = new File("temp.txt");
            boolean isUpdate = false;
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String lineToUpdate = idVenta;
            String currentLine;

            while ((currentLine = reader.readLine()) != null) {
                String[] data = currentLine.split("-");
                if (!data[0].equals(lineToUpdate)) {
                    writer.write(currentLine);
                    writer.newLine();
                }
                else {
                    writer.write(venta.toString());
                    writer.newLine();
                    isUpdate = true;
                }
            }

            writer.close();
            reader.close();

            if (inputFile.delete() && isUpdate) {
                tempFile.renameTo(inputFile);
                System.out.println("Venta with ID " + idVenta + " updated successfully.");
            } else if (!isUpdate) {
                System.out.println("Failed to found venta with ID " + idVenta + ".");
            }
            else{
                System.out.println("Failed to update venta with ID " + idVenta + ".");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return venta;
    }

    public void deleteVenta(String idVenta) {
        try {
            File inputFile = new File(path);
            File tempFile = new File("ServerHttp/database/temp.txt");
            int cont = 0;
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String lineToRemove = idVenta;
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
                System.out.println("Product with ID " + idVenta + " deleted successfully.");
            } else if (cont == 0) {
                System.out.println("Failed to found product with ID " + idVenta + ".");
            }
            else{
                System.out.println("Failed to delete product with ID " + idVenta + ".");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        CRUDVentas crudVentas = new CRUDVentas();
        crudVentas.createVenta("1", "nameProducto", "detailProducto", "priceProducto");
        crudVentas.createVenta("2", "nameProducto", "detailProducto", "priceProducto");

        Venta producto = crudVentas.readVenta("1");
        System.out.println(producto);
        crudVentas.deleteVenta("1");
        crudVentas.updateVenta("2", "nameeeeeee", "detailProducto", "priceProducto");
    }
}
