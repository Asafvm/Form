package il.co.diamed.com.form.data_objects;

import android.support.annotation.NonNull;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;

@IgnoreExtraProperties
public class Location implements Comparable<Location>{
    private String name = "";
    private Address address = new Address("","","");
    private String comments = "";
    private double latitude = -1;
    private double longtitude = -1;
    private ArrayList<SubLocation> SubLocation = new ArrayList<>();

    public Location(){

    }

    public Location(String name, Address address, String comments, ArrayList<SubLocation> SubLocation) {
        this.name = name;
        this.address = address;
        this.comments = comments;
        this.SubLocation = SubLocation;
    }

    public Location(String name, Address address, String comments) {
        this.name = name;
        this.address = address;
        this.comments = comments;
        this.SubLocation = new ArrayList<>();
    }


    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
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

    public ArrayList<SubLocation> getSubLocation() {
        return SubLocation;
    }

    public void setSubLocation(ArrayList<SubLocation> SubLocation) {
        this.SubLocation = SubLocation;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
            this.latitude = latitude;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }

    public void addSublocation(SubLocation subLocation) {
        SubLocation.add(subLocation);
    }

    @Override
    public int compareTo(@NonNull Location o) {
        return name.compareTo(o.getName());
    }


}
