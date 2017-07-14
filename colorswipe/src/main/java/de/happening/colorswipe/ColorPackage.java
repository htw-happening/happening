package de.happening.colorswipe;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class ColorPackage implements Serializable {

    private final int from;
    private final int to;
    private final int color;
    private final Swiper.Direction direction;

    public ColorPackage(int from, int to, Swiper.Direction direction, int color) {
        this.from = from;
        this.to = to;
        this.direction = direction;
        this.color = color;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public int getColor() {
        return color;
    }

    public Swiper.Direction getDirection() {
        return direction;
    }

    @Override
    public String toString() {
        return "Color Package - direction: " + direction + " | from: " + from  + " | to: " + to + " | color: " + color;
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

    public static ColorPackage fromBytes(byte[] bytes) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInput in = new ObjectInputStream(bis)) {
            return (ColorPackage) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }
}
