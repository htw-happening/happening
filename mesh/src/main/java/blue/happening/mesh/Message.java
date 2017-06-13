package blue.happening.mesh;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;


public class Message implements Serializable {

    public static final int INITIAL_MESSAGE_TQ = 255;
    public static final int INITIAL_MESSAGE_TTL = 5;
    public static final int MESSAGE_TYPE_OGM = 1;
    public static final int MESSAGE_TYPE_UCM = 2;

    private String source;
    private String previousHop;
    private String destination;
    private byte[] body;
    private int tq;
    private int type;
    private int sequence;
    private int ttl;

    public Message(String source, String destination, int sequence, int type,
                   byte[] body) {
        this(source, destination, sequence, type, INITIAL_MESSAGE_TQ,
                INITIAL_MESSAGE_TTL, body);
    }

    private Message(String source, String destination, int sequence, int type,
                    int tq, int ttl, byte[] body) {
        this.source = source;
        this.previousHop = source;
        this.destination = destination;
        this.sequence = sequence;
        this.type = type;
        this.tq = tq;
        this.ttl = ttl;
        this.body = body;
    }

    public static Message fromBytes(byte[] bytes) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInput in = new ObjectInputStream(bis)) {
            return (Message) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public String getPreviousHop() {
        return previousHop;
    }

    public void setPreviousHop(String lastSender) {
        this.previousHop = lastSender;
    }

    public byte[] getBody() {
        return body;
    }

    public int getTq() {
        return tq;
    }

    public void setTq(int tq) {
        this.tq = tq;
    }

    public int getType() {
        return type;
    }

    public int getSequence() {
        return sequence;
    }

    public int getTtl() {
        return ttl;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }

    public byte[] toBytes() {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutput out = new ObjectOutputStream(bos)) {
            out.writeObject(this);
            return bos.toByteArray();
        } catch (IOException e) {
            return null;
        }
    }

    public String toString() {
        return getSource() + "->" + getPreviousHop() + "->"
                + getDestination() + ", TTL: " + getTtl() + ", TQ:" + getTq() + ", " + getSequence();
    }
}
