package ServerHttp;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class Client {
    public int idAccount;
    public String balance;
    public Queue<String> querys;

    public void generateQuery(int numberOfQuerys){

        querys = new LinkedList<>();

        Random random = new Random();
        while(numberOfQuerys > 0){
            double randomQuery = Math.random();
            int randomIDSend = 1 + random.nextInt(Server.connections.size());
            if(randomQuery < 0.6) {
                querys.add("L-" + randomIDSend);
            }
            else{
                int randomIDReceive = 1 + random.nextInt(Server.connections.size());
                while(randomIDSend == randomIDReceive){
                    randomIDReceive = 1 + random.nextInt(Server.connections.size());
                }
                double mount = random.nextDouble() * 1000;
                mount = Math.round(mount * 100.0) / 100.0;
                querys.add("A-" + randomIDSend + ";" + randomIDReceive + ";" + mount);
            }
            numberOfQuerys--;
        }

    }
}


