import java.io.*;
import java.net.*;

public class NetworkGame {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String lastSent;

    public NetworkGame(String host, int port, String mode) throws IOException {
        if ("server".equalsIgnoreCase(mode)) {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Czekam na połączenie...");
            this.socket = serverSocket.accept();
            serverSocket.close();
        } else {
            this.socket = new Socket(host, port);
        }
        this.socket.setSoTimeout(1000); 
        this.out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
    }

    public void sendMessage(String command, String coord) {
        String msg = command + (coord.isEmpty() ? "" : ";" + coord);
        this.lastSent = msg;
        out.println(msg);
        System.out.println("Wysłano: " + msg);
    }

    public String receiveMessage() throws IOException {
        int retries = 0;
        while (retries < 3) {
            try {
                String line = in.readLine();
                if (line != null) {
                    System.out.println("Otrzymano: " + line);
                    return line;
                }
            } catch (SocketTimeoutException e) {
                retries++;
                if (retries < 3) {
                    System.out.println("Timeout, ponawiam próbę (" + retries + "/3)...");
                    out.println(lastSent); 
                }
            }
        }
        System.out.println("Błąd komunikacji");
        System.exit(1);
        return null;
    }
}