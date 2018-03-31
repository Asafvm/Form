package il.co.diamed.com.form.res;


import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;

public class Tuple<X, Y> implements Parcelable,Serializable{
    private float x;
    private float y;

    public Tuple(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }


    protected Tuple(Parcel in) {
        this.x = in.readFloat();
        this.y = in.readFloat();

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
        dest.writeFloat(this.x);
        dest.writeFloat(this.y);
    }

}
