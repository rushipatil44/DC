package EXPT_6;
import java.io.*;
import java.net.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TokenRingServer {

    private static final int PORT = 4444;
    public static final BlockingQueue<Socket> waitingClients = new LinkedBlockingQueue<>();
    private static boolean isCriticalSectionOccupied = false;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port: " + PORT);

            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Client connected: " + clientSocket.getInetAddress());

                    // Enqueue the client to the waiting list
                    waitingClients.offer(clientSocket);

                    // Start a new thread to handle client requests
                    new ClientHandler(clientSocket).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ClientHandler extends Thread {
        private final Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String request = in.readLine();

                if (request != null) {
                    if (request.equals("Request Critical Section")) {
                        handleRequestCriticalSection();
                    } else if (request.equals("Release Critical Section")) {
                        handleReleaseCriticalSection();
                    } else {
                        System.out.println("Invalid request from client: " + request);
                    }
                }
            } catch (IOException e) {
                System.out.println("Error handling client request: " + e.getMessage());
            } finally {
                try {
                    // Close connection
                    clientSocket.close();
                    System.out.println("Client disconnected: " + clientSocket.getInetAddress());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void handleRequestCriticalSection() throws IOException {
            if (!isCriticalSectionOccupied) {
                // Grant the critical section to the client
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                out.println("Critical Section Granted");
                System.out.println("Critical Section granted to client: " + clientSocket.getInetAddress());
                isCriticalSectionOccupied = true;
            } else {
                // Inform the client that the critical section is not available
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                out.println("Critical Section Not Available");
                System.out.println("Critical Section not available. Client waiting: " + clientSocket.getInetAddress());
            }
        }

        private void handleReleaseCriticalSection() throws IOException {
            // Release the critical section
            isCriticalSectionOccupied = false;

            // Check if there are clients waiting
            if (!waitingClients.isEmpty()) {
                Socket nextClient = waitingClients.poll();
                giveTokenToClient(nextClient);
            }
        }

        private void giveTokenToClient(Socket clientSocket) throws IOException {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            out.println("Critical Section Granted");
            System.out.println("Sent critical section to client: " + clientSocket.getInetAddress());
            isCriticalSectionOccupied = true;
        }
    }
}
