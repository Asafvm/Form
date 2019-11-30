package il.co.diamed.com.form.data_objects;


public class PrototypeDevice {
    private String dev_identifier = "";
    private String dev_manufacturer = "Unknown";
    private String dev_codeNumber = "";
    private String dev_codeName = "";
    private String dev_model = "1";
    private double dev_price = 0;

    public PrototypeDevice() {
    }

    public PrototypeDevice(String dev_manufacturer, String dev_codeNumber, String dev_codeName, String dev_model, double dev_price) {
        this.setDev_manufacturer(dev_manufacturer);
        this.setDev_codeNumber(dev_codeNumber);
        this.setDev_codeName(dev_codeName);
        this.setDev_model(dev_model);
        this.setDev_price(dev_price);
        this.setDev_identifier(dev_manufacturer + " " + dev_codeNumber + " " + dev_codeName);
    }

    public String getDev_manufacturer() {
        return dev_manufacturer;
    }

    private void setDev_manufacturer(String dev_manufacturer) {
        if (dev_manufacturer.equals(""))
            this.dev_manufacturer = "Unknown";
        else
            this.dev_manufacturer = dev_manufacturer;
    }

    public String getDev_codeNumber() {
        return dev_codeNumber;
    }

    private void setDev_codeNumber(String dev_codeNumber) {
        this.dev_codeNumber = dev_codeNumber;
    }

    public String getDev_codeName() {
        return dev_codeName;
    }

    private void setDev_codeName(String dev_codeName) {
        this.dev_codeName = dev_codeName;
    }

    private String getDev_model() {
        return dev_model;
    }

    private void setDev_model(String dev_model) {
        if (dev_model.equals(""))
            this.dev_model = "1";
        else
            this.dev_model = dev_model;
    }

    public double getDev_price() {
        return dev_price;
    }

    private void setDev_price(double dev_price) {
        if (dev_price < 0)
            this.dev_price = 0;
        else
            this.dev_price = dev_price;
    }

    public String getDev_identifier() {
        return dev_identifier;
    }

    private void setDev_identifier(String dev_identifier) {
        this.dev_identifier = dev_identifier;
    }

    @Override
    public String toString() {
        return getDev_manufacturer() + "\t" + getDev_codeNumber()
                + "\t" + getDev_codeName();
    }
}

