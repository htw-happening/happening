package blue.happening;

import android.os.Parcel;
import android.os.Parcelable;


public class HappeningParcel implements Parcelable {

    public String parcelId;

    public HappeningParcel(Parcel source) {
        parcelId = source.readString();
    }

    public HappeningParcel() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    public static final Parcelable.Creator<HappeningParcel> CREATOR = new Parcelable.Creator<HappeningParcel>() {
        @Override
        public HappeningParcel createFromParcel(Parcel source) {
            return new HappeningParcel(source);
        }

        @Override
        public HappeningParcel[] newArray(int size) {
            return new HappeningParcel[size];
        }
    };
}
