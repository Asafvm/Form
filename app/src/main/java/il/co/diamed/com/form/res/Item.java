package il.co.diamed.com.form.res;
import android.view.View;

public class Item {

    private int layout_res;
    private String text;


    public Item(int layout_res, String text) {
        this.layout_res = layout_res;
        this.text = text;
    }


    public String getText() {
        return text;
    }

    public int getLayout_res() {
        return layout_res;
    }
}
