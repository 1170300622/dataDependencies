import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author serious
 */
public class Main {
    public static void main(String[] args) throws IOException {
//        Set<Set<Integer>> all = new HashSet<>();
//        Set<Integer> a = new HashSet<>();
//        a.add(1);
//        a.add(2);
//        Set<Integer> b = new HashSet<>();
//        b.add(1);
//        b.add(2);
//        all.add(a);
//        System.out.println(all.contains(b));
//        System.out.println(a.equals(b));
//
//
//
        Set<Integer> predicates = new HashSet<>();
        for (int i = 30; i > 0; i--) {
            predicates.add(i);
        }
        long time0 = System.currentTimeMillis();
        new Main().dfs(predicates, new TreeSet<>());
        System.out.println((System.currentTimeMillis() - time0) / (double)1000 + "s");
    }
    Set<Set<Integer>> all = new HashSet<>();
    public void dfs(Set<Integer> predicates, TreeSet<Integer> currentPredicates) throws IOException {
//        if (all.size() % 1000 == 0) {
//            System.out.println(all.size());
//        }
        if (currentPredicates.size() >= 6) {
            return;
        }
        Set<Integer> newPre = new HashSet<>(predicates);
        for (Integer i : predicates) {
            if (currentPredicates.size() > 0 && currentPredicates.last() > i) {
                continue;
            }
            currentPredicates.add(i);
//            if (all.contains(currentPredicates)) {continue;}
            if (all.contains(currentPredicates)) {
                System.out.println(all.toString());
            }
            all.add(new HashSet<>(currentPredicates));
//            System.out.println(all.size());
            newPre.remove(i);
            dfs(newPre, currentPredicates);
            currentPredicates.remove(i);
            newPre.add(i);
        }
    }


}