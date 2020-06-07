import java.io.*;

public class processLog {
    public static void main(String[] args) throws Exception {
        BufferedReader reader;
        long count = 0;
        long sumTJ = 0;
        long sumTS = 0;
        try {
            reader = new BufferedReader(new FileReader("/Users/liuxuan/Desktop/Test3Slave.txt"));
            String line = reader.readLine();
            while (line != null) {
                // read TJ, TS
                String[] rs = line.split(",");
                sumTJ += Integer.parseInt(rs[0]);
                sumTS += Integer.parseInt(rs[1]);
                count += 1;

                // read next line
                line = reader.readLine();
            }
            System.out.println("averTJ: " + sumTJ/count + ", averTS: " + sumTS/count);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
