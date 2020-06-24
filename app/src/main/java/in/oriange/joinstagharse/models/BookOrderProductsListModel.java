package in.oriange.joinstagharse.models;

import java.io.Serializable;
import java.util.List;

public class BookOrderProductsListModel implements Serializable {

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
        private boolean isAlreadyAddedInCart;
        private int quantity = 1;
        private List<String> product_images;
        private List<String> product_brouchure;
        private List<ProductCategories> product_categories;
        private List<ProductSubCategories> product_sub_categories;

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

        public boolean isAlreadyAddedInCart() {
            return isAlreadyAddedInCart;
        }

        public void setAlreadyAddedInCart(boolean alreadyAddedInCart) {
            isAlreadyAddedInCart = alreadyAddedInCart;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
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

        public List<ProductCategories> getProduct_categories() {
            return product_categories;
        }

        public void setProduct_categories(List<ProductCategories> product_categories) {
            this.product_categories = product_categories;
        }

        public List<ProductSubCategories> getProduct_sub_categories() {
            return product_sub_categories;
        }

        public void setProduct_sub_categories(List<ProductSubCategories> product_sub_categories) {
            this.product_sub_categories = product_sub_categories;
        }

        public static class ProductCategories implements Serializable {
            /**
             * {
             * "id": "13",
             * "product_id": "91",
             * "product_category_id": "4",
             * "product_category": "Beauty Products"
             * }
             */

            private String id;
            private String product_id;
            private String product_category_id;
            private String product_category;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getProduct_id() {
                return product_id;
            }

            public void setProduct_id(String product_id) {
                this.product_id = product_id;
            }

            public String getProduct_category_id() {
                return product_category_id;
            }

            public void setProduct_category_id(String product_category_id) {
                this.product_category_id = product_category_id;
            }

            public String getProduct_category() {
                return product_category;
            }

            public void setProduct_category(String product_category) {
                this.product_category = product_category;
            }
        }

        public static class ProductSubCategories implements Serializable {
            /**
             * {
             * "id": "13",
             * "product_id": "91",
             * "product_category_id": "4",
             * "product_category": "Beauty Products"
             * }
             */

            private String id;
            private String product_id;
            private String product_category_id;
            private String product_category;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getProduct_id() {
                return product_id;
            }

            public void setProduct_id(String product_id) {
                this.product_id = product_id;
            }

            public String getProduct_category_id() {
                return product_category_id;
            }

            public void setProduct_category_id(String product_category_id) {
                this.product_category_id = product_category_id;
            }

            public String getProduct_category() {
                return product_category;
            }

            public void setProduct_category(String product_category) {
                this.product_category = product_category;
            }
        }
    }
}
