package il.co.diamed.com.form.data_objects;


import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Date;

public class Device implements Comparable<Device> {
    private String dev_codeNumber = "";
    private String dev_codename = "";
    private String dev_model = "";
    private String dev_serial = "";
    private Date dev_install_date = new Date();
    private Date dev_next_maintenance = new Date();
    private Date end_of_warranty = new Date();
    private boolean dev_under_warranty = true;
    private String dev_comments = "";
    private double dev_price = 0;
    private ArrayList<Report> reports = new ArrayList<>();


    public Device() {


    }

    public Device(String dev_codename, String dev_model, String dev_serial, Date dev_next_maintenance) {
        this.dev_codename = dev_codename;
        this.dev_model = dev_model;
        this.dev_serial = dev_serial;
        this.dev_next_maintenance = dev_next_maintenance;
    }

    public Device(String dev_codeNumber, String dev_codename, String dev_model, String dev_serial, Date dev_install_date, Date dev_next_maintenance, Date end_of_warranty, boolean dev_under_warranty, String dev_comments, double dev_price, ArrayList<Report> reports) {
        this.dev_codeNumber = dev_codeNumber;
        this.dev_codename = dev_codename;
        this.dev_model = dev_model;
        this.dev_serial = dev_serial;
        this.dev_install_date = dev_install_date;
        this.dev_next_maintenance = dev_next_maintenance;
        this.end_of_warranty = end_of_warranty;
        this.dev_under_warranty = dev_under_warranty;
        this.dev_comments = dev_comments;
        this.dev_price = dev_price;
        this.reports = reports;

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

    public String getDev_serial() {
        return dev_serial;
    }

    public void setDev_serial(String dev_serial) {
        this.dev_serial = dev_serial;
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

    public boolean getDev_under_warranty() {
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

    public double getDev_price() {
        return dev_price;
    }

    public void setDev_price(double dev_price) {
        this.dev_price = dev_price;
    }

    public ArrayList<Report> getReports() {
        return reports;
    }

    public void setReports(ArrayList<Report> reports) {
        this.reports = reports;
    }

    @Override
    public int compareTo(@NonNull Device o) {
        return this.dev_under_warranty && !o.getDev_under_warranty() ? -1 :  //else
                !this.dev_under_warranty && o.getDev_under_warranty() ? 1 : //else
                this.dev_next_maintenance.compareTo(o.getDev_next_maintenance()) == 0 ? this.dev_codename.compareTo(o.getDev_codename()) : //else
                        this.dev_next_maintenance.compareTo(o.getDev_next_maintenance());
    }

    public Date getEnd_of_warranty() {
        return end_of_warranty;
    }

    public void setEnd_of_warranty(Date end_of_warranty) {
        this.end_of_warranty = end_of_warranty;
    }
}

