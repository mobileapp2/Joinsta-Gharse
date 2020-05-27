package in.oriange.eorder.models;

import java.io.Serializable;
import java.util.List;

public class CustomerModel implements Serializable {

    /**
     * type : success
     * message : Customer returned successfully!
     * result : [{"id":"5","customer_code":"C8942","name":"Customer A","country_code":"+91","mobile":"8275460300","email":"radhika.awad@gmail.com","city":"Latur","business_code":"B001","business_name":"Business for customer","is_prime_customer":"1","is_deleted":"0","created_by":"10","updated_by":"10","created_at":"2020-05-27 09:04:52","updated_at":"2020-05-27 09:04:52"}]
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

    public static class ResultBean implements Serializable{
        /**
         * id : 5
         * customer_code : C8942
         * name : Customer A
         * country_code : +91
         * mobile : 8275460300
         * email : radhika.awad@gmail.com
         * city : Latur
         * business_code : B001
         * business_name : Business for customer
         * is_prime_customer : 1
         * is_deleted : 0
         * created_by : 10
         * updated_by : 10
         * created_at : 2020-05-27 09:04:52
         * updated_at : 2020-05-27 09:04:52
         */

        private String id;
        private String customer_code;
        private String name;
        private String country_code;
        private String mobile;
        private String email;
        private String city;
        private String business_code;
        private String business_name;
        private String is_prime_customer;
        private String is_deleted;
        private String created_by;
        private String updated_by;
        private String created_at;
        private String updated_at;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCustomer_code() {
            return customer_code;
        }

        public void setCustomer_code(String customer_code) {
            this.customer_code = customer_code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCountry_code() {
            return country_code;
        }

        public void setCountry_code(String country_code) {
            this.country_code = country_code;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getBusiness_code() {
            return business_code;
        }

        public void setBusiness_code(String business_code) {
            this.business_code = business_code;
        }

        public String getBusiness_name() {
            return business_name;
        }

        public void setBusiness_name(String business_name) {
            this.business_name = business_name;
        }

        public String getIs_prime_customer() {
            return is_prime_customer;
        }

        public void setIs_prime_customer(String is_prime_customer) {
            this.is_prime_customer = is_prime_customer;
        }

        public String getIs_deleted() {
            return is_deleted;
        }

        public void setIs_deleted(String is_deleted) {
            this.is_deleted = is_deleted;
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

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }
    }
}
