package il.co.diamed.com.form.res;

import android.support.annotation.NonNull;

public class FileBrowserItem implements Comparable{
    private String text;
private boolean directory;

    public FileBrowserItem(String text, boolean directory) {
        this.text = text;
        this.directory = directory;
    }


    public String getText() {
        return text;
    }

    public boolean isDirectory() {
        return directory;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        if (!directory)
            return -1;
        else
            return(this.text.compareTo(((FileBrowserItem)o).getText()));

    }
}
