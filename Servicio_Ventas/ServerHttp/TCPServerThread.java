package ServerHttp;

import java.io.*;
import java.net.Socket;

public class TCPServerThread extends Thread {

    private final Socket node;
    private final TCPServer tcpServer;
    public int nodeID;
    public PrintWriter out;
    public BufferedReader in;
    TCPServerThread[] connectedClients;

    public TCPServerThread(Socket node, TCPServer tcpServer, int nodeID, TCPServerThread[] connectedClients) {
        this.node = node;
        this.tcpServer = tcpServer;
        this.nodeID = nodeID;
        this.connectedClients = connectedClients;
    }

    public void run() {
        try {
            try {
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(node.getOutputStream())), true);
                TCPServer.OnMessageReceived messageListener = tcpServer.getMessageListener();
                in = new BufferedReader(new InputStreamReader(node.getInputStream()));
                while (true) {
                    String message = in.readLine();
                    if (message != null && messageListener != null) {
                        messageListener.messageReceived(message);
                    }
                }
            } catch (Exception e) {
                System.out.println("TCP ServerHttp.Server: Error " + e);
            } finally {
                node.close();
            }

        } catch (Exception e) {
            System.out.println("TCP ServerHttp.Server: Error " + e);
        }
    }

    public void sendMessage(String message) {
        if (out != null && !out.checkError()) {
            out.println(message);
            out.flush();
        }
    }

    public void stopClient() {
        try {
            node.close();
        } catch (Exception e) {
            System.out.println("TCP ServerHttp.Server: Error " + e);
        }
    }
}
