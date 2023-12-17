package ServerHttp.Prueba;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Scanner;

public class CONSENSO {
    public static String sha1(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String[] sha1WithDifficulty(String input, int difficulty) {
        String[] output = new String[2];
        long startTime;
        long endTime;
        startTime = System.currentTimeMillis();
        while(true) {
            String nonce = String.valueOf((int) (Math.random() * 1000000000));
            String newInput = input + nonce;
            System.out.println(input + " + " + nonce + " = " + newInput);
            String inputHash = sha1(newInput);
            System.out.println("Hash " + inputHash);

            assert inputHash != null;
            if (inputHash.startsWith("0".repeat(difficulty))) {
                System.out.println("Nonce: " + nonce);
                System.out.println("Hash: " + inputHash);
                output[0] = inputHash;

                endTime = System.currentTimeMillis();
                System.out.println("Tiempo: " + (endTime - startTime) + " ms");
                output[1] = String.valueOf(endTime - startTime);
                return output;
            }
        }
    }

    public static String findMerkleRoot(List<String> transactions) {
        if (transactions.size() == 1) {
            return sha1(transactions.get(0));
        }
        int n = transactions.size();
        int rest = findRestPower2(n);
        String[] newTransactions = new String[n + rest];
        for (int i = 0; i < n; i++) {
            newTransactions[i] = transactions.get(i);
        }
        for (int i = n; i < newTransactions.length; i++) {
            newTransactions[i] = "nil";
        }

        int numberOfRoots = n;

        while (numberOfRoots > 1) {
            String[] newRoots = new String[numberOfRoots / 2];
            for (int i = 0; i < numberOfRoots; i += 2) {
                newRoots[i / 2] = sha1(newTransactions[i] + newTransactions[i + 1]);
            }
            numberOfRoots /= 2;
            newTransactions = newRoots;
        }

        return newTransactions[0];
    }

    static int findRestPower2(int n) {
        int i = 0;
        while (Math.pow(2, i) < n) {
            i++;
        }
        return (int) Math.pow(2, i) - n;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String input = "03-A-1012;1110;579.83";
        System.out.print("Ingresa la dificultad: ");
        int difficulty = sc.nextInt();
        long startTime;
        long endTime;
        startTime = System.currentTimeMillis();

        while(true) {
            String nonce = String.valueOf((int) (Math.random() * 1000000000));
            String newInput = input + nonce;
            System.out.println(input + " + " + nonce + " = " + newInput);
            String inputHash = sha1(newInput);
            System.out.println("Hash " + inputHash);

            if (inputHash.startsWith("0".repeat(difficulty))) {
                System.out.println("Nonce: " + nonce);
                System.out.println("Hash: " + inputHash);
                break;
            }
        }
        endTime = System.currentTimeMillis();
        System.out.println("Tiempo: " + (endTime - startTime) + " ms");
    }
}
