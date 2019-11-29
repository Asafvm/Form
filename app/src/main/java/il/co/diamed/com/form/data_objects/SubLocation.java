package il.co.diamed.com.form.data_objects;

import androidx.annotation.NonNull;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;

@IgnoreExtraProperties
public class SubLocation implements Comparable<SubLocation>{

    private String name;
    //private Phone phone;
    private HashMap<String, Contact> contacts;
    private HashMap<String, FieldDevice> devices;
    private String comments;

    public SubLocation() {

    }

    public SubLocation(String name, String comments, HashMap<String, Contact> contacts, HashMap<String, FieldDevice> devices) {

        this.name = name;
        //this.phone = phone;
        this.comments = comments;
        this.contacts = contacts;
        this.devices = devices;
    }

    public SubLocation(String name, String comments) {

        this.name = name;
        this.comments = comments;
        this.contacts = new HashMap<>();
        this.devices = new HashMap<>();
    }


    public HashMap<String, Contact> getContacts() {
        return contacts;
    }

    public void setContacts(HashMap<String, Contact> contacts) {
        this.contacts = contacts;
    }

    public HashMap<String, FieldDevice> getDevices() {
        return devices == null ? new HashMap<>() : devices;
    }
/*
    public void setDevices(ArrayList<PrototypeDevice> devices) {
        if(this.devices == null)
            this.devices = new HashMap<>();
        if (devices != null)
            for (PrototypeDevice labDevice : devices)
                if (labDevice != null)
                    this.devices.put(labDevice.getDev_type(), labDevice);
        //this.devices = devices;
    }
*/
    public void setDevices(HashMap<String, FieldDevice> devices) {
        this.devices = devices;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addDevice(FieldDevice device) {
        if (devices == null)
            devices = new HashMap<>();
        devices.put(device.getDev_serial(), device);
    }

    @Override
    public int compareTo(@NonNull SubLocation o) {
        return this.name.compareTo(o.getName());
    }

}
