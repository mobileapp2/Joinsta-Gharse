package in.oriange.joinstagharse.models;

import java.util.List;

public class ProductsCategoryModel {

    /**
     * type : success
     * message : Product Categories retuned successfully!
     * result : [{"id":"2","business_category_id":"1","product_category":"tt jj","created_at":"2020-05-21 15:01:44","updated_at":"2020-05-21 15:01:44","created_by":"1","updated_by":"1","business_id":"1","type_id":"1","sub_type_id":"10"},{"id":"2","business_category_id":"1","product_category":"tt jj dddd","created_at":"2020-05-21 15:01:44","updated_at":"2020-05-21 15:01:44","created_by":"1","updated_by":"1","business_id":"1","type_id":"1","sub_type_id":"10"}]
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

    public static class ResultBean {
        /**
         * id : 2
         * business_category_id : 1
         * product_category : tt jj
         * created_at : 2020-05-21 15:01:44
         * updated_at : 2020-05-21 15:01:44
         * created_by : 1
         * updated_by : 1
         * business_id : 1
         * type_id : 1
         * sub_type_id : 10
         */

        private String id;
        private String business_category_id;
        private String product_category;
        private String created_at;
        private String updated_at;
        private String created_by;
        private String updated_by;
        private String business_id;
        private String type_id;
        private String sub_type_id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getBusiness_category_id() {
            return business_category_id;
        }

        public void setBusiness_category_id(String business_category_id) {
            this.business_category_id = business_category_id;
        }

        public String getProduct_category() {
            return product_category;
        }

        public void setProduct_category(String product_category) {
            this.product_category = product_category;
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

        public String getBusiness_id() {
            return business_id;
        }

        public void setBusiness_id(String business_id) {
            this.business_id = business_id;
        }

        public String getType_id() {
            return type_id;
        }

        public void setType_id(String type_id) {
            this.type_id = type_id;
        }

        public String getSub_type_id() {
            return sub_type_id;
        }

        public void setSub_type_id(String sub_type_id) {
            this.sub_type_id = sub_type_id;
        }
    }
}
