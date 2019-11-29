package il.co.diamed.com.form.data_objects;

import java.util.ArrayList;
import java.util.Date;

public class FieldDevice extends PrototypeDevice implements Comparable<FieldDevice> {

    private String dev_serial = "";
    private Date dev_install_date = new Date();
    private Date dev_next_maintenance = new Date();
    private Date end_of_warranty = new Date();
    private boolean dev_under_warranty = true;
    private String dev_comments = "";
    private int dev_calValid = 12;  //months
    private ArrayList<Report> reports = new ArrayList<>();

    public FieldDevice(){
        super();
    }

    public FieldDevice(String dev_codeNumber, String dev_codename, String dev_model, String dev_serial, Date dev_install_date, Date dev_next_maintenance, Date end_of_warranty, boolean dev_under_warranty, String dev_comments, double dev_price, ArrayList<Report> reports) {
        super(dev_codeNumber,dev_codename,dev_model,dev_serial,dev_price);
        this.dev_serial = dev_serial;
        this.dev_install_date = dev_install_date;
        this.dev_next_maintenance = dev_next_maintenance;
        this.end_of_warranty = end_of_warranty;
        this.dev_under_warranty = dev_under_warranty;
        this.dev_comments = dev_comments;
        this.reports = reports;

    }

    public FieldDevice(String dev_manifacturer, String dev_codeNumber, String dev_codename, String dev_model, double dev_price) {
        super(dev_manifacturer, dev_codeNumber, dev_codename, dev_model, dev_price);
    }

    public Date getDev_install_date() {
        return dev_install_date;
    }

    public void setDev_install_date(Date dev_install_date) {
        this.dev_install_date = dev_install_date;
    }

    public Date getDev_next_maintenance() {
        return dev_next_maintenance;
    }

    
    public void setDev_next_maintenance(Date dev_next_maintenance) {
        this.dev_next_maintenance = dev_next_maintenance;
    }

    
    public Date getEnd_of_warranty() {
        return end_of_warranty;
    }

    
    public void setEnd_of_warranty(Date end_of_warranty) {
        this.end_of_warranty = end_of_warranty;
    }

    
    public boolean isDev_under_warranty() {
        return dev_under_warranty;
    }

    
    public void setDev_under_warranty(boolean dev_under_warranty) {
        this.dev_under_warranty = dev_under_warranty;
    }

    
    public String getDev_comments() {
        return dev_comments;
    }

    
    public void setDev_comments(String dev_comments) {
        this.dev_comments = dev_comments;
    }

    
    public int getDev_calValid() {
        return dev_calValid;
    }

    
    public void setDev_calValid(int dev_calValid) {
        this.dev_calValid = dev_calValid;
    }

    
    public ArrayList<Report> getReports() {
        return reports;
    }

    
    public void setReports(ArrayList<Report> reports) {
        this.reports = reports;
    }


    @Override
    public int compareTo(FieldDevice o) {
        return this.dev_under_warranty && !o.isDev_under_warranty() ? -1 :  //else
                !this.dev_under_warranty && o.isDev_under_warranty() ? 1 : //else
                        this.dev_next_maintenance.compareTo(o.getDev_next_maintenance()) == 0 ? this.getDev_codename().compareTo(o.getDev_codename()) : //else
                                this.dev_next_maintenance.compareTo(o.getDev_next_maintenance());    }

    public String getDev_serial() {
        return dev_serial;
    }

    public void setDev_serial(String dev_serial) {
        this.dev_serial = dev_serial;
    }
}
