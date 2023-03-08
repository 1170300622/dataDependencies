import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author serious
 * @time 2023-01-23 21:44:29
 * @description TODO
 */
public class TsdDetection {

    public void errorDetection(List<Double> input, double[] intervals) {
        int size = input.size();
        int errorNum = 0;
        System.out.println("error: ");
        for (int i = 0; i < size - 1; i++) {
            double val = input.get(i) - input.get(i+1);
            if (val > intervals[1] || val < intervals[0]) {
                errorNum++;
                System.out.print((i + 1) + ", ");
                if (errorNum % 20 == 0) {
                    System.out.println();
                }
            }
        }
        System.out.println("\nerror: " + (errorNum/(double)size));
    }

    public List<Double> readFile(String fileName) {
        List<Double> input = new ArrayList<>();
        File file = new File(fileName);
        BufferedReader reader = null;
        int index = 2;
        try {
            reader = new BufferedReader(new FileReader(fileName));
            String[] firstLine = reader.readLine().split(",");


            String nowLine = new String();
            while ((nowLine = reader.readLine()) != null) {
                input.add(Double.valueOf(nowLine.split(",")[index]));
            }
            return input;
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
        return input;
    }

    public static void main(String[] args) {
        TsdDetection detection = new TsdDetection();
        String fileName = "E:\\学校\\科研\\研究生毕设\\数据集\\TSD\\realData.csv";
        List<Double> inputData = detection.readFile(fileName);
        double[] interval = {-0.5, 0};
        detection.errorDetection(inputData, interval);
    }
}