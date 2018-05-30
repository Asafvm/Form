package il.co.diamed.com.form.res;

import android.provider.ContactsContract;

public class ObjectLocation {
    private String locAddress;
    private String locName;
    private String locPrimaryMail;
    private String locPhone;

    public ObjectLocation(){

    }

    public ObjectLocation(String name, String address, String mail, String phone){
        this.locName = name;
        this.locAddress = address;
        this.locPrimaryMail = mail;
        this.locPhone = phone;
    }


    public String getLocAddress() {
        return locAddress;
    }

    public String getLocName() {
        return locName;
    }

    public String getLocPrimaryMail() {
        return locPrimaryMail;
    }

    public String getLocPhone() {
        return locPhone;
    }

    @Override
    public String toString() {
        return getLocName()+":\nAddress: "+getLocAddress()+"\neMail: "+getLocPrimaryMail()+"\n";
    }
}
