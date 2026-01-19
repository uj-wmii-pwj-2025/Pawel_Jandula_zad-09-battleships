import static org.junit.Assert.*;
import org.junit.Test;

public class BoardTest {
    @Test
    public void testShotLogic() throws Exception {
        Board board = new Board("test_map.txt");
        String result = board.processShot("A1");
        assertTrue(result.equals("trafiony") || result.equals("trafiony zatopiony") || result.equals("ostatni zatopiony"));
    }
}