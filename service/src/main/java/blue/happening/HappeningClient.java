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


public class HappeningClient implements Parcelable, Serializable {

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
    private String uuid;
    private String name;

    public HappeningClient(String uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    private HappeningClient(Parcel in) {
        readFromParcel(in);
    }

    public static HappeningClient fromBytes(byte[] bytes) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInput in = new ObjectInputStream(bis)) {
            return (HappeningClient) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(uuid);
        out.writeString(name);
    }

    public void readFromParcel(Parcel in) {
        uuid = in.readString();
        name = in.readString();
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

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }
}
