package blue.happening.service.lib;

import android.os.Parcel;
import android.os.Parcelable;


public class BluetoothDevice implements Parcelable {

    public static final Parcelable.Creator<BluetoothDevice> CREATOR = new Parcelable.Creator<BluetoothDevice>() {
        public BluetoothDevice createFromParcel(Parcel in) {
            return new BluetoothDevice(in);
        }

        public BluetoothDevice[] newArray(int size) {
            return new BluetoothDevice[size];
        }
    };

    private String name;

    public BluetoothDevice(String name) {
        this.name = name;
    }

    private BluetoothDevice(Parcel in) {
        name = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(name);
    }

    public String getName() {
        return name;
    }
}
