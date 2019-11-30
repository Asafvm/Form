package il.co.diamed.com.form.data_objects;


import java.util.Date;

public class PrototypeDevice {
    private String dev_identifier="";
    private String dev_manufacturer = "Unknown";
    private String dev_codeNumber = "";
    private String dev_codename = "";
    private String dev_model = "1";
    private double dev_price = 0;

    public PrototypeDevice(){}

    public PrototypeDevice(String dev_manufacturer, String dev_codeNumber, String dev_codename, String dev_model, double dev_price) {
        this.dev_manufacturer = dev_manufacturer;
        this.dev_codeNumber = dev_codeNumber;
        this.dev_codename = dev_codename;
        this.dev_model = dev_model;
        this.dev_price = dev_price;
        setDev_identifier(dev_manufacturer+" "+dev_codename+" "+dev_model);
    }

    public PrototypeDevice(PrototypeDevice device) {
        this.dev_manufacturer = device.dev_manufacturer;
        this.dev_codeNumber = device.dev_codeNumber;
        this.dev_codename = device.dev_codename;
        this.dev_model = device.dev_model;
        this.dev_price = device.dev_price;
    }

    public String getDev_manufacturer() {
        return dev_manufacturer;
    }

    public void setDev_manufacturer(String dev_manufacturer) {
        this.dev_manufacturer = dev_manufacturer;
    }

    public String getDev_codeNumber() {
        return dev_codeNumber;
    }

    public void setDev_codeNumber(String dev_codeNumber) {
        this.dev_codeNumber = dev_codeNumber;
    }

    public String getDev_codename() {
        return dev_codename;
    }

    public void setDev_codename(String dev_codename) {
        this.dev_codename = dev_codename;
    }

    public String getDev_model() {
        return dev_model;
    }

    public void setDev_model(String dev_model) {
        this.dev_model = dev_model;
    }

    public double getDev_price() {
        return dev_price;
    }

    public void setDev_price(double dev_price) {
        this.dev_price = dev_price;
    }

    public String getDev_identifier() {
        return dev_identifier;
    }

    public void setDev_identifier(String dev_identifier) {
        this.dev_identifier = dev_identifier;
    }
}

