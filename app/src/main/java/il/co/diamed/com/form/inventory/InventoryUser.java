package il.co.diamed.com.form.inventory;

import androidx.annotation.NonNull;
import android.util.Log;

public class InventoryUser implements Comparable<InventoryUser> {
    private String id;
    private String inStock;
    private String name;

    public InventoryUser() {

    }

    public InventoryUser(String id, String name, String inStock) {
        this.id = id;
        this.name = name;
        this.inStock = inStock;
    }

    public InventoryUser(InventoryUser item) {
        this.id = item.id;
        this.inStock = item.inStock;
        this.name = item.name;
    }

    @Override
    public int compareTo(@NonNull InventoryUser o) {
        return this.inStock.compareTo(o.getInStock());
    }

    public String getInStock() {
        return inStock;
    }

    public String getName() {
        return name;
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

    public void setName(String name) {
        this.name = name;
    }

}
