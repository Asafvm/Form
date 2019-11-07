package il.co.diamed.com.form.filebrowser;

import androidx.annotation.NonNull;

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
        if (directory && !o.directory)
            return 1;
        else if(directory && o.directory || !(directory && o.directory))
            return(this.text.compareTo(o.getText()));
        else
            return -1;

    }
}
