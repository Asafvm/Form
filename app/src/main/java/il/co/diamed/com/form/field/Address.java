package il.co.diamed.com.form.field;

public class Address {
    private String city;
    private String street;
    private String h_num;  //house number

    public Address(){

    }
    public Address(String city, String street, String h_num) {
        this.city = city;
        this.street = street;
        this.h_num = h_num;
    }


    public String getH_num() {
        return h_num;
    }

    public void setH_num(String h_num) {
        this.h_num = h_num;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return this.street + " "+ this.h_num+ ", "+this.city;
    }
}
