package blue.happening.mesh;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.UUID;


public class Message implements Serializable {

    private String source;
    private String previousHop;
    private String destination;
    private byte[] body;
    private int tq;
    private int type;
    private int sequence;
    private int ttl;
    private transient UUID uuid;

    public Message(String source, String destination, int sequence, int type,
                   byte[] body) {
        this(source, destination, sequence, type, MeshHandler.INITIAL_MESSAGE_TQ,
                MeshHandler.INITIAL_MESSAGE_TTL, body);
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
        this.uuid = UUID.randomUUID();
    }

    public static Message fromBytes(byte[] bytes) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInput in = new ObjectInputStream(bis)) {
            Message message = (Message) in.readObject();
            message.setUuid(UUID.randomUUID());
            return message;
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

    void setPreviousHop(String lastSender) {
        this.previousHop = lastSender;
    }

    public byte[] getBody() {
        return body;
    }

    public int getTq() {
        return tq;
    }

    void setTq(int tq) {
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

    void setTtl(int ttl) {
        this.ttl = ttl;
    }

    public UUID getUuid() {
        return uuid;
    }

    void setUuid(UUID uuid) {
        this.uuid = uuid;
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
