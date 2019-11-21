package il.co.diamed.com.form.data_objects;

public class Address {


    private String loc_Country;
    private String loc_Area;
    private String loc_Locality;
    private String loc_Thoroughfare;
    private String loc_SubThoroughfare;

    public Address(){}

    public Address(String loc_Country, String loc_Area, String loc_Locality, String loc_Thoroughfare, String loc_SubThoroughfare){

        this.loc_Country = loc_Country;
        this.loc_Area = loc_Area;
        this.loc_Locality = loc_Locality;
        this.loc_Thoroughfare = loc_Thoroughfare;
        this.loc_SubThoroughfare = loc_SubThoroughfare;
    }

    @Override
    public String toString() {
        return this.loc_Thoroughfare + " "+ this.loc_SubThoroughfare + ", "+this.loc_Locality;
    }

    public String getLoc_Country() { return loc_Country; }

    public String getLoc_Area() {
        return loc_Area;
    }


    public String getloc_Locality() {
        return loc_Locality;
    }


    public String getloc_Thoroughfare() {
        return loc_Thoroughfare;
    }


    public String getloc_SubThoroughfare() {
        return loc_SubThoroughfare;
    }


    public void setLoc_Country(String loc_Country) {
        this.loc_Country = loc_Country;
    }

    public void setLoc_Area(String loc_Area) {
        this.loc_Area = loc_Area;
    }

    public void setLoc_Locality(String loc_Locality) {
        this.loc_Locality = loc_Locality;
    }

    public void setLoc_Thoroughfare(String loc_Thoroughfare) {
        this.loc_Thoroughfare = loc_Thoroughfare;
    }

    public void setLoc_SubThoroughfare(String loc_SubThoroughfare) {
        this.loc_SubThoroughfare = loc_SubThoroughfare;
    }

}
