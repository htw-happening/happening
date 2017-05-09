package blue.happening;

import android.os.Parcel;
import android.os.Parcelable;


public class HappeningClient implements Parcelable {

    public String clientId;

    public HappeningClient(Parcel source) {
        clientId = source.readString();
    }

    public HappeningClient() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    public static final Parcelable.Creator<HappeningClient> CREATOR = new Parcelable.Creator<HappeningClient>() {
        @Override
        public HappeningClient createFromParcel(Parcel source) {
            return new HappeningClient(source);
        }

        @Override
        public HappeningClient[] newArray(int size) {
            return new HappeningClient[size];
        }
    };

}
