import java.io.*;
import java.util.*;

/**
 * @author serious
 */
public class test {
    /**
     * Attributes inverted index.
     */
    Map<Integer, Integer> pliIndex = new HashMap<>();
    List<Map<String, List<Integer>>> pliString = new ArrayList<>();
    List<TreeMap<Double, List<Integer>>> pliNumerical = new ArrayList<>();
    List<Integer> timeIndex = new ArrayList<Integer>();

    int row, col;
    //    final int hashMapInitialCapacity = 10;
    List<Set<Integer>> arrayB;
    String[] attributeNameArray;
    String[] comparison = {"=", "!=", ">", ">=", "<", "<="};
    int[] selectivity;
    public static void main(String[] args) {
        test tdc = new test();
        List<String[]> input = new ArrayList<>();
        boolean[] types = tdc.readFile("data/Stock.csv", input);
//        for (int i = 0; i < tdc.col; i++) {
//            System.out.println(tdc.attributeNameArray[i] + " " + types[i]);
//        }
//        System.out.println(input.get(0)[0] + " " + input.get(0)[6]);
        long timeStart = System.currentTimeMillis();
        tdc.createRealPli(input, types);
        long timePli = System.currentTimeMillis();
        System.out.println("create Value Index: " + (timePli-timeStart) + "ms");
//        System.out.println(tdc.pliNumerical.size() + " " + tdc.pliString.size());
        tdc.createArrayB(input, types);
        long timeArrayB = System.currentTimeMillis();
        System.out.println("create E: " + (timeArrayB - timePli) + "ms");
        tdc.findAllDc(0, types);
        long timeFind = System.currentTimeMillis();
        System.out.println("find TDC: " + (timeFind - timeArrayB) + "ms");
//        System.out.println(res.size());
//        System.out.println(res.size());
//        tdc.outputDc(res);
    }


