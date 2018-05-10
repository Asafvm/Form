package il.co.diamed.com.form.res;

public class FileBrowserItem {
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
}
