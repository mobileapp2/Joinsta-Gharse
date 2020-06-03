package in.oriange.joinstagharse.models;

public class ContactsModel {

    private String initLetter;
    private String name;
    private String phoneNo;

    public ContactsModel(String initLetter, String name, String phoneNo) {
        this.initLetter = initLetter;
        this.name = name;
        this.phoneNo = phoneNo;
    }

    public String getInitLetter() {
        return initLetter;
    }

    public void setInitLetter(String initLetter) {
        this.initLetter = initLetter;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ContactsModel) {
            ContactsModel temp = (ContactsModel) obj;
            return this.phoneNo.equals(temp.phoneNo);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (/*this.name.hashCode() + */this.phoneNo.hashCode());

    }
}
