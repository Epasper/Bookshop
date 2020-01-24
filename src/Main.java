import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

        CSVwriter csv = new CSVwriter("database.csv");

        csv.addElementToDatabase("User",
                new String[]{"Username", "e-mail", "Password", "Avatar"},
                new String[]{"hwnd", "hwnd@gmail.com", "haslo123","pic.jpg"});

    }
}
