package in.oriange.joinstagharse.models;

import java.io.Serializable;
import java.util.List;

public class ProductCategoriesModel implements Serializable {

    /**
     * type : success
     * message : Product data returned successfully!
     * result : [{"id":"2","business_category_id":"2","product_category":"Fruits & Vegetable","level":"0","parent_id":"0","is_approved":"1","created_at":"2020-05-30 14:21:13","updated_at":"2020-05-30 14:21:13","created_by":"0","updated_by":"0","name":"Daily Need","sub_categories":[{"id":"57","business_category_id":"2","product_category":"Roots","level":"1","parent_id":"2","is_approved":"0","created_at":"2020-06-19 15:25:06","updated_at":"2020-06-19 15:25:06","created_by":"1","updated_by":"1","name":"Daily Need"}]},{"id":"54","business_category_id":"2","product_category":"Dry Fruits","level":"0","parent_id":"0","is_approved":"0","created_at":"2020-06-19 15:21:48","updated_at":"2020-06-19 15:24:02","created_by":"1","updated_by":"1","name":"Daily Need","sub_categories":[{"id":"56","business_category_id":"2","product_category":"Groundnuts","level":"1","parent_id":"54","is_approved":"0","created_at":"2020-06-19 15:24:26","updated_at":"2020-06-19 15:24:26","created_by":"1","updated_by":"1","name":"Daily Need"}]}]
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

    public static class ResultBean  implements Serializable{
        /**
         * id : 2
         * business_category_id : 2
         * product_category : Fruits & Vegetable
         * level : 0
         * parent_id : 0
         * is_approved : 1
         * created_at : 2020-05-30 14:21:13
         * updated_at : 2020-05-30 14:21:13
         * created_by : 0
         * updated_by : 0
         * name : Daily Need
         * sub_categories : [{"id":"57","business_category_id":"2","product_category":"Roots","level":"1","parent_id":"2","is_approved":"0","created_at":"2020-06-19 15:25:06","updated_at":"2020-06-19 15:25:06","created_by":"1","updated_by":"1","name":"Daily Need"}]
         */

        private String id;
        private String business_category_id;
        private String product_category;
        private String level;
        private String parent_id;
        private String is_approved;
        private String created_at;
        private String updated_at;
        private String created_by;
        private String updated_by;
        private String name;
        private List<SubCategoriesBean> sub_categories;

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

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public String getParent_id() {
            return parent_id;
        }

        public void setParent_id(String parent_id) {
            this.parent_id = parent_id;
        }

        public String getIs_approved() {
            return is_approved;
        }

        public void setIs_approved(String is_approved) {
            this.is_approved = is_approved;
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

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<SubCategoriesBean> getSub_categories() {
            return sub_categories;
        }

        public void setSub_categories(List<SubCategoriesBean> sub_categories) {
            this.sub_categories = sub_categories;
        }

        public static class SubCategoriesBean  implements Serializable{
            /**
             * id : 57
             * business_category_id : 2
             * product_category : Roots
             * level : 1
             * parent_id : 2
             * is_approved : 0
             * created_at : 2020-06-19 15:25:06
             * updated_at : 2020-06-19 15:25:06
             * created_by : 1
             * updated_by : 1
             * name : Daily Need
             */

            private String id;
            private String business_category_id;
            private String product_category;
            private String level;
            private String parent_id;
            private String is_approved;
            private String created_at;
            private String updated_at;
            private String created_by;
            private String updated_by;
            private String name;

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

            public String getLevel() {
                return level;
            }

            public void setLevel(String level) {
                this.level = level;
            }

            public String getParent_id() {
                return parent_id;
            }

            public void setParent_id(String parent_id) {
                this.parent_id = parent_id;
            }

            public String getIs_approved() {
                return is_approved;
            }

            public void setIs_approved(String is_approved) {
                this.is_approved = is_approved;
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

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }
    }
}
