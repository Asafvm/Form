package il.co.diamed.com.form.inventory;

import android.support.annotation.NonNull;

public class InventoryItem implements Comparable {
    private String id;
    private String description;
    private String inStock;
    private String serial;
    private String supplier;
    private String company;
    private String cost;
    private String row;
    private String drawer;

    private String area;
    private String minimum;
    private String device;
    private String active;

    public InventoryItem() {

    }

    public InventoryItem(String serial, String description, String inStock) {
        this.serial = serial;
        this.description = description;
        this.inStock = inStock;
    }

    public InventoryItem(InventoryItem item) {
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
        this.minimum = item.minimum;
        this.supplier = item.supplier;
    }


    @Override
    public int compareTo(@NonNull Object o) {
        return 0;//(this.id.compareTo(((InventoryItem)o).getId()));

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

    public String getId() {
        return id;
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

    public String getMinimum() {
        return minimum;
    }

    public String getDevice() {
        return device;
    }

    public void setinStock(String inStock) {
        this.inStock = inStock;
    }

    public void setMinimum(String minimum) {
        this.minimum = minimum;
    }
}
