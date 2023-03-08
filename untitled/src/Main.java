import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

/**
 * @author serious
 */
public class Main {
    public static void main(String[] args) {
        BufferedWriter writer = null;
        try {
            FileWriter file = new FileWriter("data/StockArrayB.txt");
            writer = new BufferedWriter(file);
            writer.write("321");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
}