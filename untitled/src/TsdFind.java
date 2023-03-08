import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * @author serious
 */
public class TsdFind {
//    final int SD_INTERVAL_SIZE = 2;
    // Exact SD
    public double[] findExactSd(List<Double> input) {
        double[] sdInterval = {Double.MAX_VALUE, Double.MAX_VALUE};
        for (int i = 0; i < input.size() - 1; i++) {
            double val = input.get(i) - input.get(i+1);
            sdInterval[0] = Math.min(sdInterval[0], val);
            sdInterval[1] = Math.max(sdInterval[1], val);
        }
        return sdInterval;
    }
    // Approximate SD
    public List<double[]> findApproximateSd(List<Double> input, double degree) {

        int inputSize = input.size();
        List<Double> differentVals = new ArrayList<>();
        for (int i = 0; i < inputSize - 1; i++) {
            differentVals.add(input.get(i) - input.get(i+1));
        }
        Collections.sort(differentVals);
        int approximateNum = (int)(inputSize * degree);
        int num = 0;
        for (double val : differentVals) {
            if (Math.abs(val) < 0.000001) {
                num++;
            }
        }
//        System.out.println(num);
        List<double[]> approximateIntervals = new ArrayList<>();
        Set<String> flagSet = new HashSet<>();
        for (int i = 0; i < inputSize - approximateNum; i++) {
            if (i < inputSize - approximateNum - 1 && differentVals.get(i + approximateNum - 1).toString().equals(differentVals.get(i + approximateNum).toString())) continue;
            if (i > 0 && flagSet.contains(differentVals.get(i).toString())) continue;
            double[] interval = {differentVals.get(i), differentVals.get(i + approximateNum - 1)};
            flagSet.add(differentVals.get(i).toString());
            approximateIntervals.add(interval);
        }
        return approximateIntervals;
    }

    public List<Double> readFirstFile(String fileName) {
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
        TsdFind tsd = new TsdFind();
        List<Double> input = tsd.readFirstFile("E:\\学校\\科研\\研究生毕设\\数据集\\TSD\\test.csv");
        double[] exact = tsd.findExactSd(input);
        System.out.println("----精确TSD----");
        System.out.println("[" + exact[0] + ", " + exact[1] + "]");
        double degree = 1;
        List<double[]> res = tsd.findApproximateSd(input, degree);
        System.out.println("----近似TSD----");
        System.out.println("degree: " + degree);
        for (double[] data : res) {
            System.out.println("[" + data[0] + ", " + data[1] + "]");
        }
    }
}
