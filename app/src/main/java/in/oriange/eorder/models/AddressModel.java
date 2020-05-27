package in.oriange.eorder.models;

import java.io.Serializable;
import java.util.List;

public class AddressModel implements Serializable {


    /**
     * type : success
     * message : Address data returned successfully!
     * result : [{"user_address_id":"293","user_id":"10","Area":"Test area 2","Latitude":"12.4341","Langtitude":"45.241","Address_line1":"Subhash Nagar","Address_line2":"Barshi Road","pincode":"413512","City":"Latur","State":"Maharashtra","District":"Latur","Country":"India","created_by":"10","updated_by":"10"}]
     */

    private String type;
    private String message;
    private List<ResultBean> result;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean implements Serializable {
        /**
         * user_address_id : 293
         * user_id : 10
         * Area : Test area 2
         * Latitude : 12.4341
         * Langtitude : 45.241
         * Address_line1 : Subhash Nagar
         * Address_line2 : Barshi Road
         * pincode : 413512
         * City : Latur
         * State : Maharashtra
         * District : Latur
         * Country : India
         * created_by : 10
         * updated_by : 10
         */

        private String user_address_id;
        private String user_id;
        private String full_name;
        private String Area;
        private String Latitude;
        private String Langtitude;
        private String Address_line1;
        private String Address_line2;
        private String pincode;
        private String City;
        private String State;
        private String District;
        private String Country;
        private String created_by;
        private String updated_by;

        public String getUser_address_id() {
            return user_address_id;
        }

        public void setUser_address_id(String user_address_id) {
            this.user_address_id = user_address_id;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getFull_name() {
            if (full_name != null) {
                return full_name;
            } else {
                return "";
            }
        }

        public void setFull_name(String full_name) {
            this.full_name = full_name;
        }

        public String getArea() {
            return Area;
        }

        public void setArea(String Area) {
            this.Area = Area;
        }

        public String getLatitude() {
            return Latitude;
        }

        public void setLatitude(String Latitude) {
            this.Latitude = Latitude;
        }

        public String getLangtitude() {
            return Langtitude;
        }

        public void setLangtitude(String Langtitude) {
            this.Langtitude = Langtitude;
        }

        public String getAddress_line1() {
            return Address_line1;
        }

        public void setAddress_line1(String Address_line1) {
            this.Address_line1 = Address_line1;
        }

        public String getAddress_line2() {
            return Address_line2;
        }

        public void setAddress_line2(String Address_line2) {
            this.Address_line2 = Address_line2;
        }

        public String getPincode() {
            return pincode;
        }

        public void setPincode(String pincode) {
            this.pincode = pincode;
        }

        public String getCity() {
            return City;
        }

        public void setCity(String City) {
            this.City = City;
        }

        public String getState() {
            return State;
        }

        public void setState(String State) {
            this.State = State;
        }

        public String getDistrict() {
            return District;
        }

        public void setDistrict(String District) {
            this.District = District;
        }

        public String getCountry() {
            return Country;
        }

        public void setCountry(String Country) {
            this.Country = Country;
        }

        public String getCreated_by() {
            return created_by;
        }

        public void setCreated_by(String created_by) {
            this.created_by = created_by;
        }

        public String getUpdated_by() {
            return updated_by;
        }

        public void setUpdated_by(String updated_by) {
            this.updated_by = updated_by;
        }
    }
}
