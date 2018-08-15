package il.co.diamed.com.form.calibration.res;


import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Tuple<X, Y> implements Parcelable,Serializable{
    private float x;
    private float y;
    private String text;
    private boolean rtl;

    public Tuple(float x, float y, java.lang.String text, boolean rtl) {
        this.x = x;
        this.y = y;
        this.text = text;
        this.rtl = rtl;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public String getText() { return text; }

    public Boolean getRtl() { return rtl; }

    private Tuple(Parcel in) {
        this.x = in.readFloat();
        this.y = in.readFloat();
        this.text = in.readString();
        this.rtl = in.readByte() != 0;

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
        dest.writeString(this.text);
        dest.writeByte((byte) (this.rtl ? 1 : 0));
    }

}
