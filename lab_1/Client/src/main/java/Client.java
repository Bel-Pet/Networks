import java.io.BufferedReader;
import java.io.IOException;
import java.net.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Client {
    private static final Logger logger = LogManager.getLogger(Client.class);
    private final DatagramSocket socket;
    private final InetAddress address;
    private final BufferedReader br;

    private int numPacket;
    private final int finalSendAttempts = 10;

    public Client(BufferedReader br) throws SocketException, UnknownHostException {
        socket = new DatagramSocket();
        address = InetAddress.getLocalHost();
        this.br = br;
    }

    public void run() {
        try {
            logger.info("Start sending");
            if (handshake()) sendMessage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   private boolean handshake() throws IOException {
       logger.info("Connection start");
        int attempt = 0;
        int timeout = 1;
       int maxDataSize = 1024;
       byte[] data = new byte[maxDataSize];
        numPacket++;
        while (attempt < finalSendAttempts) {
            logger.info("Attempt[" + attempt + "]");
            socket.send(new DatagramPacket(data, data.length, address, 8080));
            try {
                socket.setSoTimeout(timeout);
                socket.receive(new DatagramPacket(data, data.length));
                if (data[0] == numPacket) {
                    logger.info("Connection success");
                    return true;
                }
            } catch (SocketTimeoutException e) {
                timeout *= 2;
                attempt++;
            }
        }
        return false;
    }

    private void sendMessage() throws IOException {
        int maxSize = 62;
        String message;
        while((message = br.readLine()) != null) {
            int position = 0;
            while (position < message.length()) {
                String segment = message.substring(position, Math.min(message.length(), position + maxSize));
                if (!sendSegment(segment)) {
                    System.out.println("Server connection problem");
                    return;
                }
                position += maxSize;
            }
        }
    }

    private boolean sendSegment(String segment) throws IOException {
        logger.info("Send[" + numPacket + "]: " + segment);
        int attempt = 0;
        int timeout = 1;
        byte[] data = fillData(segment);

        while (attempt < finalSendAttempts) {
            logger.info("Again send, attempt[" + attempt + "]");
            socket.send(new DatagramPacket(data, data.length, address, 8080));
            try {
                socket.setSoTimeout(timeout);
                socket.receive(new DatagramPacket(data, data.length));
                if (data[0] == numPacket) {
                    logger.info("Success");
                    numPacket++;
                    return true;
                }
            } catch (SocketTimeoutException e) {
                timeout *= 2;
                attempt++;
            }
        }
        return false;
    }

    private byte[] fillData(String segment) {
        byte[] data = new byte[segment.getBytes().length + 2];
        data[0] = (byte) numPacket;
        data[1] = (byte) segment.length();
        System.arraycopy(segment.getBytes(), 0, data, 2, segment.getBytes().length);
        return data;
    }

    public void close() {
        socket.close();
    }
}