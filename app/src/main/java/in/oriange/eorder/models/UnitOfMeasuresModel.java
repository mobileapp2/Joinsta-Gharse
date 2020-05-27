package in.oriange.eorder.models;

import java.util.List;

public class UnitOfMeasuresModel {

    /**
     * type : success
     * message : Unit of measures retuned successfully!
     * result : [{"id":"1","unit_of_measure":"BAG","description":"BAG","created_by":"1","updated_by":"1","created_at":"2017-09-24 00:00:00","updated_at":"2017-09-24 00:00:00"},{"id":"2","unit_of_measure":"BGS","description":"BAGS","created_by":"1","updated_by":"1","created_at":"2017-09-24 00:00:00","updated_at":"2017-09-24 00:00:00"},{"id":"3","unit_of_measure":"BKL","description":"BUCKLES","created_by":"1","updated_by":"1","created_at":"2017-09-24 00:00:00","updated_at":"2017-09-24 00:00:00"},{"id":"4","unit_of_measure":"BOU","description":"BOU","created_by":"1","updated_by":"1","created_at":"2017-09-24 00:00:00","updated_at":"2017-09-24 00:00:00"},{"id":"5","unit_of_measure":"BOX","description":"BOX","created_by":"1","updated_by":"1","created_at":"2017-09-24 00:00:00","updated_at":"2017-09-24 00:00:00"},{"id":"6","unit_of_measure":"BTL","description":"BOTTLES","created_by":"1","updated_by":"1","created_at":"2017-09-24 00:00:00","updated_at":"2017-09-24 00:00:00"},{"id":"7","unit_of_measure":"BUN","description":"BUNCHES","created_by":"1","updated_by":"1","created_at":"2017-09-24 00:00:00","updated_at":"2017-09-24 00:00:00"},{"id":"8","unit_of_measure":"CBM","description":"CUBIC METER","created_by":"1","updated_by":"1","created_at":"2017-09-24 00:00:00","updated_at":"2017-09-24 00:00:00"},{"id":"9","unit_of_measure":"CCM","description":"CUBIC CENTIMETER","created_by":"1","updated_by":"1","created_at":"2017-09-24 00:00:00","updated_at":"2017-09-24 00:00:00"},{"id":"10","unit_of_measure":"CIN","description":"CUBIC INCHES","created_by":"1","updated_by":"1","created_at":"2017-09-24 00:00:00","updated_at":"2017-09-24 00:00:00"},{"id":"11","unit_of_measure":"CMS","description":"CENTIMETER","created_by":"1","updated_by":"1","created_at":"2017-09-24 00:00:00","updated_at":"2017-09-24 00:00:00"},{"id":"12","unit_of_measure":"CQM","description":"CUBIC METERS","created_by":"1","updated_by":"1","created_at":"2017-09-24 00:00:00","updated_at":"2017-09-24 00:00:00"},{"id":"13","unit_of_measure":"CTN","description":"CARTON","created_by":"1","updated_by":"1","created_at":"2017-09-24 00:00:00","updated_at":"2017-09-24 00:00:00"},{"id":"14","unit_of_measure":"DOZ","description":"DOZEN","created_by":"1","updated_by":"1","created_at":"2017-09-24 00:00:00","updated_at":"2017-09-24 00:00:00"},{"id":"15","unit_of_measure":"DRM","description":"DRUM","created_by":"1","updated_by":"1","created_at":"2017-09-24 00:00:00","updated_at":"2017-09-24 00:00:00"},{"id":"16","unit_of_measure":"FTS","description":"FEET","created_by":"1","updated_by":"1","created_at":"2017-09-24 00:00:00","updated_at":"2017-09-24 00:00:00"},{"id":"17","unit_of_measure":"GGR","description":"GREAT GROSS","created_by":"1","updated_by":"1","created_at":"2017-09-24 00:00:00","updated_at":"2017-09-24 00:00:00"},{"id":"18","unit_of_measure":"GMS","description":"GRAMS","created_by":"1","updated_by":"1","created_at":"2017-09-24 00:00:00","updated_at":"2017-09-24 00:00:00"},{"id":"19","unit_of_measure":"GRS","description":"GROSS","created_by":"1","updated_by":"1","created_at":"2017-09-24 00:00:00","updated_at":"2017-09-24 00:00:00"},{"id":"20","unit_of_measure":"GYD","description":"GROSS YARDS","created_by":"1","updated_by":"1","created_at":"2017-09-24 00:00:00","updated_at":"2017-09-24 00:00:00"},{"id":"21","unit_of_measure":"HKS","description":"HANKS","created_by":"1","updated_by":"1","created_at":"2017-09-24 00:00:00","updated_at":"2017-09-24 00:00:00"},{"id":"22","unit_of_measure":"INC","description":"INCHES","created_by":"1","updated_by":"1","created_at":"2017-09-24 00:00:00","updated_at":"2017-09-24 00:00:00"},{"id":"23","unit_of_measure":"KGS","description":"Kilograms","created_by":"1","updated_by":"1","created_at":"2017-09-24 00:00:00","updated_at":"2017-09-24 00:00:00"},{"id":"24","unit_of_measure":"KLR","description":"KILOLITER","created_by":"1","updated_by":"1","created_at":"2017-09-24 00:00:00","updated_at":"2017-09-24 00:00:00"},{"id":"25","unit_of_measure":"KME","description":"KILOMETERS","created_by":"1","updated_by":"1","created_at":"2017-09-24 00:00:00","updated_at":"2017-09-24 00:00:00"},{"id":"26","unit_of_measure":"LBS","description":"POUNDS","created_by":"1","updated_by":"1","created_at":"2017-09-24 00:00:00","updated_at":"2017-09-24 00:00:00"},{"id":"27","unit_of_measure":"LOT","description":"LOTS","created_by":"1","updated_by":"1","created_at":"2017-09-24 00:00:00","updated_at":"2017-09-24 00:00:00"},{"id":"28","unit_of_measure":"LTR","description":"LITERS","created_by":"1","updated_by":"1","created_at":"2017-09-24 00:00:00","updated_at":"2017-09-24 00:00:00"},{"id":"29","unit_of_measure":"MGS","description":"MILLI GRAMS","created_by":"1","updated_by":"1","created_at":"2017-09-24 00:00:00","updated_at":"2017-09-24 00:00:00"},{"id":"30","unit_of_measure":"MTR","description":"METER","created_by":"1","updated_by":"1","created_at":"2017-09-24 00:00:00","updated_at":"2017-09-24 00:00:00"},{"id":"31","unit_of_measure":"MTS","description":"METRIC TON","created_by":"1","updated_by":"1","created_at":"2017-09-24 00:00:00","updated_at":"2017-09-24 00:00:00"},{"id":"32","unit_of_measure":"NOS","description":"Numbers","created_by":"1","updated_by":"1","created_at":"2017-09-24 00:00:00","updated_at":"2017-09-24 00:00:00"},{"id":"33","unit_of_measure":"ODD","description":"ODDS","created_by":"1","updated_by":"1","created_at":"2017-09-24 00:00:00","updated_at":"2017-09-24 00:00:00"},{"id":"34","unit_of_measure":"PAC","description":"PACKS","created_by":"1","updated_by":"1","created_at":"2017-09-24 00:00:00","updated_at":"2017-09-24 00:00:00"},{"id":"35","unit_of_measure":"PCS","description":"Pieces","created_by":"1","updated_by":"1","created_at":"2017-09-24 00:00:00","updated_at":"2017-09-24 00:00:00"},{"id":"36","unit_of_measure":"PRS","description":"PAIRS","created_by":"1","updated_by":"1","created_at":"2017-09-24 00:00:00","updated_at":"2017-09-24 00:00:00"},{"id":"37","unit_of_measure":"QTL","description":"QUINTAL","created_by":"1","updated_by":"1","created_at":"2017-09-24 00:00:00","updated_at":"2017-09-24 00:00:00"},{"id":"38","unit_of_measure":"ROL","description":"ROLLS","created_by":"1","updated_by":"1","created_at":"2017-09-24 00:00:00","updated_at":"2017-09-24 00:00:00"},{"id":"39","unit_of_measure":"SDM","description":"DECAMETER SQUARE","created_by":"1","updated_by":"1","created_at":"2017-09-24 00:00:00","updated_at":"2017-09-24 00:00:00"},{"id":"40","unit_of_measure":"SET","description":"SETS","created_by":"1","updated_by":"1","created_at":"2017-09-24 00:00:00","updated_at":"2017-09-24 00:00:00"},{"id":"41","unit_of_measure":"SHT","description":"SHEETS","created_by":"1","updated_by":"1","created_at":"2017-09-24 00:00:00","updated_at":"2017-09-24 00:00:00"},{"id":"42","unit_of_measure":"SQF","description":"SQUARE FEET","created_by":"1","updated_by":"1","created_at":"2017-09-24 00:00:00","updated_at":"2017-09-24 00:00:00"},{"id":"43","unit_of_measure":"SQI","description":"SQUARE INCHES","created_by":"1","updated_by":"1","created_at":"2017-09-24 00:00:00","updated_at":"2017-09-24 00:00:00"},{"id":"44","unit_of_measure":"SQM","description":"SQUARE METER","created_by":"1","updated_by":"1","created_at":"2017-09-24 00:00:00","updated_at":"2017-09-24 00:00:00"},{"id":"45","unit_of_measure":"SQY","description":"SQUARE YARDS","created_by":"1","updated_by":"1","created_at":"2017-09-24 00:00:00","updated_at":"2017-09-24 00:00:00"},{"id":"46","unit_of_measure":"TBS","description":"TABLETS","created_by":"1","updated_by":"1","created_at":"2017-09-24 00:00:00","updated_at":"2017-09-24 00:00:00"},{"id":"47","unit_of_measure":"THD","description":"THOUSANDS","created_by":"1","updated_by":"1","created_at":"2017-09-24 00:00:00","updated_at":"2017-09-24 00:00:00"},{"id":"48","unit_of_measure":"TOL","description":"TOLA","created_by":"1","updated_by":"1","created_at":"2017-09-24 00:00:00","updated_at":"2017-09-24 00:00:00"},{"id":"49","unit_of_measure":"TON","description":"GREAT BRITAIN TON","created_by":"1","updated_by":"1","created_at":"2017-09-24 00:00:00","updated_at":"2017-09-24 00:00:00"},{"id":"50","unit_of_measure":"TUB","description":"TUBES","created_by":"1","updated_by":"1","created_at":"2017-09-24 00:00:00","updated_at":"2017-09-24 00:00:00"},{"id":"51","unit_of_measure":"UGS","description":"US GALLONS","created_by":"1","updated_by":"1","created_at":"2017-09-24 00:00:00","updated_at":"2017-09-24 00:00:00"},{"id":"52","unit_of_measure":"UNT","description":"UNITS","created_by":"1","updated_by":"1","created_at":"2017-09-24 00:00:00","updated_at":"2017-09-24 00:00:00"},{"id":"53","unit_of_measure":"VLS","description":"Vials","created_by":"1","updated_by":"1","created_at":"2017-09-24 00:00:00","updated_at":"2017-09-24 00:00:00"},{"id":"54","unit_of_measure":"YDS","description":"YARDS","created_by":"1","updated_by":"1","created_at":"2017-09-24 00:00:00","updated_at":"2020-05-04 10:45:15"},{"id":"55","unit_of_measure":"TEST","description":"","created_by":"5","updated_by":"0","created_at":"2017-09-24 11:54:57","updated_at":"2017-09-24 11:54:57"},{"id":"59","unit_of_measure":"Box Box","description":"","created_by":"25","updated_by":"0","created_at":"2017-10-15 10:39:54","updated_at":"2017-10-15 10:39:54"},{"id":"60","unit_of_measure":"TOL TOLA","description":"","created_by":"25","updated_by":"0","created_at":"2017-10-15 10:42:32","updated_at":"2017-10-15 10:42:32"},{"id":"69","unit_of_measure":"Mile","description":"1,760 yards","created_by":"1","updated_by":"1","created_at":"2020-05-04 10:54:12","updated_at":"2020-05-04 10:54:12"}]
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
         * id : 1
         * unit_of_measure : BAG
         * description : BAG
         * created_by : 1
         * updated_by : 1
         * created_at : 2017-09-24 00:00:00
         * updated_at : 2017-09-24 00:00:00
         */

        private String id;
        private String unit_of_measure;
        private String description;
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

        public String getUnit_of_measure() {
            return unit_of_measure;
        }

        public void setUnit_of_measure(String unit_of_measure) {
            this.unit_of_measure = unit_of_measure;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
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

        public String getUnitOfMeasure() {
            if (!getUnit_of_measure().trim().isEmpty() && !getDescription().trim().isEmpty()) {
                return getUnit_of_measure() + " (" + getDescription() + ")";
            } else if (!getUnit_of_measure().trim().isEmpty() && getDescription().trim().isEmpty()) {
                return getUnit_of_measure();
            } else {
                return "";
            }

        }

    }
}
