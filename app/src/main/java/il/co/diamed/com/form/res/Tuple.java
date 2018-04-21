package il.co.diamed.com.form.res;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Tuple<X, Y> implements Parcelable{
    protected int corX;
    protected int corY;
    public final X x;
    public final Y y;

    public Tuple(X x, Y y) {
        this.x = x;
        this.y = y;
    }

    protected Tuple(Parcel in) {
        in.readParcelable(getClass().getClassLoader());
    }

    public static final Creator<Tuple> CREATOR = new Creator<Tuple>() {
        @Override
        public Tuple createFromParcel(Parcel in) {
            return new Tuple(in);
        }

        @Override
        public Tuple[] newArray(int size) {
            return new Tuple[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(corX);
        dest.writeInt(corY);
    }

}
