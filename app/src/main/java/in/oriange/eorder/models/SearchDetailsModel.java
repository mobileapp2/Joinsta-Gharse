package in.oriange.eorder.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SearchDetailsModel implements Serializable {

    /**
     * type : success
     * message : Search details returned successfully!
     * result : {"businesses":[{"id":"6","order_online":"","business_code":"L00006","address":"Latur, Maharashtra, India","city":"Latur","country":"India","district":"Latur","pincode":"413531","longitude":"Latur, Maharashtra, India","latitude":"18.4087934","landmark":"","business_name":"Test","locality":"Latur","state":"Maharashtra","designation":"Proprietor","is_enquiry_available":"1","email":"","website":"","record_statusid":"1","created_at":"2020-05-21 12:52:31","updated_at":"2020-05-21 12:52:31","created_by":"10","updated_by":"10","type_description":"Hotel & Restaurant","IsFavourite":"0","image_url":"","bank_id":"6","account_holder_name":"","bank_alias":"","bank_name":"","ifsc_code":"","account_no":"","bank_status":"online","tax_id":"6","tax_name":"","tax_alias":"","pan_number":"","gst_number":"","tax_status":"online","type_id":"121","can_book_order":"0","avg_rating":0,"review_title_by_user":"","review_description_by_user":"","rating_by_user":0,"total_number_review":"0","offer_count":0,"mobiles":[[{"id":"6","mobile_number":"8164983948","country_code":"+91"}]],"sub_categories":[[{"sub_type_id":"42","subtype_description":"Hotel"}]],"landline":[[{"id":"6","landline_number":"0256481378","country_code":"+91"}]],"tag":[[{"id":"13","tag_name":"book hotel online"}]],"common_groups_count":0,"common_groups_data":[]}]}
     */

    private String type;
    private String message;
    private ResultBean result;

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

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean implements Serializable {
        private List<BusinessesBean> businesses;

        public List<BusinessesBean> getBusinesses() {
            return businesses;
        }

        public void setBusinesses(List<BusinessesBean> businesses) {
            this.businesses = businesses;
        }

        public static class BusinessesBean implements Serializable {
            private String id;
            private String order_online;
            private String business_code;
            private String address;
            private String city;
            private String country;
            private String district;
            private String pincode;
            private String longitude;
            private String latitude;
            private String landmark;
            private String business_name;
            private String locality;
            private String state;
            private String designation;
            private String is_enquiry_available;
            private String email;
            private String website;
            private String record_statusid;
            private String created_at;
            private String updated_at;
            private String created_by;
            private String updated_by;
            private String type_description;
            private String IsFavourite;
            private String image_url;
            private String bank_id;
            private String account_holder_name;
            private String bank_alias;
            private String bank_name;
            private String ifsc_code;
            private String account_no;
            private String bank_status;
            private String tax_id;
            private String tax_name;
            private String tax_alias;
            private String pan_number;
            private String gst_number;
            private String tax_status;
            private String type_id;
            private String can_book_order;
            private String avg_rating;
            private String review_title_by_user;
            private String review_description_by_user;
            private String rating_by_user;
            private String total_number_review;
            private String offer_count;
            private String common_groups_count;
            private List<List<MobilesBeanXX>> mobiles;
            private List<List<SubCategoriesBean>> sub_categories;
            private List<List<LandlineBeanXX>> landline;
            private List<List<TagBean>> tag;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getOrder_online() {
                return order_online;
            }

            public void setOrder_online(String order_online) {
                this.order_online = order_online;
            }

            public String getBusiness_code() {
                return business_code;
            }

            public void setBusiness_code(String business_code) {
                this.business_code = business_code;
            }

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }

            public String getCity() {
                return city;
            }

            public void setCity(String city) {
                this.city = city;
            }

            public String getCountry() {
                return country;
            }

            public void setCountry(String country) {
                this.country = country;
            }

            public String getDistrict() {
                return district;
            }

            public void setDistrict(String district) {
                this.district = district;
            }

            public String getPincode() {
                return pincode;
            }

            public void setPincode(String pincode) {
                this.pincode = pincode;
            }

            public String getLongitude() {
                return longitude;
            }

            public void setLongitude(String longitude) {
                this.longitude = longitude;
            }

            public String getLatitude() {
                return latitude;
            }

            public void setLatitude(String latitude) {
                this.latitude = latitude;
            }

            public String getLandmark() {
                return landmark;
            }

            public void setLandmark(String landmark) {
                this.landmark = landmark;
            }

            public String getBusiness_name() {
                return business_name;
            }

            public void setBusiness_name(String business_name) {
                this.business_name = business_name;
            }

            public String getLocality() {
                return locality;
            }

            public void setLocality(String locality) {
                this.locality = locality;
            }

            public String getState() {
                return state;
            }

            public void setState(String state) {
                this.state = state;
            }

            public String getDesignation() {
                return designation;
            }

            public void setDesignation(String designation) {
                this.designation = designation;
            }

            public String getIs_enquiry_available() {
                return is_enquiry_available;
            }

            public void setIs_enquiry_available(String is_enquiry_available) {
                this.is_enquiry_available = is_enquiry_available;
            }

            public String getEmail() {
                return email;
            }

            public void setEmail(String email) {
                this.email = email;
            }

            public String getWebsite() {
                return website;
            }

            public void setWebsite(String website) {
                this.website = website;
            }

            public String getRecord_statusid() {
                return record_statusid;
            }

            public void setRecord_statusid(String record_statusid) {
                this.record_statusid = record_statusid;
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

            public String getType_description() {
                return type_description;
            }

            public void setType_description(String type_description) {
                this.type_description = type_description;
            }

            public String getIsFavourite() {
                return IsFavourite;
            }

            public void setIsFavourite(String IsFavourite) {
                this.IsFavourite = IsFavourite;
            }

            public String getImage_url() {
                return image_url;
            }

            public void setImage_url(String image_url) {
                this.image_url = image_url;
            }

            public String getBank_id() {
                if (bank_id != null) {
                    return bank_id;
                } else {
                    return "";
                }
            }

            public void setBank_id(String bank_id) {
                this.bank_id = bank_id;
            }

            public String getAccount_holder_name() {
                if (account_holder_name != null) {
                    return account_holder_name;
                } else {
                    return "";
                }
            }

            public void setAccount_holder_name(String account_holder_name) {
                this.account_holder_name = account_holder_name;
            }

            public String getBank_alias() {
                if (bank_alias != null) {
                    return bank_alias;
                } else {
                    return "";
                }
            }

            public void setBank_alias(String bank_alias) {
                this.bank_alias = bank_alias;
            }

            public String getBank_name() {
                if (bank_name != null) {
                    return bank_name;
                } else {
                    return "";
                }
            }

            public void setBank_name(String bank_name) {
                this.bank_name = bank_name;
            }

            public String getIfsc_code() {
                if (ifsc_code != null) {
                    return ifsc_code;
                } else {
                    return "";
                }
            }

            public void setIfsc_code(String ifsc_code) {
                this.ifsc_code = ifsc_code;
            }

            public String getAccount_no() {
                if (account_no != null) {
                    return account_no;
                } else {
                    return "";
                }
            }

            public void setAccount_no(String account_no) {
                this.account_no = account_no;
            }

            public String getBank_status() {
                if (bank_status != null) {
                    return bank_status;
                } else {
                    return "";
                }
            }

            public void setBank_status(String bank_status) {
                this.bank_status = bank_status;
            }

            public String getTax_id() {
                if (tax_id != null) {
                    return tax_id;
                } else {
                    return "";
                }
            }

            public void setTax_id(String tax_id) {
                this.tax_id = tax_id;
            }

            public String getTax_name() {
                if (tax_name != null) {
                    return tax_name;
                } else {
                    return "";
                }
            }

            public void setTax_name(String tax_name) {
                this.tax_name = tax_name;
            }

            public String getTax_alias() {
                if (tax_alias != null) {
                    return tax_alias;
                } else {
                    return "";
                }
            }

            public void setTax_alias(String tax_alias) {
                this.tax_alias = tax_alias;
            }

            public String getPan_number() {
                if (pan_number != null) {
                    return pan_number;
                } else {
                    return "";
                }
            }

            public void setPan_number(String pan_number) {
                this.pan_number = pan_number;
            }

            public String getGst_number() {
                if (gst_number != null) {
                    return gst_number;
                } else {
                    return "";
                }
            }

            public void setGst_number(String gst_number) {
                this.gst_number = gst_number;
            }

            public String getTax_status() {
                if (tax_status != null) {
                    return tax_status;
                } else {
                    return "";
                }
            }

            public void setTax_status(String tax_status) {
                this.tax_status = tax_status;
            }

            public String getType_id() {
                return type_id;
            }

            public void setType_id(String type_id) {
                this.type_id = type_id;
            }

            public String getCan_book_order() {
                return can_book_order;
            }

            public void setCan_book_order(String can_book_order) {
                this.can_book_order = can_book_order;
            }

            public String getAvg_rating() {
                return avg_rating;
            }

            public void setAvg_rating(String avg_rating) {
                this.avg_rating = avg_rating;
            }

            public String getReview_title_by_user() {
                return review_title_by_user;
            }

            public void setReview_title_by_user(String review_title_by_user) {
                this.review_title_by_user = review_title_by_user;
            }

            public String getReview_description_by_user() {
                return review_description_by_user;
            }

            public void setReview_description_by_user(String review_description_by_user) {
                this.review_description_by_user = review_description_by_user;
            }

            public String getRating_by_user() {
                return rating_by_user;
            }

            public void setRating_by_user(String rating_by_user) {
                this.rating_by_user = rating_by_user;
            }

            public String getTotal_number_review() {
                return total_number_review;
            }

            public void setTotal_number_review(String total_number_review) {
                this.total_number_review = total_number_review;
            }

            public String getOffer_count() {
                if (can_book_order != null) {
                    return can_book_order;
                } else {
                    return "0";
                }
            }

            public void setOffer_count(String offer_count) {
                this.offer_count = offer_count;
            }

            public String getCommon_groups_count() {
                return common_groups_count;
            }

            public void setCommon_groups_count(String common_groups_count) {
                this.common_groups_count = common_groups_count;
            }

            public List<List<MobilesBeanXX>> getMobiles() {
                if (mobiles != null) {
                    return mobiles;
                } else {
                    return new ArrayList<>();
                }
            }

            public void setMobiles(List<List<MobilesBeanXX>> mobiles) {
                this.mobiles = mobiles;
            }

            public List<List<SubCategoriesBean>> getSub_categories() {
                return sub_categories;
            }

            public void setSub_categories(List<List<SubCategoriesBean>> sub_categories) {
                this.sub_categories = sub_categories;
            }

            public List<List<LandlineBeanXX>> getLandline() {
                return landline;
            }

            public void setLandline(List<List<LandlineBeanXX>> landline) {
                this.landline = landline;
            }

            public List<List<TagBean>> getTag() {
                if (tag != null) {
                    return tag;
                } else {
                    return new ArrayList<>();
                }
            }

            public void setTag(List<List<TagBean>> tag) {
                this.tag = tag;
            }

            public String getTypeSubTypeName() {
                StringBuilder subTypeNameSb = new StringBuilder();
                String subTypeNameStr = "";

                for (SearchDetailsModel.ResultBean.BusinessesBean.SubCategoriesBean subCategoriesBean : getSub_categories().get(0)) {
                    subTypeNameSb.append(subCategoriesBean.getSubtype_description()).append(" | ");
                }
                subTypeNameStr = subTypeNameSb.toString();
                if (!subTypeNameStr.equals(""))
                    subTypeNameStr = subTypeNameStr.substring(0, subTypeNameStr.length() - 3);

                if (!getType_description().trim().isEmpty() && !subTypeNameStr.trim().isEmpty()) {
                    return getType_description() + " | " + subTypeNameStr;
                } else if (getType_description().trim().isEmpty() && subTypeNameStr.trim().isEmpty()) {
                    return "";
                } else if (!getType_description().trim().isEmpty() && subTypeNameStr.trim().isEmpty()) {
                    return getType_description();
                } else if (getType_description().trim().isEmpty() && !subTypeNameStr.trim().isEmpty()) {
                    return subTypeNameStr;
                } else {
                    return "";
                }

            }

            public String getAddressCityPincode() {
                if (!getAddress().trim().isEmpty()) {
                    return getAddress().trim();
                } else if (getAddress().trim().isEmpty() && !getCity().trim().isEmpty() && !getPincode().trim().isEmpty()) {
                    return getCity().trim().isEmpty() + ", " + getPincode().trim().isEmpty();
                } else if (getAddress().trim().isEmpty() && !getCity().trim().isEmpty() && getPincode().trim().isEmpty()) {
                    return getCity().trim();
                } else if (getAddress().trim().isEmpty() && getCity().trim().isEmpty() && !getPincode().trim().isEmpty()) {
                    return getPincode().trim();
                } else if (getAddress().trim().isEmpty() && getCity().trim().isEmpty() && getPincode().trim().isEmpty()) {
                    return "";
                } else {
                    return "";
                }
            }

            public List<String> getSubTypesTagsList(String type) {   //    type == 1 (return subtype and tags)  type == 2 (return tags only)
                List<String> list = new ArrayList<>();

                if (type.equals("1"))
                    if (getSub_categories().get(0) != null)
                        if (getSub_categories().get(0).size() > 0)
                            for (SearchDetailsModel.ResultBean.BusinessesBean.SubCategoriesBean subCategoriesBean : getSub_categories().get(0))
                                list.add(subCategoriesBean.getSubtype_description());

                if (getTag().get(0) != null)
                    if (getTag().get(0).size() > 0)
                        for (SearchDetailsModel.ResultBean.BusinessesBean.TagBean tags : getTag().get(0))
                            if (!tags.getTag_name().trim().equals(""))
                                list.add(tags.getTag_name());


                return list;
            }

            public static class MobilesBeanXX implements Serializable {
                /**
                 * id : 6
                 * mobile_number : 8164983948
                 * country_code : +91
                 */

                private String id;
                private String mobile_number;
                private String country_code;

                public String getId() {
                    return id;
                }

                public void setId(String id) {
                    this.id = id;
                }

                public String getMobile_number() {
                    return mobile_number.replace("-", "").replace("+", "");
                }

                public void setMobile_number(String mobile_number) {
                    this.mobile_number = mobile_number;
                }

                public String getCountry_code() {
                    if (country_code != null) {
                        if (!country_code.equals("")) {
                            if (country_code.startsWith("+")) {
                                return country_code;
                            } else {
                                return "+" + country_code;
                            }
                        } else {
                            return "+91";
                        }
                    } else {
                        return "+91";
                    }
                }

                public void setCountry_code(String country_code) {
                    this.country_code = country_code;
                }
            }

            public static class SubCategoriesBean implements Serializable {
                /**
                 * sub_type_id : 42
                 * subtype_description : Hotel
                 */

                private String sub_type_id;
                private String subtype_description;

                public String getSub_type_id() {
                    return sub_type_id;
                }

                public void setSub_type_id(String sub_type_id) {
                    this.sub_type_id = sub_type_id;
                }

                public String getSubtype_description() {
                    return subtype_description;
                }

                public void setSubtype_description(String subtype_description) {
                    this.subtype_description = subtype_description;
                }
            }

            public static class LandlineBeanXX implements Serializable {
                /**
                 * id : 6
                 * landline_number : 0256481378
                 * country_code : +91
                 */

                private String id;
                private String landline_number;
                private String country_code;

                public String getId() {
                    return id;
                }

                public void setId(String id) {
                    this.id = id;
                }

                public String getLandline_number() {
                    return landline_number.replace("-", "").replace("+", "");
                }

                public void setLandline_number(String landline_number) {
                    this.landline_number = landline_number;
                }

                public String getCountry_code() {
                    if (country_code != null) {
                        if (!country_code.equals("")) {
                            if (country_code.startsWith("+")) {
                                return country_code;
                            } else {
                                return "+" + country_code;
                            }
                        } else {
                            return "+91";
                        }
                    } else {
                        return "+91";
                    }
                }

                public void setCountry_code(String country_code) {
                    this.country_code = country_code;
                }
            }

            public static class TagBean implements Serializable {
                /**
                 * id : 13
                 * tag_name : book hotel online
                 */

                private String id;
                private String tag_name;

                public String getId() {
                    return id;
                }

                public void setId(String id) {
                    this.id = id;
                }

                public String getTag_name() {
                    if (tag_name != null) {
                        return tag_name;
                    } else {
                        return "";
                    }
                }

                public void setTag_name(String tag_name) {
                    this.tag_name = tag_name;
                }
            }

            @Override
            public boolean equals(Object anotherObject) {
                if (!(anotherObject instanceof BusinessesBean)) {
                    return false;
                }
                BusinessesBean p = (BusinessesBean) anotherObject;
                return (this.id.equals(p.id));
            }
        }
    }
}

