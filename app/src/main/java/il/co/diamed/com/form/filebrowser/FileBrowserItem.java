package il.co.diamed.com.form.filebrowser;

import android.support.annotation.NonNull;

public class FileBrowserItem implements Comparable<FileBrowserItem>{
    private String text;
private boolean directory;

    FileBrowserItem(String text, boolean directory) {
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
    public int compareTo(@NonNull FileBrowserItem o) {
        if (!directory)
            return -1;
        else
            return(this.text.compareTo(o.getText()));

    }
}
