import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Board {
    private char[][] grid = new char[10][10]; 
    private char[][] opponentView = new char[10][10]; 
    private List<Set<String>> originalShips = new ArrayList<>();
    private List<Set<String>> currentShips = new ArrayList<>();

    public Board(String mapPath) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(mapPath));
        for (int i = 0; i < 10; i++) {
            String line = lines.get(i);
            for (int j = 0; j < 10; j++) {
                grid[i][j] = line.charAt(j);
                opponentView[i][j] = '?';
            }
        }
        findShips();
    }

    private void findShips() {
        boolean[][] visited = new boolean[10][10];
        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 10; c++) {
                if (grid[r][c] == '#' && !visited[r][c]) {
                    Set<String> ship = new HashSet<>();
                    dfs(r, c, visited, ship);
                    originalShips.add(new HashSet<>(ship));
                    currentShips.add(ship);
                }
            }
        }
    }

    private void dfs(int r, int c, boolean[][] visited, Set<String> ship) {
        if (r < 0 || r >= 10 || c < 0 || c >= 10 || grid[r][c] != '#' || visited[r][c]) return;
        visited[r][c] = true;
        ship.add(getCoordString(r, c));
        dfs(r + 1, c, visited, ship);
        dfs(r - 1, c, visited, ship);
        dfs(r, c + 1, visited, ship);
        dfs(r, c - 1, visited, ship);
    }

    public String processShot(String coord) {
        if (coord.isEmpty()) return "pudło";
        int c = coord.charAt(0) - 'A';
        int r = Integer.parseInt(coord.substring(1)) - 1;

        for (int i = 0; i < originalShips.size(); i++) {
            if (originalShips.get(i).contains(coord)) {
                grid[r][c] = '@'; 
                Set<String> ship = currentShips.get(i);
                ship.remove(coord);

                if (isEverythingSunk()) return "ostatni zatopiony";
                if (ship.isEmpty()) return "trafiony zatopiony";
                return "trafiony";
            }
        }
        if (grid[r][c] != '@') grid[r][c] = '~'; 
        return "pudło";
    }

    private boolean isEverythingSunk() {
        for (Set<String> s : currentShips) {
            if (!s.isEmpty()) return false;
        }
        return true;
    }

    public void recordMyShot(String coord, String result) {
        if (coord.isEmpty()) return;
        int c = coord.charAt(0) - 'A';
        int r = Integer.parseInt(coord.substring(1)) - 1;

        if (result.contains("trafiony") || result.equals("ostatni zatopiony")) {
            opponentView[r][c] = '#';
            if (result.equals("trafiony zatopiony") || result.equals("ostatni zatopiony")) {
                markSurroundingAsEmpty(r, c); 
            }
        } else if (result.equals("pudło")) {
            opponentView[r][c] = '.';
        }
    }

    private void markSurroundingAsEmpty(int r, int c) {
        List<int[]> shipParts = new ArrayList<>();
        findConnectedSunkParts(r, c, new boolean[10][10], shipParts);
        
        for (int[] part : shipParts) {
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    int nr = part[0] + i;
                    int nc = part[1] + j;
                    if (nr >= 0 && nr < 10 && nc >= 0 && nc < 10 && opponentView[nr][nc] == '?') {
                        opponentView[nr][nc] = '.'; 
                    }
                }
            }
        }
    }

    private void findConnectedSunkParts(int r, int c, boolean[][] visited, List<int[]> parts) {
        if (r < 0 || r >= 10 || c < 0 || c >= 10 || visited[r][c] || opponentView[r][c] != '#') return;
        visited[r][c] = true;
        parts.add(new int[]{r, c});
        findConnectedSunkParts(r+1, c, visited, parts);
        findConnectedSunkParts(r-1, c, visited, parts);
        findConnectedSunkParts(r, c+1, visited, parts);
        findConnectedSunkParts(r, c-1, visited, parts);
    }

    public static String getCoordString(int r, int c) {
        return (char) ('A' + c) + "" + (r + 1);
    }

    public void printMyBoard() {
        for (int r = 0; r < 10; r++) {
            System.out.println(new String(grid[r]));
        }
    }

    public void printOpponentBoard(boolean won) {
        for (int r = 0; r < 10; r++) {
            StringBuilder sb = new StringBuilder();
            for (int c = 0; c < 10; c++) {
                char ch = opponentView[r][c];
                if (won && ch == '?') sb.append('.'); 
                else sb.append(ch);
            }
            System.out.println(sb.toString());
        }
    }
}