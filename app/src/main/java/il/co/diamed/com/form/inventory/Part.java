package il.co.diamed.com.form.inventory;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties

public class Part implements Comparable<Part> {
    private String description;
    private String inStock;
    private String serial;
    private String supplier;
    private String company;
    private String cost;
    private String row;
    private String drawer;
    private String area;
    private String minimum_lab;
    private String minimum_car;
    private String device;
    private String active;
    private String id;

    public Part() {

    }
/*
    public Part(String serial, String description, String inStock) {
        this.serial = serial;
        this.description = description;
        this.inStock = inStock;
    }

    public Part(Part item) {
        this.id = item.id;
        this.serial = item.serial;
        this.description = item.description;
        this.inStock = item.inStock;
        this.active = item.active;
        this.area = item.area;
        this.row = item.row;
        this.drawer = item.drawer;
        this.company = item.company;
        this.cost = item.cost;
        this.device = item.device;
        this.minimum_lab = item.minimum_lab;
        this.minimum_car = item.minimum_car;
        this.supplier = item.supplier;
    }
*/

    @Override
    public int compareTo(@NonNull Part o) {
        return this.serial.compareTo(o.getSerial());

    }


    public String getMinimum_lab() {
        return minimum_lab;
    }

    public void setMinimum_lab(String minimum_lab) {
        this.minimum_lab = minimum_lab;
    }

    public String getMinimum_car() {
        return minimum_car;
    }

    public void setMinimum_car(String minimum_car) {
        this.minimum_car = minimum_car;
    }

    public String getInStock() {
        return inStock;
    }

    public String getDescription() {
        return description;
    }

    public String getActive() {
        return active;
    }


    public String getSupplier() {
        return supplier;
    }

    public String getSerial() {
        return serial;
    }

    public String getCost() {
        return cost;
    }

    public String getCompany() {
        return company;
    }

    public String getRow() {
        return row;
    }

    public String getDrawer() {
        return drawer;
    }

    public String getArea() {
        return area;
    }

    public String getDevice() {
        return device;
    }

    public String getId() {
        return id;
    }

    public void setinStock(String inStock) {
        try {
            if (Integer.valueOf(inStock) >= 0)
                this.inStock = inStock;
        }catch (Exception e){
            Log.e("Part", "Invalid stock value: "+inStock);
        }
    }

    public void setId(String id) {
        this.id = id;
    }
}
