package ServerHttp;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class TCPServer {

    public static int nodeCount = 0;
    public static final int SERVER_PORT = CONSTANTS.PORT;
    private final OnMessageReceived messageListener;
    public static final TCPServerThread[] connectedNodes = new TCPServerThread[10];
    private boolean generateQuerys = false;
    public TCPServer(OnMessageReceived messageListener) {
        this.messageListener = messageListener;
    }
    public OnMessageReceived getMessageListener() {
        return this.messageListener;
    }

    boolean toMaster = false;
    public void sendMessageToTCPServer(String message) {
        if(message.contains("Generar")) {
            generateQuerys = true;
        }

        if(message.contains("Master Node")){
            toMaster = true;
        }


        if(generateQuerys) {
            generateQuerys = false;
            int v = Integer.parseInt(message.split("-")[1]);
            List<Thread> clientThreads = new ArrayList<>();

            for (Client c : Server.connections) {
                Thread t = new Thread(() -> {
                    c.generateQuery(v);
                });
                t.start();
                clientThreads.add(t);
            }

            for (Thread t : clientThreads) {
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            String fileName = "Archivos/blockchaincuentas.txt";
            try{
                FileWriter fileWriter = new FileWriter(fileName);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                System.out.println("Escribiendo en el archivo " + fileName);

                for (Client client: Server.connections) {
                    Queue<String> q = client.querys;
                    while(!q.isEmpty()) {
                        String query = q.poll();
                        if(isValid(query)){
                            bufferedWriter.write(Server.numberQuery + "-" + query);
                            Server.numberQuery++;
                            bufferedWriter.newLine();
                        }
                    }
                }
                bufferedWriter.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            System.out.println("Generando copias para los nodos");


            copyFile("cuentas.txt");
            copyFile("blockchaincuentas.txt");
            // Copia cuentas.txt en el directorio Copias


            System.out.println("Enviando nodos");
            for (int i = 1; i <= nodeCount; i++) {
                if(i == 1) connectedNodes[i].sendMessage("Master node-1");
                else connectedNodes[i].sendMessage("Slave node-" + i);
            }
        }

        if(toMaster){
            toMaster = false;
            String[] parts = message.split("-");
            String query = parts[2];
            String[] parts2 = query.split(":");
            String nodeID = parts2[0];
            String blockHash = parts2[1];
            String timeExecution = parts2[2];
            String merkleRoot = parts2[3];

            String fileName = "Archivos/Chain.txt";

            String previousHash = "";

            try {
                FileReader fr = new FileReader(fileName);
                BufferedReader br = new BufferedReader(fr);
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts3 = line.split(":");
                    String findHashWord = parts3[0];
                    if (findHashWord.equals("Hash")) {
                        previousHash = parts3[1];
                    }
                }
                fr.close();
            } catch (Exception e) {
                System.out.println("Error: " + e);
            }

            try {
                FileWriter fileWriter = new FileWriter(fileName, true);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                System.out.println("Escribiendo en el archivo " + fileName);
                bufferedWriter.write("Hash:" + blockHash);
                bufferedWriter.newLine();
                bufferedWriter.write("+node:"+nodeID);
                bufferedWriter.newLine();
                bufferedWriter.write("+previousHash:" + previousHash);
                bufferedWriter.newLine();
                bufferedWriter.write("+timestamp:" + timeExecution);
                bufferedWriter.newLine();
                bufferedWriter.write("+merkleRoot:" + merkleRoot);
                bufferedWriter.newLine();
                bufferedWriter.newLine();
                bufferedWriter.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        }
    }

    private boolean isValid(String query){
        char typeQuery = query.charAt(1);
        String[] parts = query.split("-");
        if(typeQuery == 'L') {
            double mount = getMount(parts[1]);
        }
        else if(typeQuery == 'A') {
            return updateTransaction(parts[1]);
        }
        return true;
    }

    double getMount(String stringAccountID){
        int accountID = Integer.parseInt(stringAccountID);
        double mount = -1;
        String fileName = "Archivos/cuentas.txt";
        try{

            FileReader fr = new FileReader(fileName);
            BufferedReader br = new BufferedReader(fr);
            String line;

            while((line = br.readLine()) != null) {
                int accountAux = Integer.parseInt(line.split(":")[0]);
                if (accountID == accountAux) {
                    mount = Double.parseDouble(line.split(":")[1]);
                    return mount;
                }
            }

            fr.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return mount;
    }

    boolean updateTransaction(String query){
        String[] parts = query.split(";");
        int idAccountSend = Integer.parseInt(parts[0]);
        int idAccountReceive = Integer.parseInt(parts[1]);
        double mountToTransfer = Integer.parseInt(parts[2]);
        double mountidAccountSend = getMount(parts[0]);

        if (mountidAccountSend < mountToTransfer) {
            return false;
        }


        // Actualizar el txt
        String fileName = "Archivos/cuentas.txt";
        List<String> lines = readFileContent(fileName);

        for (int i = 0; i < lines.size(); i++) {
            String[] partsLine = lines.get(i).split(":");
            int accountID = Integer.parseInt(partsLine[0]);
            if (accountID == idAccountSend) {
                double mount = Double.parseDouble(partsLine[1]);
                mount -= mountToTransfer;
                lines.set(i, accountID + ": " + mount);
            }
            else if (accountID == idAccountReceive) {
                double mount = Double.parseDouble(partsLine[1]);
                mount += mountToTransfer;
                lines.set(i, accountID + ": " + mount);
            }
        }
        writeFileContent(fileName, lines);
        return true;
    }

    private List<String> readFileContent(String fileName) {
        List<String> lines = new ArrayList<>();
        try {
            FileReader fr = new FileReader(fileName);
            BufferedReader br = new BufferedReader(fr);
            String line;

            while((line = br.readLine()) != null) {
                lines.add(line);
            }

            fr.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return lines;
    }

    private void writeFileContent(String fileName, List<String> lines) {
        try {
            FileWriter fw = new FileWriter(fileName);
            BufferedWriter bw = new BufferedWriter(fw);
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
            bw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void run() {
        System.out.println(SERVER_PORT);
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)){
            while (true) {
                Socket client = serverSocket.accept();
                nodeCount++;
                System.out.println("Clientes totales: " + nodeCount);
                connectedNodes[nodeCount] = new TCPServerThread(client, this, nodeCount, connectedNodes);
                connectedNodes[nodeCount].start();
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void stopServer() {
        for (int i = 1; i <= nodeCount; i++) {
            connectedNodes[i].stopClient();
        }
    }

    public interface OnMessageReceived {
        void messageReceived(String message);
    }

    void copyFile(String filename) {
        String fileNameOriginal = "Archivos/" + filename;
        for (int i = 1; i <= nodeCount; i++) {
            String fileNameCopy = "Archivos/Copias/" + i + filename;
            try{
                FileReader fr = new FileReader(fileNameOriginal);
                FileWriter fw =  new FileWriter(fileNameCopy);

                char []buffer = new char[1024];
                int length;

                while((length = fr.read(buffer)) != -1) {
                    fw.write(buffer, 0, length);
                }
                fw.close();
                fr.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

