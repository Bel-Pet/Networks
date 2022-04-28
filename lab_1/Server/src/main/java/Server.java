import java.io.IOException;
import java.net.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class Server {
    private static final Logger logger = LogManager.getLogger(Server.class);
    private final DatagramSocket socket;

    private int numPacket;
    private DatagramPacket packet;

    public Server() throws SocketException {
        socket = new DatagramSocket(8080);
    }

    public void run() {
        try {
            receiveSegment();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handshake() throws IOException {
        logger.info("Connection start");
        int attempt = 0;
        byte[] data = packet.getData();
        numPacket = 1;
        data[0] = (byte) numPacket;
        logger.info("Attempt[" + attempt + "]");
        socket.send(new DatagramPacket(data, data.length, packet.getAddress(), packet.getPort()));
        logger.info("Connection success");
    }

    private void receiveSegment() throws IOException {
        while (true) {
            int maxDataSize = 1024;
            byte[] data = new byte[maxDataSize];
            packet = new DatagramPacket(data, data.length);
            socket.receive(packet);

            if (Math.random() < 0.6) {
                logger.info("Packet is lost");
                continue;
            }
            data = packet.getData();
            if (data[0] == 0) {
                handshake();
                continue;
            }

            if (data[0] == numPacket) {
                String segment = new String(packet.getData(), 2, packet.getLength() - 2);
                logger.info("Received[" + numPacket + "]: " + segment);
                socket.send(new DatagramPacket(data, data.length, packet.getAddress(), packet.getPort()));
                numPacket++;
            }
        }
    }
}
