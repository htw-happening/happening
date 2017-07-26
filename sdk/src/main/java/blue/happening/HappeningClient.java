package blue.happening;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import blue.happening.sdk.Happening;

/**
 * This class represents a device in the happening network. You get a list of the clients
 * via {@link Happening#getClients getClients }or by implementing the
 * different {@link IHappeningCallback HappeningCallback methods}.
 */
public class HappeningClient implements Parcelable, Serializable {

    private String uuid;
    private String name;

    public static final Creator<HappeningClient> CREATOR = new Creator<HappeningClient>() {
        @Override
        public HappeningClient createFromParcel(Parcel in) {
            return new HappeningClient(in);
        }

        @Override
        public HappeningClient[] newArray(int size) {
            return new HappeningClient[size];
        }
    };

    public HappeningClient(String uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    private HappeningClient(Parcel in) {
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Marshalling
     * @param out
     * @param flags
     */
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(uuid);
        out.writeString(name);
    }

    /**
     * Demarshalling
     * @param in
     */
    public void readFromParcel(Parcel in) {
        uuid = in.readString();
        name = in.readString();
    }

    /**
     * Deserialization
     * @param bytes
     * @return
     */
    public static HappeningClient fromBytes(byte[] bytes) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInput in = new ObjectInputStream(bis)) {
            return (HappeningClient) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }

    /**
     * Serialization
     * @return
     */
    public byte[] toBytes() {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutput out = new ObjectOutputStream(bos)) {
            out.writeObject(this);
            return bos.toByteArray();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Getting the unique ID for this client in the network.
     * @return uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Getting the name for this client.
     * @return
     */
    public String getName() {
        return name;
    }
}
