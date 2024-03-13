import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
public class Client {
    private static void startSendingTime(Socket slaveSocket) throws IOException, InterruptedException {
        PrintWriter out = new PrintWriter(slaveSocket.getOutputStream(), true);
        while (true) {
            out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            System.out.println("Recent time sent successfully\n");
            Thread.sleep(5000);
        }
    }
    private static void startReceivingTime(Socket slaveSocket) throws IOException, ParseException {
    BufferedReader in = new BufferedReader(new InputStreamReader(slaveSocket.getInputStream()));
    while (true) {
        String receivedTimeStr = in.readLine();
        receivedTimeStr = receivedTimeStr.trim();
        LocalDateTime synchronizedTime = LocalDateTime.parse(receivedTimeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        System.out.println("Synchronized time at the client is: " + synchronizedTime + "\n");
    }
}
    private static void initiateSlaveClient(int port) throws IOException {
        Socket slaveSocket = new Socket("127.0.0.1", port);
        System.out.println("Starting to receive time from server\n");
        Thread sendTimeThread = new Thread(() -> {
            try {
                startSendingTime(slaveSocket);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        sendTimeThread.start();
        System.out.println("Starting to receive synchronized time from server\n");
        Thread receiveTimeThread = new Thread(() -> {
            try {
                startReceivingTime(slaveSocket);
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        });
        receiveTimeThread.start();
    }
    public static void main(String[] args) throws IOException {
        initiateSlaveClient(8080);
    }
}
