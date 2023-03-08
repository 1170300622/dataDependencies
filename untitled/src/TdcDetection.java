import java.io.*;
import java.util.*;

/**
 * @author serious
 * @time 2023-01-31 14:20:31
 * @description TODO
 */
public class TdcDetection {
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
    public static void main(String[] args) {
        TdcFind tdc = new TdcFind();
        List<String[]> input = new ArrayList<>  ();
        boolean[] types = tdc.readFile("E:\\学校\\科研\\研究生毕设\\数据集\\TDC\\TestStock.csv", input);
//        for (int i = 0; i < tdc.col; i++) {
//            System.out.println(tdc.attributeNameArray[i] + " " + types[i]);
//        }
//        System.out.println(input.get(0)[0] + " " + input.get(0)[6]);
        tdc.createRealPli(input, types);
//        System.out.println(tdc.pliNumerical.size() + " " + tdc.pliString.size());
        tdc.createArrayB(input, types);
        Set<Set<Integer>> res = tdc.findAllDc(0, types);
        System.out.println(res.toString());
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

//    public void createPli(List<String[]> input) {
//        for (int j = 0; j < col; j++) {
//            pli.add(new HashMap<>(hashMapInitialCapacity));
//            for (int i = 0; i < row; i++) {
//                String key = input.get(i)[j];
//                if (!pli.get(j).containsKey(key)) {
//                    pli.get(i).put(key, new ArrayList<>());
//                }
//                pli.get(i).get(key).add(j);
//            }
//        }
//    }


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
    }
    public void errorBatchDetection(List<Integer> tdcs) {


    }

    public void errorStreamDetection(String[] newData, List<Integer> tdcs) {
        switch (tdcs.get(0)) {
            case 0:

                break;
            case 1:

                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
        }
    }

}