import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

        CSVwriter csv = new CSVwriter("test.csv");

        csv.addElementToDatabase("Book",
                new String[]{"111", "222", "333"});

    }
}
