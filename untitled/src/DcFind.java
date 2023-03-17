import java.io.*;
import java.util.*;

/**
 * @author serious
 */
public class DcFind {
    /**
     * Attributes inverted index.
     */
    Map<Integer, Integer> pliIndex = new HashMap<>();
    List<Map<String, List<Integer>>> pliString = new ArrayList<>();
    List<TreeMap<Double, List<Integer>>> pliNumerical = new ArrayList<>();
    List<Map<String, List<Integer>>> pli = new ArrayList<>();
    int row, col;
    //    final int hashMapInitialCapacity = 10;
    Set<Integer>[] arrayB;
    String[] attributeNameArray;
    String[] comparison = {"=", "!=", ">", ">=", "<", "<="};
    int[] selectivity;
    Map<Integer, Set<Integer>> predicateEvidence = new HashMap<Integer, Set<Integer>>();
    public static void main(String[] args) {
        DcFind dcFind = new DcFind();
        List<String[]> input = new ArrayList<>();
        boolean[] types = dcFind.readFile("data/Stock-60000.csv", input);
//        for (int i = 0; i < tdc.col; i++) {
//            System.out.println(tdc.attributeNameArray[i] + " " + types[i]);
//        }
//        System.out.println(input.get(0)[0] + " " + input.get(0)[6]);
        long timeStart = System.currentTimeMillis();
        dcFind.createRealPli(input, types);
        long timePli = System.currentTimeMillis();
        System.out.println("create Value Index: " + (timePli-timeStart) + "ms");
        dcFind.createArrayB(input, types);
        long timeArrayB = System.currentTimeMillis();
        System.out.println("create Array: " + (timeArrayB - timePli)/((double)1000) + "s");
        dcFind.findAllDc(0, types);
        long timeFind = System.currentTimeMillis();
        System.out.println("find DC: " + (timeFind - timeArrayB)/((double) 60000) + "min");
//        System.out.println(res.size());
//        tdc.outputDc(res);
    }


    public boolean[] readFile(String fileName, List<String[]> input) {
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(fileName));
            String[] firstLine = reader.readLine().split(",");

