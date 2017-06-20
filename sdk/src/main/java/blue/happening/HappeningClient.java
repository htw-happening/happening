package blue.happening;

import android.os.Parcel;
import android.os.Parcelable;


public class HappeningClient implements Parcelable {

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

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(uuid);
        out.writeString(name);
    }

    public void readFromParcel(Parcel in) {
        uuid = in.readString();
        name = in.readString();
    }

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }
}
