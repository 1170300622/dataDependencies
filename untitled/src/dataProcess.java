import java.io.*;
import java.util.*;

/**
 * @author serious
 * @time 2023-03-15 10:55:10
 * @description TODO
 */
public class dataProcess {
    public void dataSort(String fileName) {

        String writeFileName = "E:\\学校\\科研\\研究生毕设\\数据集\\TDC\\TheStock.csv";
        BufferedReader reader = null;
        BufferedWriter writer = null;
        try {
            reader = new BufferedReader(new FileReader(fileName));
            String[] firstLine = reader.readLine().split(",");

            int col = firstLine.length;
            // Confirm the name and type

            String nowLine = new String();
            List<String[]> res = new ArrayList<String[]>();
            while ((nowLine = reader.readLine()) != null) {
                String[] tmpLine = nowLine.split(",");
                res.add(tmpLine);
            }
            Collections.sort(res, (String[] s1, String[] s2)->(s1[0].compareTo(s2[0])));
            writer = new BufferedWriter(new FileWriter(writeFileName));
            for (String s : firstLine) {
                writer.write(s + ",");
                writer.flush();
            }
            writer.newLine();

            for (String[] tmp : res) {
                for (String s : tmp) {
                    writer.write(s + ",");
                    writer.flush();
                }
                writer.newLine();
            }
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
    }

    public static void main(String[] args) {
        new dataProcess().dataSort("E:\\学校\\科研\\研究生毕设\\数据集\\TDC\\SPStock.csv");
    }
}