package ServerHttp.Prueba;

import java.io.*;
import java.net.Socket;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import ServerHttp.dto.Producto;
import ServerHttp.dao.CRUD;

class RequestProcessor implements Runnable {
    private final static Logger logger = Logger.getLogger(
    RequestProcessor.class.getCanonicalName());
    private File rootDirectory;
    private String indexFileName = "index.html";
    private Socket connection;
    
    public RequestProcessor(File rootDirectory,String indexFileName, Socket connection) {
        if (rootDirectory.isFile()) {
            throw new IllegalArgumentException(
            "rootDirectory must be a directory, not a file");
        }
        try {
            rootDirectory = rootDirectory.getCanonicalFile();
        } catch (IOException ex) {
        }
        this.rootDirectory = rootDirectory;
        if (indexFileName != null) this.indexFileName = indexFileName;
        this.connection = connection;
    }
    
    @Override
    public void run() {
        String root = rootDirectory.getPath();
        try {
            OutputStream raw = new BufferedOutputStream(
                    connection.getOutputStream()
            );
            Writer out = new OutputStreamWriter(raw);
            Reader in = new InputStreamReader(
                    new BufferedInputStream(
                            connection.getInputStream()
                    ), "US-ASCII"
            );

            StringBuilder requestLine = new StringBuilder();
            String line;
            BufferedReader reader = new BufferedReader(in);
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                requestLine.append(line).append("\r\n");
            }
            System.out.println(requestLine.toString());

            String get = requestLine.toString().split("\n")[0].trim();
            logger.info(connection.getRemoteSocketAddress() + " " + get);
            String[] tokens = get.split("\\s+");
            String method = tokens[0];
            String version = "";
            if (method.equals("GET")) {
                System.out.println("Entro al get");
                String fileName = tokens[1];
                String params = "";
                if (fileName.contains("?")) {
                    System.out.println("Entro al get con params");
                    String aux = fileName;
                    fileName = aux.split("\\?")[0];
                    params = aux.split("\\?")[1];
                    System.out.println("fileName: " + fileName);
                    System.out.println(params);
                }
                if(fileName.equals("/delete")){
                    params = params.split("\\.")[0];
                    String id = params.split("=")[1];

                    CRUD crud = new CRUD();
                    crud.deleteProducto(id);
                    fileName = "/";
                }
                System.out.println(fileName);
                boolean put = false;
                if(fileName.equals("/update")){
                    params = params.split("\\.")[0];
                    String id = params.split("=")[1];
                    fileName = "/update.html";
                    System.out.println("Entro a update");
                    System.out.println(params);
                    String contentType =
                            URLConnection.getFileNameMap().getContentTypeFor(fileName);
                    if (tokens.length > 2) {
                        version = tokens[2];
                    }
                    if(params.contains("&")){
                        System.out.println("PUT params");
                        String[] paramsArray = params.split("&");
                        String idProducto = paramsArray[0].split("=")[1];
                        String nameProducto = paramsArray[1].split("=")[1];
                        String detailProducto = paramsArray[2].split("=")[1];
                        String priceProducto = paramsArray[3].split("=")[1];
                        String stockProducto = paramsArray[4].split("=")[1];

                        CRUD crud = new CRUD();
                        Producto producto = crud.updateProducto(idProducto, nameProducto, detailProducto, priceProducto, stockProducto);
                        System.out.println(producto);
                        fileName = "/";
                        put = true;
                    }

                    if(!put){
                        CRUD crud = new CRUD();
                        Producto producto = crud.readProducto(id);
                        System.out.println(producto);

                        StringBuilder body = new StringBuilder("<HTML>\r\n")
                                .append("<HEAD><TITLE>index</TITLE>\r\n")
                                .append("</HEAD>\r\n")
                                .append("<BODY>")
                                .append("<H1>Update</H1>\r\n")
                                .append("<H2>Producto con id = "+id+"</H2>\r\n")
                                .append("<form action=\"/update\" method=\"POST\">\r\n")
                                .append("<label for=\"id\">id:</label><br>\r\n")
                                .append("<input type=\"text\" id=\"id\" name=\"id\" value=\""+producto.idProducto+"\"><br>\r\n")
                                .append("<label for=\"name\">name:</label><br>\r\n")
                                .append("<input type=\"text\" id=\"name\" name=\"name\" value=\""+producto.nameProducto+"\"><br>\r\n")
                                .append("<label for=\"detail\">detail:</label><br>\r\n")
                                .append("<input type=\"text\" id=\"detail\" name=\"detail\" value=\""+producto.detailProducto+"\"><br>\r\n")
                                .append("<label for=\"price\">price:</label><br>\r\n")
                                .append("<input type=\"text\" id=\"price\" name=\"price\" value=\""+producto.priceProducto+"\"><br>\r\n")
                                .append("<label for=\"stock\">stock:</label><br>\r\n")
                                .append("<input type=\"text\" id=\"stock\" name=\"stock\" value=\""+producto.stockProducto+"\"><br><br>\r\n")
                                .append("<input type=\"submit\" value=\"Submit\">\r\n")
                                .append("</form>\r\n")
                                .append("</BODY></HTML>\r\n");
                        String bodyStr = body.toString();

                        if (version.startsWith("HTTP/")) { // send a MIME header
                            sendHeader(out, "HTTP/1.0 404 File Not Found",
                                    "text/html; charset=utf-8", bodyStr.length());
                        }
                        byte[] theData = bodyStr.getBytes("UTF-8");
                        raw.write(theData);
                        raw.flush();
                    }

                }

                if (fileName.endsWith("/")) fileName += indexFileName;
                String contentType =
                URLConnection.getFileNameMap().getContentTypeFor(fileName);
                if (tokens.length > 2) {
                    version = tokens[2];
                }
                File theFile = new File(rootDirectory,fileName.substring(1, fileName.length()));
                if (theFile.canRead()
                // Don't let clients outside the document root
                && theFile.getCanonicalPath().startsWith(root)) {

                    StringBuilder body = new StringBuilder("<HTML>\r\n")
                            .append("<HEAD><TITLE>index</TITLE>\r\n")
                            .append("</HEAD>\r\n")
                            .append("<BODY>")
                            .append("<H1>Almacen</H1>\r\n")
                            .append("<H2>Mostrar</H2>\r\n")
                            .append("<table>\r\n")
                            .append("<tr>\r\n")
                            .append("<th>id</th>\r\n")
                            .append("<th>name</th>\r\n")
                            .append("<th>detail</th>\r\n")
                            .append("<th>price</th>\r\n")
                            .append("<th>stock</th>\r\n")
                            .append("</tr>\r\n")
                            .append("<tr>\r\n");
                    // leer baseDatosAlmacen.txt
                    try {
                        FileReader fr = new FileReader("ServerHttp/database/baseDatosAlmacen.txt");
                        BufferedReader br = new BufferedReader(fr);
                        String line2;
                        while ((line2 = br.readLine()) != null) {
                            String[] data = line2.split("-");
                            body.append("<td>").append(data[0]).append("</td>\r\n")
                                    .append("<td>").append(data[1]).append("</td>\r\n")
                                    .append("<td>").append(data[2]).append("</td>\r\n")
                                    .append("<td>").append(data[3]).append("</td>\r\n")
                                    .append("<td>").append(data[4]).append("</td>\r\n")
                                    .append("<td><button type=\"button\" onclick=\"location.href='/update?id="+data[0]+"'\">Update</button></td>\r\n")
                                    .append("<td><button type=\"button\" onclick=\"location.href='/delete?id="+data[0]+"'\">Delete</button></td>\r\n")
                                    .append("</tr>\r\n");
                        }
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                        String bodyStr = body.append("</table>\r\n")
                                .append("<H2>Create</H2>\r\n")
                                .append("<form action=\"/create\" method=\"POST\">\r\n")
                                .append("<label for=\"id\">id:</label><br>\r\n")
                                .append("<input type=\"text\" id=\"id\" name=\"id\"><br>\r\n")
                                .append("<label for=\"name\">name:</label><br>\r\n")
                                .append("<input type=\"text\" id=\"name\" name=\"name\"><br>\r\n")
                                .append("<label for=\"detail\">detail:</label><br>\r\n")
                                .append("<input type=\"text\" id=\"detail\" name=\"detail\"><br>\r\n")
                                .append("<label for=\"price\">price:</label><br>\r\n")
                                .append("<input type=\"text\" id=\"price\" name=\"price\"><br>\r\n")
                                .append("<label for=\"stock\">stock:</label><br>\r\n")
                                .append("<input type=\"text\" id=\"stock\" name=\"stock\"><br><br>\r\n")
                                .append("<input type=\"submit\" value=\"Submit\">\r\n")
                                .append("</form>\r\n")
                            .append("</BODY></HTML>\r\n").toString();
                    if (version.startsWith("HTTP/")) { // send a MIME header
                        sendHeader(out, "HTTP/1.0 404 File Not Found",
                                "text/html; charset=utf-8", bodyStr.length());
                    }
                    byte[] theData = bodyStr.getBytes("UTF-8");
                    raw.write(theData);
                    raw.flush();


                } else { // can't find the file
                    String body = new StringBuilder("<HTML>\r\n")
                    .append("<HEAD><TITLE>File Not Found</TITLE>\r\n")
                    .append("</HEAD>\r\n")
                    .append("<BODY>")
                    .append("<H1>HTTP Error 404: File Not Found</H1>\r\n")
                    .append("</BODY></HTML>\r\n").toString();
                    if (version.startsWith("HTTP/")) { // send a MIME header
                    sendHeader(out, "HTTP/1.0 404 File Not Found",
                    "text/html; charset=utf-8", body.length());
                    }
                    byte[] theData = body.getBytes("UTF-8");
                    raw.write(theData);
                    raw.flush();
                }
            } else if (method.equals("POST")) {
                int contentLength = 0;
                String[] requestLines = requestLine.toString().split("\n");
                for (String request : requestLines) {
                    if (request.startsWith("Content-Length:")) {
                        contentLength = Integer.parseInt(request.split(":")[1].trim());
                        break;
                    }
                }
                System.out.println(requestLine);


                char[] postData = new char[contentLength];
                int bytesRead = reader.read(postData, 0, contentLength);
                System.out.println(bytesRead);
            
                String postDataString = new String(postData);
                System.out.println("Received POST data: " + new String(postData));
                String[] paramsArray = postDataString.split("&");
                String idProducto = paramsArray[0].split("=")[1];
                String nameProducto = paramsArray[1].split("=")[1];
                String detailProducto = paramsArray[2].split("=")[1];
                String priceProducto = paramsArray[3].split("=")[1];
                String stockProducto = paramsArray[4].split("=")[1];

                CRUD crud = new CRUD();
                Producto producto;
                if(requestLine.toString().contains(("update"))){
                    producto = crud.updateProducto(idProducto, nameProducto, detailProducto, priceProducto, stockProducto);
                    System.out.println(producto);
                } else if (requestLine.toString().contains(("create"))) {
                    producto = crud.createProducto(idProducto, nameProducto, detailProducto, priceProducto, stockProducto);
                    System.out.println(producto);
                }





                String fileName = "/index.html";
                String contentType =
                        URLConnection.getFileNameMap().getContentTypeFor(fileName);
                if (tokens.length > 2) {
                    version = tokens[2];
                }
                File theFile = new File(rootDirectory,fileName.substring(1, fileName.length()));
                if (theFile.canRead()
                        // Don't let clients outside the document root
                        && theFile.getCanonicalPath().startsWith(root)) {





                    StringBuilder body = new StringBuilder("<HTML>\r\n")
                            .append("<HEAD><TITLE>index</TITLE>\r\n")
                            .append("</HEAD>\r\n")
                            .append("<BODY>")
                            .append("<H1>Almacen</H1>\r\n")
                            .append("<H2>Mostrar</H2>\r\n")
                            .append("<table>\r\n")
                            .append("<tr>\r\n")
                            .append("<th>id</th>\r\n")
                            .append("<th>name</th>\r\n")
                            .append("<th>detail</th>\r\n")
                            .append("<th>price</th>\r\n")
                            .append("<th>stock</th>\r\n")
                            .append("</tr>\r\n")
                            .append("<tr>\r\n");
                    // leer baseDatosAlmacen.txt
                    try {
                        FileReader fr = new FileReader("ServerHttp/database/baseDatosAlmacen.txt");
                        BufferedReader br = new BufferedReader(fr);
                        String line2;
                        while ((line2 = br.readLine()) != null) {
                            String[] data = line2.split("-");
                            body.append("<td>").append(data[0]).append("</td>\r\n")
                                    .append("<td>").append(data[1]).append("</td>\r\n")
                                    .append("<td>").append(data[2]).append("</td>\r\n")
                                    .append("<td>").append(data[3]).append("</td>\r\n")
                                    .append("<td>").append(data[4]).append("</td>\r\n")
                                    .append("<td><button type=\"button\" onclick=\"location.href='/update?id="+data[0]+"'\">Update</button></td>\r\n")
                                    .append("<td><button type=\"button\" onclick=\"location.href='/delete?id="+data[0]+"'\">Delete</button></td>\r\n")
                                    .append("</tr>\r\n");
                        }
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    String bodyStr = body.append("</table>\r\n")
                            .append("<H2>Create</H2>\r\n")
                            .append("<form action=\"/create\" method=\"POST\">\r\n")
                            .append("<label for=\"id\">id:</label><br>\r\n")
                            .append("<input type=\"text\" id=\"id\" name=\"id\"><br>\r\n")
                            .append("<label for=\"name\">name:</label><br>\r\n")
                            .append("<input type=\"text\" id=\"name\" name=\"name\"><br>\r\n")
                            .append("<label for=\"detail\">detail:</label><br>\r\n")
                            .append("<input type=\"text\" id=\"detail\" name=\"detail\"><br>\r\n")
                            .append("<label for=\"price\">price:</label><br>\r\n")
                            .append("<input type=\"text\" id=\"price\" name=\"price\"><br>\r\n")
                            .append("<label for=\"stock\">stock:</label><br>\r\n")
                            .append("<input type=\"text\" id=\"stock\" name=\"stock\"><br><br>\r\n")
                            .append("<input type=\"submit\" value=\"Submit\">\r\n")
                            .append("</form>\r\n")
                            .append("</BODY></HTML>\r\n").toString();
                    if (version.startsWith("HTTP/")) { // send a MIME header
                        sendHeader(out, "HTTP/1.0 404 File Not Found",
                                "text/html; charset=utf-8", bodyStr.length());
                    }
                    byte[] theData = bodyStr.getBytes("UTF-8");
                    raw.write(theData);
                    raw.flush();
                }


            } else if (method.equals("PUT")) {
                int contentLength = 0;
                String[] requestLines = requestLine.toString().split("\n");
                for (String request : requestLines) {
                    if (request.startsWith("Content-Length:")) {
                        contentLength = Integer.parseInt(request.split(":")[1].trim());
                        break;
                    }
                }
                System.out.println(requestLine);

                char[] putData = new char[contentLength];
                int bytesRead = reader.read(putData, 0, contentLength);
                System.out.println(bytesRead);
                String putDataString = new String(putData);
                System.out.println("Received PUT data: " + new String(putData));
                String[] paramsArray = putDataString.split("&");
                String idProducto = paramsArray[0].split("=")[1];
                String nameProducto = paramsArray[1].split("=")[1];
                String detailProducto = paramsArray[2].split("=")[1];
                String priceProducto = paramsArray[3].split("=")[1];
                String stockProducto = paramsArray[4].split("=")[1];

                CRUD crud = new CRUD();
                Producto producto = crud.updateProducto(idProducto, nameProducto, detailProducto, priceProducto, stockProducto);
                System.out.println(producto);

            } else {
                String body = new StringBuilder("<HTML>\r\n")
                .append("<HEAD><TITLE>Not Implemented</TITLE>\r\n")
                .append("</HEAD>\r\n")
                .append("<BODY>")
                .append("<H1>HTTP Error 501: Not Implemented</H1>\r\n")
                .append("</BODY></HTML>\r\n").toString();
                if (version.startsWith("HTTP/")) { // send a MIME header
                    sendHeader(out, "HTTP/1.0 501 Not Implemented",
                    "text/html; charset=utf-8", body.length());
                }
                out.write(body);
                out.flush();
            }
        } catch (IOException ex) {
            logger.log(Level.WARNING,
            "Error talking to " + connection.getRemoteSocketAddress(), ex);
        } finally {
            try {
                connection.close();
            }
            catch (IOException ex) {
            }
        }
    }

    private void sendHeader(Writer out, String responseCode,String contentType, int length) throws IOException {
        out.write(responseCode + "\r\n");
        Date now = new Date();
        out.write("Date: " + now + "\r\n");
        out.write("Server: JHTTP 2.0\r\n");
        out.write("Content-length: " + length + "\r\n");
        out.write("Content-type: " + contentType + "\r\n\r\n");
        out.flush();
    }
}
