import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class ClockServer {

    private static final Map<String, ClientData> clientData = new HashMap<>();
    private static void startReceivingClockTime(Socket masterSlaveConnector, String address)
            throws IOException, InterruptedException {
        BufferedReader in = new BufferedReader(new InputStreamReader(masterSlaveConnector.getInputStream()));
        while (true) {
            String clockTimeString = in.readLine();
            clockTimeString = clockTimeString.trim();
            LocalDateTime clockTime = LocalDateTime.parse(clockTimeString,
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            Duration timeDifference = Duration.between(clockTime, LocalDateTime.now());
            clientData.put(address, new ClientData(clockTime, timeDifference, masterSlaveConnector));
            System.out.println("Client Data updated with: " + address + "\n");
            Thread.sleep(5000);
        }
    }
    private static void startConnecting(ServerSocket masterServer) throws IOException {
        while (true) {
            Socket masterSlaveConnector = masterServer.accept();
            String slaveAddress = masterSlaveConnector.getInetAddress().getHostAddress() + ":"
                    + masterSlaveConnector.getPort();

            System.out.println(slaveAddress + " got connected successfully");
            Thread currentThread = new Thread(() -> {
                try {
                    startReceivingClockTime(masterSlaveConnector, slaveAddress);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            });
            currentThread.start();
        }
    }
    private static Duration getAverageClockDiff() {
        Map<String, ClientData> currentClientData = new HashMap<>(clientData);

        Duration sumOfClockDifference = currentClientData.values().stream()
                .map(ClientData::getTimeDifference)
                .reduce(Duration.ZERO, Duration::plus);

        return sumOfClockDifference.dividedBy(currentClientData.size());
    }
    private static void synchronizeAllClocks() throws IOException {
        while (true) {
            System.out.println("New synchronization cycle started.");
            System.out.println("Number of clients to be synchronized: " + clientData.size());

            if (!clientData.isEmpty()) {
                Duration averageClockDifference = getAverageClockDiff();

                for (Map.Entry<String, ClientData> entry : clientData.entrySet()) {
                    try {
                        LocalDateTime synchronizedTime = LocalDateTime.now().plus(averageClockDifference);
                        entry.getValue().getConnector().getOutputStream().write((synchronizedTime + "\n").getBytes());
                    } catch (Exception e) {
                        System.out.println(
                                "Something went wrong while sending synchronized time through " + entry.getKey());
                    }
                }
            } else {
                System.out.println("No client data. Synchronization not applicable.");
            }

            System.out.println("\n\n");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    private static void initiateClockServer(int port) throws IOException {
        ServerSocket masterServer = new ServerSocket(port);
        System.out.println("Socket at master node created successfully\n");
        System.out.println("Clock server started...\n");
        System.out.println("Starting to make connections...\n");
        Thread masterThread = new Thread(() -> {
            try {
                startConnecting(masterServer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        masterThread.start();
        System.out.println("Starting synchronization parallelly...\n");
        Thread syncThread = new Thread(() -> {
            try {
                synchronizeAllClocks();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        syncThread.start();
    }
    public static void main(String[] args) throws IOException {
        initiateClockServer(8080);
    }
    private static class ClientData {
        private final LocalDateTime clockTime;
        private final Duration timeDifference;
        private final Socket connector;
        public ClientData(LocalDateTime clockTime, Duration timeDifference, Socket connector) {
            this.clockTime = clockTime;
            this.timeDifference = timeDifference;
            this.connector = connector;
        }
        public LocalDateTime getClockTime() {
            return clockTime;
        }
        public Duration getTimeDifference() {
            return timeDifference;
        }
        public Socket getConnector() {
            return connector;
        }
    }
}
