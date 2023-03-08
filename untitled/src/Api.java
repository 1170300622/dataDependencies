import java.util.*;

/**
 * @author serious
 */
public class Api {

    public void readCsvFile(String fileName) {

    }

    public void getPredicates() {

    }

    public static void main(String[] args) {
        List<TreeMap<Double, List<Integer>>> pliNumerical = new ArrayList<>();
        pliNumerical.add(new TreeMap<>(Comparator.reverseOrder()));
        pliNumerical.get(0).put(1.1, new ArrayList<>());
        pliNumerical.get(0).get(1.1).add(55);

        pliNumerical.get(0).put(1.2, new ArrayList<>());
        pliNumerical.get(0).get(1.2).add(23);
        Iterator<Map.Entry<Double, List<Integer>>> entryIterator = pliNumerical.get(0).entrySet().iterator();
        while (entryIterator.hasNext()) {
            Map.Entry<Double, List<Integer>> entry = entryIterator.next();
            System.out.println(entry.getKey() + "  " + entry.getValue()  );
        }
    }
}