            col = firstLine.length;
            attributeNameArray = new String[col];
            boolean[] types = new boolean[col];
            // Confirm the name and type
            for (int i = 0; i < col; i++) {
                String[] currentCol = firstLine[i].split("\\(");
                attributeNameArray[i] = currentCol[0];
                if (currentCol[1].charAt(0) == 'S') {
                    types[i] = true;
                }
            }
            row = 0;
            String nowLine = new String();
            while ((nowLine = reader.readLine()) != null) {
                input.add(nowLine.split(","));
                row++;
            }
            selectivity = new int[col * 6];
            return types;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return new boolean[0];
    }

    /**
     * Create attributes inverted index(different types).
     * @author serious
     * @param input
     * @param types: attribute type
     */
    public void createRealPli(List<String[]> input, boolean[] types) {
        // string pli index and num pli index.
        int indexString = 0, indexNumerical = 0;
        for (int i = 0; i < col; i++) {
            // Current column pli.
            if (!types[i]) {

                pliNumerical.add(new TreeMap<>(Comparator.reverseOrder()));
                pliIndex.put(i, indexNumerical);
                for (int j = 0; j < row; j++) {
                    Double nowKey = Double.valueOf(input.get(j)[i]);
                    if (pliNumerical.get(indexNumerical).keySet().contains(nowKey)) {
                        pliNumerical.get(indexNumerical).get(nowKey).add(j);
                    } else {
                        List<Integer> newList = new ArrayList<>();
                        newList.add(j);
                        pliNumerical.get(indexNumerical).put(nowKey, newList);
                    }
                }
                indexNumerical++;
            } else {
                pliString.add(new HashMap<>());
                pliIndex.put(i, indexString);
                for (int j = 0; j < row; j++) {
                    String nowKey = input.get(j)[i];
                    if (pliString.get(indexString).keySet().contains(nowKey)) {
                        pliString.get(indexString).get(nowKey).add(j);
                    } else {
                        List<Integer> newList = new ArrayList<>();
                        newList.add(j);
                        pliString.get(indexString).put(nowKey, newList);
                    }

                }
                indexString++;
            }

        }
    }



    public void writeFileArrayB() {
        BufferedWriter writer = null;
        try {
            System.out.println("Start write file");
            FileWriter file = new FileWriter("data/StockArrayB.txt");
            writer = new BufferedWriter(file);
            for (Set<Integer> set : arrayB) {
                List<Integer> list = new ArrayList<>(set);
                Collections.sort(list);
                writer.write(list.toString());
                writer.newLine();
                writer.flush();
            }
            System.out.println("ArrayB成功写入文件");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @author serious
     * @param input
     * @param types whether the value is a character type
     */
    public void createArrayB(List<String[]> input, boolean[] types) {
        // Evidence initialization
        arrayB = new Set[row * row];

        for (int j = 0; j < row * row; j++) {
            Set<Integer> eAhead = new HashSet<>();
            for (int i = 0; i < col; i++) {
                eAhead.add(i * 6 + 1);
                if (!types[i]) {
                    eAhead.add(i * 6 + 4);
                    eAhead.add(i * 6 + 5);
                }
            }
            arrayB[j] = eAhead;
        }
        for (int i = 0; i < col; i++) {
            // is String
            if (types[i]) {
                Iterator<Map.Entry<String, List<Integer>>> entryIterator = pliString.get(pliIndex.get(i)).entrySet().iterator();
                while (entryIterator.hasNext()) {
                    Map.Entry<String, List<Integer>> entry = entryIterator.next();
                    // same predicate
                    List<Integer> sameValueList = entry.getValue();
                    for (Integer index0 : sameValueList) {
                        for (Integer index1 : sameValueList) {
                            if (index0.equals(index1)) {
                                continue;
                            }
                            int realIndex = index0 * row + index1;
                            // add same predicate
                            arrayB[realIndex].add(i * 6);
                            // remove different predicate
                            arrayB[realIndex].remove(i * 6 + 1);
                        }
                    }
                }
            } else {
                Iterator<Map.Entry<Double, List<Integer>>> entryIterator = pliNumerical.get(pliIndex.get(i)).entrySet().iterator();
                List<Integer> tmpSet = new ArrayList<>();
                // greater predicate
                while (entryIterator.hasNext()) {
                    Map.Entry<Double, List<Integer>> entry = entryIterator.next();
                    for (Integer index1 : entry.getValue()) {
                        for (Integer index0 : tmpSet) {
                            int realIndex = index0 * row + index1;

                            arrayB[realIndex].add(i * 6 + 2);
                            arrayB[realIndex].add(i * 6 + 3);
                            arrayB[realIndex].remove(i * 6 + 4);
                            arrayB[realIndex].remove(i * 6 + 5);

                        }
                        for (Integer index2 : entry.getValue()) {
                            if (index1.equals(index2)) {
                                continue;
                            }
                            int realIndex1 = index1 * row + index2;
                            arrayB[realIndex1].add(i * 6);
                            arrayB[realIndex1].remove(i * 6 + 1);

                        }
                    }
                    tmpSet.addAll(entry.getValue());
                }

            }
        }

//        writeFileArrayB();
        countSelectivity(types);
    }

    public void countSelectivity(boolean[] types) {
        int index = -1;
        for (int i = 0; i < col; i++) {
            predicateEvidence.put(i * 6, new HashSet<Integer>());
            predicateEvidence.put(i * 6 + 1, new HashSet<Integer>());
            if (!types[i]) {
                predicateEvidence.put(i * 6 + 2, new HashSet<Integer>());
                predicateEvidence.put(i * 6 + 3, new HashSet<Integer>());
                predicateEvidence.put(i * 6 + 4, new HashSet<Integer>());
                predicateEvidence.put(i * 6 + 5, new HashSet<Integer>());
            }
        }
        for (Set<Integer> set : arrayB) {
            index++;
            if (index % row == index / row) {
                continue;
            }
            for (Integer predicate : set) {
                selectivity[predicate]++;
                predicateEvidence.get(predicate).add(index);
            }
        }
    }

    /**
     *
     * @author serious
     * @param approximate degree
     * @param types
     * @return
     */
    public Set<Set<Integer>> findAllDc(double approximate, boolean[] types) {
        Set<Set<Integer>> allMinimalCover = new HashSet<>();
        // Time predicate
        Set<Integer> otherPredicate = new HashSet<Integer>();
        for (int i = 0; i < col; i++) {
            otherPredicate.add(i * 6);
            otherPredicate.add(i * 6 + 1);
            if (!types[i]) {
                otherPredicate.add(i * 6 + 2);
                otherPredicate.add(i * 6 + 3);
                otherPredicate.add(i * 6 + 4);
                otherPredicate.add(i * 6 + 5);
            }
        }

        List<int[]> orderedPre = new ArrayList<int[]>();
        for (Integer predicate : otherPredicate) {
            orderedPre.add(new int[]{predicate, selectivity[predicate]});
        }
        Collections.sort(orderedPre, (int[] num1, int[] num2)->(num2[1] - num1[1]));
        int approximateNumber =  (int)(approximate * row * (row - 1));

        Set<Integer> isFind = new HashSet<>();
        System.out.println("近似数：" + approximateNumber);
        TreeSet<Integer> currentPredicate = new TreeSet<>();
        for (int i = 1; i < row * row; i++) {
            if (i % row != i / row) { isFind.add(i); }
        }
        Set<Set<Integer>> allRoads = new HashSet<>();
        findCover(currentPredicate, isFind, otherPredicate, orderedPre, allMinimalCover, approximateNumber, 4);

        System.out.println(allMinimalCover.size());
        return allMinimalCover;
    }
    public void findCover(TreeSet<Integer> currentPredicate, Set<Integer> isFind, Set<Integer> otherPredicate, List<int[]> orderedPre, Set<Set<Integer>> allMinimalCover, int approximate, int maxPreSize) {
        // Satisfy the approximation.
        if (isFind.size() <= approximate && !isSubset(allMinimalCover, currentPredicate)) {
            allMinimalCover.add(new TreeSet<>(currentPredicate));
        } else if (otherPredicate.isEmpty() || currentPredicate.size() >= maxPreSize) {
            return;
        } else {
            for (int[] predicateArray : orderedPre)  {
//                nowPreIndex++;
                int predicate = predicateArray[0];
                if (!otherPredicate.contains(predicate) || (!currentPredicate.isEmpty() && currentPredicate.last() > predicate)) { continue; }

                currentPredicate.add(predicate);

//                if (isSubset(allMinimalCover, currentPredicate)) {
//                    currentPredicate.remove(predicate);
//                    continue;
//                }
                int index0 = predicate / 6;
                for (int i = 0; i < 6; i++) { otherPredicate.remove(index0 * 6 + i); }

//                for (int j : isFind) { if (j % row != j / row && arrayB[j].contains(predicate)) { tmpFind.add(j); } }
                isFind.removeAll(predicateEvidence.get(predicate));
                findCover(currentPredicate, isFind, otherPredicate, orderedPre, allMinimalCover, approximate, maxPreSize);
                currentPredicate.remove(predicate);
                isFind.addAll(predicateEvidence.get(predicate));
                for (int i = 0; i < 6; i++) { otherPredicate.add(index0 * 6 + i); }
            }
        }
    }

    public boolean isSubset(Set<Set<Integer>> allMinimalCover, Set<Integer> currentPredicate) {
        for (Set<Integer> nowMC : allMinimalCover) {
            if (currentPredicate.containsAll(nowMC)) return true;
        }
        return false;
    }

    public void outputDc(Set<Set<Integer>> allMinimalCover) {
        for (Set<Integer> set : allMinimalCover) {
            List<Integer> list = new ArrayList<Integer>();
            list.addAll(set);
            Collections.sort(list);
            for (Integer predicate : list) {
                int index0 = predicate / 6, index1 = predicate % 6;
                System.out.print("Tx." + attributeNameArray[index0] + " " + comparison[index1] + " " + "Ty." + attributeNameArray[index0] + " and ");
            }
            System.out.println();
//        }
        }
    }




}
