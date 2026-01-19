import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        String mode = "", host = "localhost", mapPath = "";
        int port = 0;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-mode")) mode = args[++i];
            else if (args[i].equals("-port")) port = Integer.parseInt(args[++i]);
            else if (args[i].equals("-map")) mapPath = args[++i];
            else if (args[i].equals("-host")) host = args[++i];
        }

        Board board = new Board(mapPath);
        board.printMyBoard();
        NetworkGame net = new NetworkGame(host, port, mode);

        List<String> myShotsPool = generateShots();
        String currentMyShot = "";

        if ("client".equalsIgnoreCase(mode)) {
            currentMyShot = myShotsPool.remove(0);
            net.sendMessage("start", currentMyShot);
        }

        while (true) {
            String raw = net.receiveMessage();
            String[] parts = raw.split(";");
            String cmd = parts[0];
            
            
            if (!currentMyShot.isEmpty() && !cmd.equals("start")) {
                board.recordMyShot(currentMyShot, cmd);
            }

            if (cmd.equals("ostatni zatopiony")) {
                System.out.println("Wygrana");
                System.out.println();
                board.printOpponentBoard(true); 
                System.out.println();
                board.printMyBoard();
                break;
            }

            String incomingCoord = (parts.length > 1) ? parts[1] : "";
            String response = board.processShot(incomingCoord);
            
            if (response.equals("ostatni zatopiony")) {
                net.sendMessage(response, "");
                System.out.println("Przegrana");
                System.out.println();
                board.printOpponentBoard(false); 
                System.out.println();
                board.printMyBoard();
                break;
            }

            currentMyShot = myShotsPool.remove(0);
            net.sendMessage(response, currentMyShot);
        }
    }

    private static List<String> generateShots() {
        List<String> shots = new ArrayList<>();
        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 10; c++) {
                shots.add(Board.getCoordString(r, c));
            }
        }
        Collections.shuffle(shots);
        return shots;
    }
}