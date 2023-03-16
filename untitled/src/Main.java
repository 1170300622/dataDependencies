import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author serious
 */
public class Main {
    public static void main(String[] args) {
        Set<Set<Integer>> all = new HashSet<>();
        Set<Integer> a = new HashSet<>();
        a.add(1);
        a.add(2);
        Set<Integer> b = new HashSet<>();
        b.add(1);
        b.add(2);
        all.add(a);
        System.out.println(all.contains(b));
        System.out.println(a.equals(b));
    }
}