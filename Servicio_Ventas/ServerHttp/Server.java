package ServerHttp;

import ServerHttp.dao.CRUDProduct;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Server {
    private TCPServer tcpServer;
    public int clientCount;
    public static ArrayList<Client> connections;
    public static int numberQuery;
    public static CRUDProduct crudProduct = new CRUDProduct();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese el número de clientes (c): ");
        int clientCount = scanner.nextInt();

        Server server = new Server(clientCount);
        server.start();
    }

    public Server(int clientCount) {
        numberQuery = 1;
        this.clientCount = clientCount;
        connections = new ArrayList<>();
        for (int i = 0; i < clientCount; i++) {
            Client aux = new Client();
            int integerPart = (int) (1000 + Math.random() * 9000);
            int firstDecimalPart = (int) (Math.random() * 10);
            int secondDecimalPart = (int) (Math.random() * 10);
            String balance = (integerPart + "." + firstDecimalPart + secondDecimalPart);
            aux.idAccount = (i + 1);
            aux.balance = balance;
            connections.add(aux);
        }
    }

    void start() {
        new Thread(() -> {
            tcpServer = new TCPServer(message -> {
                synchronized (this) {
                    receiveFromServer(message);
                }
            });
            tcpServer.run();
        }).start();

        System.out.print("1. Generar transacciones (v): Escribir Generar-v\n");
        System.out.println("2. Salir");

        String fileName = "Archivos/cuentas.txt";
        try{
            FileWriter fileWriter = new FileWriter(fileName);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            System.out.println("Escribiendo en el archivo " + fileName);

            for (Client client: connections) {
                bufferedWriter.write(client.idAccount + ": " + client.balance);
                bufferedWriter.newLine();
            }
            bufferedWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        /* Imprimir el estado de las cuentas
        for (int i = 0; i < clientCount; i++) {
            System.out.println("Cuenta " + clients[i].idAccount + ": " + clients[i].balance);
        }
        */

        listening();

        tcpServer.stopServer();
        System.exit(0);

    }

    boolean firstResult = true;
    int cont = 0;

    // Recibe los mensajes de los clientes (Final)
    void receiveFromServer(String message) {

        if (message != null && !message.isEmpty()) {
            System.out.println("Mensaje recibido: " + message);
            if (firstResult && message.contains("Recibe")) {
                firstResult = false;
                cont++;
                System.out.println("PRimer if");
                message = "Master Node-" + message;
                sendToServer(message);
                return;
                // Cierra canal
            }
            cont++;
            System.out.println("Contador : "+ cont);
            if(cont == TCPServer.nodeCount){
                System.out.println("Segundo if");
                firstResult = true;
                cont = 0;
                System.out.println("Puede continuar");
            }
        }
    }

    // Envia los mensajes a los cliente
    void sendToServer(String message) {
        System.out.println("Entra sendToServer");
        if (message != null) {
            if (message.trim().contains("Generar")) {
                // Enviar mensaje a los clientes
                System.out.println("Enviando querys");
                tcpServer.sendMessageToTCPServer(message);
            } else if (message.trim().contains("Master Node")){
                System.out.println("Enviando al nodo maestro");
                tcpServer.sendMessageToTCPServer(message);
            }
            else {
                System.out.println("El mensaje no contiene la palabra 'enviar'");
            }
        }
    }

    void listening() {
        Scanner scanner = new Scanner(System.in);
        String command;

        do {
            command = scanner.nextLine();
            sendToServer(command);
        } while (!command.equals("salir"));
    }
}
