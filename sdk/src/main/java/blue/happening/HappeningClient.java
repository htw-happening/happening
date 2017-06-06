package blue.happening;

import android.os.Parcel;
import android.os.Parcelable;


public class HappeningClient implements Parcelable {

    private String clientId;
    private String clientName;

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

    public HappeningClient(String clientId, String clientName) {
        this.clientId = clientId;
        this.clientName = clientName;
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
        out.writeString(clientId);
        out.writeString(clientName);
    }

    public void readFromParcel(Parcel in) {
        clientId = in.readString();
        clientName = in.readString();
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientName() {
        return clientName;
    }
}
