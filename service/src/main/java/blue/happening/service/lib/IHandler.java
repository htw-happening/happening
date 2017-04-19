package blue.happening.service.lib;

import android.os.Parcel;
import android.os.Parcelable;


public class IHandler implements Parcelable {

    public static final Parcelable.Creator<IHandler> CREATOR = new Parcelable.Creator<IHandler>() {
        public IHandler createFromParcel(Parcel in) {
            return new IHandler(in);
        }

        public IHandler[] newArray(int size) {
            return new IHandler[size];
        }
    };

    private String name;

    public IHandler(String name) {
        this.name = name;
    }

    private IHandler(Parcel in) {
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

    @Override
    public String toString() {
        return "IHandler{" +
                "name='" + name + '\'' +
                '}';
    }

}
