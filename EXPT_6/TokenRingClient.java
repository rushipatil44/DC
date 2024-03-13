package EXPT_6;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class TokenRingClient extends Thread {

    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 4444;

    public static void main(String[] args) {
        new TokenRingClient().start();
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);

        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            int choice;
            do {
                System.out.println("MENU");
                System.out.println("1. Request the critical section");
                System.out.println("2. Release the critical section");
                System.out.println("3. Exit");
                System.out.print("Enter your choice: ");
                choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        requestCriticalSection(out, in);
                        break;
                    case 2:
                        releaseCriticalSection(out, in);
                        break;
                    case 3:
                        System.out.println("Exiting the program.");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                        break;
                }
            } while (choice != 3);

        } catch (IOException e) {
            System.out.println("Error during communication with the server: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }

    private void requestCriticalSection(PrintWriter out, BufferedReader in) throws IOException {
        out.println("Request Critical Section");

        // Receive response from the server
        String response = in.readLine();
        if (response != null) {
            System.out.println("Server Response: " + response);
        } else {
            System.out.println("Server did not respond.");
        }
    }

    private void releaseCriticalSection(PrintWriter out, BufferedReader in) throws IOException {
        out.println("Release Critical Section");

        // Receive response from the server
        String response = in.readLine();
        if (response != null) {
            System.out.println("Server Response: " + response);
        } else {
            System.out.println("Server did not respond.");
        }
    }
}
