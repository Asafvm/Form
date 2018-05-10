package il.co.diamed.com.form.res;
import android.view.View;

public class MultiLayoutItem {

    private int layout_res;
    private String text;


    public MultiLayoutItem(int layout_res, String text) {
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
