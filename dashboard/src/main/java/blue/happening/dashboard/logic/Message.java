package blue.happening.dashboard.logic;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Message implements Serializable {

    boolean broadCast = false;
    String content = "";

    public Message(boolean broadCast, String content) {
        this.broadCast = broadCast;
        this.content = content;
    }

    public boolean isBroadCast() {
        return broadCast;
    }

    public String getContent() {
        return content;
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

    public static Message fromBytes(byte[] bytes) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInput in = new ObjectInputStream(bis)) {
            return (Message) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }
}
