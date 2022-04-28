import java.io.BufferedReader;
import java.io.FileReader;

public class Main {
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader("src/main/resources/text.txt"));
        Client client = new Client(br);
        client.run();
        client.close();
    }
}
