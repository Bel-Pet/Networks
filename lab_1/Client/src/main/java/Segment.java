import java.nio.charset.StandardCharsets;

public class Segment {
    private byte[] data;
    private int seqPos = 0;
    private int ackPos = 0;

    Segment(byte[] segment) {
        data = segment;
    }

    public int getSeq() {
        return data[seqPos];
    }

    public int getAck() {
        return data[ackPos];
    }

    public String getString() {
        byte[] buf = new byte[data.length];
        System.arraycopy(data, 3, buf, 0, data.length);
        return new String(buf);
    }
}
