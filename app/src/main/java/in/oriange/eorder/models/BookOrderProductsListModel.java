package in.oriange.eorder.models;

import java.io.Serializable;
import java.util.List;

public class BookOrderProductsListModel implements Serializable {

    /**
     * type : success
     * message : Product data returned successfully!
     * result : [{"id":"19","name":"Product 001","description":"Test description","unit_of_measure":"BGS","max_retail_price":"120","selling_price":"120","product_code":"P001","category_id":"1","stock":"100","in_stock":"1","is_featured":"1","is_show_in_list":"0","is_inclusive_tax":"1","business_id":"6","created_by":"10","updated_by":"10","created_at":"2020-05-27 15:42:16","updated_at":"2020-05-27 15:42:16","unit_of_measure_id":"2","product_images":["test.png"],"product_brouchure":["new_test.png"],"product_category_name":"tt jj"},{"id":"14","name":"Product 001","description":"Test description","unit_of_measure":"BGS","max_retail_price":"120","selling_price":"120","product_code":"P001","category_id":"1","stock":"100","in_stock":"1","is_featured":"1","is_show_in_list":"1","is_inclusive_tax":"1","business_id":"6","created_by":"10","updated_by":"10","created_at":"2020-05-26 20:08:01","updated_at":"2020-05-26 20:08:01","unit_of_measure_id":"2","product_images":["test.png"],"product_brouchure":["new_test.png"],"product_category_name":"tt jj"},{"id":"2","name":"Product 02","description":"Test description","unit_of_measure":"BGS","max_retail_price":"120","selling_price":"120","product_code":"P002","category_id":"1","stock":"100","in_stock":"1","is_featured":"1","is_show_in_list":"1","is_inclusive_tax":"0","business_id":"6","created_by":"10","updated_by":"10","created_at":"2020-05-21 15:04:27","updated_at":"2020-05-21 15:04:27","unit_of_measure_id":"2","product_images":["test3-20200513123253.jpg"],"product_brouchure":["test.png"],"product_category_name":"tt jj"}]
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
         * id : 19
         * name : Product 001
         * description : Test description
         * unit_of_measure : BGS
         * max_retail_price : 120
         * selling_price : 120
         * product_code : P001
         * category_id : 1
         * stock : 100
         * in_stock : 1
         * is_featured : 1
         * is_show_in_list : 0
         * is_inclusive_tax : 1
         * business_id : 6
         * created_by : 10
         * updated_by : 10
         * created_at : 2020-05-27 15:42:16
         * updated_at : 2020-05-27 15:42:16
         * unit_of_measure_id : 2
         * product_images : ["test.png"]
         * product_brouchure : ["new_test.png"]
         * product_category_name : tt jj
         */

        private String id;
        private String name;
        private String description;
        private String unit_of_measure;
        private String max_retail_price;
        private String selling_price;
        private String product_code;
        private String category_id;
        private String stock;
        private String in_stock;
        private String is_featured;
        private String is_show_in_list;
        private String is_inclusive_tax;
        private String business_id;
        private String created_by;
        private String updated_by;
        private String created_at;
        private String updated_at;
        private String unit_of_measure_id;
        private String product_category_name;
        private List<String> product_images;
        private List<String> product_brouchure;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getUnit_of_measure() {
            return unit_of_measure;
        }

        public void setUnit_of_measure(String unit_of_measure) {
            this.unit_of_measure = unit_of_measure;
        }

        public String getMax_retail_price() {
            if (max_retail_price != null)
                if (!max_retail_price.equals(""))
                    return max_retail_price;
                else
                    return "0";
            else
                return "0";

//            return "100";
        }

        public void setMax_retail_price(String max_retail_price) {
            this.max_retail_price = max_retail_price;
        }

        public String getSelling_price() {
            if (selling_price != null)
                if (!selling_price.equals(""))
                    return selling_price;
                else
                    return "0";
            else
                return "0";

//            return "70";
        }

        public void setSelling_price(String selling_price) {
            this.selling_price = selling_price;
        }

        public String getProduct_code() {
            return product_code;
        }

        public void setProduct_code(String product_code) {
            this.product_code = product_code;
        }

        public String getCategory_id() {
            return category_id;
        }

        public void setCategory_id(String category_id) {
            this.category_id = category_id;
        }

        public String getStock() {
            return stock;
        }

        public void setStock(String stock) {
            this.stock = stock;
        }

        public String getIn_stock() {
            return in_stock;
        }

        public void setIn_stock(String in_stock) {
            this.in_stock = in_stock;
        }

        public String getIs_featured() {
            return is_featured;
        }

        public void setIs_featured(String is_featured) {
            this.is_featured = is_featured;
        }

        public String getIs_show_in_list() {
            return is_show_in_list;
        }

        public void setIs_show_in_list(String is_show_in_list) {
            this.is_show_in_list = is_show_in_list;
        }

        public String getIs_inclusive_tax() {
            return is_inclusive_tax;
        }

        public void setIs_inclusive_tax(String is_inclusive_tax) {
            this.is_inclusive_tax = is_inclusive_tax;
        }

        public String getBusiness_id() {
            return business_id;
        }

        public void setBusiness_id(String business_id) {
            this.business_id = business_id;
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

        public String getUnit_of_measure_id() {
            return unit_of_measure_id;
        }

        public void setUnit_of_measure_id(String unit_of_measure_id) {
            this.unit_of_measure_id = unit_of_measure_id;
        }

        public String getProduct_category_name() {
            return product_category_name;
        }

        public void setProduct_category_name(String product_category_name) {
            this.product_category_name = product_category_name;
        }

        public List<String> getProduct_images() {
            return product_images;
        }

        public void setProduct_images(List<String> product_images) {
            this.product_images = product_images;
        }

        public List<String> getProduct_brouchure() {
            return product_brouchure;
        }

        public void setProduct_brouchure(List<String> product_brouchure) {
            this.product_brouchure = product_brouchure;
        }
    }
}