    public boolean[] readFile(String fileName, List<String[]> input) {

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
            if (i == 0) {
                for (int j = 1; j < row; j++) {
                    if (!input.get(j)[0].equals(input.get(j-1)[0])) {
                        timeIndex.add(j-1);
                    }
                }
                timeIndex.add(row - 1);
            } else if (!types[i]) {

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
//                System.out.println(pliNumerical.get(indexNumerical).toString());
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
//                System.out.println(pliString.get(indexString).toString());
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

//    public void readFileArrayB() {
//
//        BufferedReader reader = null;
//        try {
//            System.out.println("Start read file");
//            FileReader file = new FileReader("data/StockArrayB.txt");
//            reader = new BufferedReader(file);
//            for (Set<Integer> set : arrayB) {
//                List<Integer> list = new ArrayList<>(set);
//                Collections.sort(list);
//                writer.write(list.toString());
//                writer.newLine();
//                writer.flush();
//            }
//            System.out.println("ArrayB成功写入文件");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    /**
     *
     * @author serious
     * @param input
     * @param types whether the value is a character type
     */
    public void createArrayB(List<String[]> input, boolean[] types) {
        // Evidence initialization
        arrayB = new ArrayList<Set<Integer>>(row * row);
        Set<Integer> eAhead = new HashSet<>();
        for (int i = 0; i < col; i++) {
            eAhead.add(i * 6 + 1);
            if (!types[i]) {
                eAhead.add(i * 6 + 4);
                eAhead.add(i * 6 + 5);
            }
        }
        for (int i = 0; i < row * row; i++) {
            arrayB.add(new HashSet<>(eAhead));
        }
        for (int i = 0; i < timeIndex.size(); i++) {
            int end0 = timeIndex.get(i);
            int start0 = i > 0 ? timeIndex.get(i-1)+1 : 0;
            for (int index0 = start0; index0 < end0; index0++) {
                for (int index1 = start0; index1 < end0; index1++) {
                    if (index0 == index1) { continue; }
                    int realIndex = index0 * row + index1;
                    arrayB.get(realIndex).add(0);
                    arrayB.get(realIndex).remove(1);
                }
                for (int index1 = start0 - 1; index1 >= 0; index1--) {
                    int realIndex = index0 * row + index1;
                    arrayB.get(realIndex).add(2);
                    arrayB.get(realIndex).add(3);
                    arrayB.get(realIndex).remove(4);
                    arrayB.get(realIndex).remove(5);
                }
            }
        }
        for (int i = 1; i < col; i++) {
            // is String
            if (types[i]) {
                Iterator<Map.Entry<String, List<Integer>>> entryIterator = pliString.get(pliIndex.get(i)).entrySet().iterator();
                while (entryIterator.hasNext()) {
                    Map.Entry<String, List<Integer>> entry = entryIterator.next();
                    // same predicate
                    List<Integer> sameValueList = entry.getValue();
                    for (Integer index0 : sameValueList) {
                        for (Integer index1 : sameValueList) {
                            if (index0.equals(index1)) { continue; }
                            int realIndex = index0 * row + index1;
                            // add same predicate
                            arrayB.get(realIndex).add(i * 6);
                            // remove different predicate
                            arrayB.get(realIndex).remove(i * 6 + 1);
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

                            arrayB.get(realIndex).add(i * 6 + 2);
                            arrayB.get(realIndex).add(i * 6 + 3);
                            arrayB.get(realIndex).remove(i * 6 + 4);
                            arrayB.get(realIndex).remove(i * 6 + 5);

                        }
                        for (Integer index2 : entry.getValue()) {
                            if (index1.equals(index2)) {
                                continue;
                            }
                            int realIndex1 = index1 * row + index2;
                            arrayB.get(realIndex1).add(i * 6);
                            arrayB.get(realIndex1).add(i * 6 + 3);
                            arrayB.get(realIndex1).remove(i * 6 + 1);


                        }
                    }
                    tmpSet.addAll(entry.getValue());
                }

            }
        }
//        writeFileArrayB();
        pliIndex.clear();
        pliNumerical.clear();
        pliString.clear();
        countSelectivity();
    }

    public void countSelectivity() {
        int index = -1;
        for (Set<Integer> set : arrayB) {
            index++;
            if (index % row == 0) {
                continue;
            }
            for (Integer predicate : set) {
                selectivity[predicate]++;
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
            int tmpInt = i * 6;
            otherPredicate.add(tmpInt);
            otherPredicate.add(tmpInt + 1);
            if (!types[i]) {
                otherPredicate.add(tmpInt + 2);
                otherPredicate.add(tmpInt + 3);
                otherPredicate.add(tmpInt + 4);
                otherPredicate.add(tmpInt + 5);
            }
        }
//        System.out.println(otherPredicate.size());
        List<int[]> orderedPre = new ArrayList<int[]>();
        for (Integer predicate : otherPredicate) {
            orderedPre.add(new int[]{predicate, selectivity[predicate]});
        }
        Collections.sort(orderedPre, (int[] num1, int[] num2)->(num2[1] - num1[1]));
        Set<Integer> isFind = new HashSet<>();
        int approximateNumber =  (int)(approximate * row * (row - 1));
        for (int i = 1; i < row * row; i++) {
            if (i % row != i / row) { isFind.add(i); }
        }
//        System.out.println("近似数：" + approximateNumber);

//        for (int i = 0; i < 6; i++) {
//            Set<Integer> currentPredicate = new HashSet<>();
//            currentPredicate.add(i);
//            for (int j = 0; j < 6; j++) { otherPredicate.remove(j); }
//            Set<Integer> tmpFind = new HashSet<Integer>(isFind);
//            for (int j = 1; j < row * row; j++) {
//                if (j % row == j /row) { continue; }
//                int index0 = j / row, index1 = j % row;
//                switch (i) {
//                    case 2:
//                        if (index0 < index1) { tmpFind.remove(j); }
//                        break;
//                    case 4:
//                        if (index0 > index1) { tmpFind.remove(j); }
//                        break;
//                    default:
//                }
//                if (tmpFind.contains(j) && !arrayB.get(j).contains(i)) { tmpFind.remove(j); }
//            }
//            findCover(currentPredicate, tmpFind, otherPredicate, orderedPre, allMinimalCover, approximateNumber);
//            for (int j = 0; j < 6; j++) { otherPredicate.add(j); }
//        }
        Set<Integer> currentPredicate = new TreeSet<>();
        findCover(currentPredicate, isFind, otherPredicate, orderedPre, allMinimalCover, approximateNumber, 4);
        System.out.println(allMinimalCover.size());
        return allMinimalCover;
    }
    public void findCover(Set<Integer> currentPredicate, Set<Integer> isFind, Set<Integer> otherPredicate, List<int[]> orderedPre, Set<Set<Integer>> allMinimalCover, int approximate, int maxPreSize) {
        // Satisfy the approximation.
        if (isFind.size() <= approximate && !allMinimalCover.contains(currentPredicate) && !isSubset(allMinimalCover, currentPredicate)) {
//            int nowIndex = 0;
//            for (Integer predicate : currentPredicate) {
//                int index0 = predicate / 6, index1 = predicate % 6;
//                nowIndex++;
//                if (nowIndex == currentPredicate.size()) {
//                    System.out.println("Tx." + attributeNameArray[index0] + comparison[index1] + "Ty." + attributeNameArray[index0]);
//                    break;
//                }
//                System.out.print("Tx." + attributeNameArray[index0] + comparison[index1] + "Ty." + attributeNameArray[index0] + " ∧ ");
//            }
            allMinimalCover.add(new HashSet<>(currentPredicate));
            return;
        } else if (otherPredicate.isEmpty() || currentPredicate.size() >= maxPreSize) {
            return;
        } else {

//            System.out.println("当前谓词: " + currentPredicate.toString() + "   证据数 " + numberEvidences(isFind));
            Set<Integer> newPredicates = new HashSet<>(otherPredicate);
            for (int[] predicateArray : orderedPre)  {
                int predicate = predicateArray[0];
                if (!otherPredicate.contains(predicate)) { continue; }
                currentPredicate.add(predicate);
                if (allMinimalCover.contains(currentPredicate) || isSubset(allMinimalCover, currentPredicate)) {
//                if (allMinimalCover.contains(currentPredicate)) {
                    currentPredicate.remove(predicate);
                    continue;
                }
                int index0 = predicate / 6;
                for (int i = 0; i < 6; i++) { newPredicates.remove(index0 * 6 + i); }
                Set<Integer> tmpFind = new HashSet<>();

                for (int j : isFind) {
                    if (j % row != j / row && !arrayB.get(j).contains(predicate)) { tmpFind.add(j); }
                }
                isFind.removeAll(tmpFind);
                findCover(currentPredicate, isFind, newPredicates, orderedPre, allMinimalCover, approximate, maxPreSize);
                isFind.addAll(tmpFind);
                tmpFind.clear();
                currentPredicate.remove(predicate);
                for (int i = 0; i < 6; i++) { newPredicates.add(index0 * 6 + i); }
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
        System.out.println(allMinimalCover.size());
//        for (Set<Integer> set : allMinimalCover) {
//            List<Integer> list = new ArrayList<Integer>();
//            list.addAll(set);
//            Collections.sort(list);
//            for (Integer predicate : list) {
//                int index0 = predicate / 6, index1 = predicate % 6;
//                System.out.print("Tx." + attributeNameArray[index0] + " " + comparison[index1] + " " + "Ty." + attributeNameArray[index0] + " and ");
//            }
//            System.out.println();
//        }
    }




}
