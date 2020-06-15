package in.oriange.joinstagharse.models;

import java.util.List;

public class ChatModel {


    /**
     * type : success
     * message : Chat returned successfully!
     * result : [{"id":"1","order_id":"1","chat_text":"text sample","chat_doc_type":"1","chat_image":"test.png","send_by":"10","created_at":"2020","is_seen":"0","is_read":"0","is_deleted":"0"}]
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
         * order_id : 1
         * chat_text : text sample
         * chat_doc_type : 1
         * chat_image : test.png
         * send_by : 10
         * created_at : 2020
         * is_seen : 0
         * is_read : 0
         * is_deleted : 0
         */

        private String id;
        private String order_id;
        private String chat_text;
        private String chat_doc_type;
        private String chat_image;
        private String send_by;
        private String created_at;
        private String is_seen;
        private String is_read;
        private String is_deleted;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getOrder_id() {
            return order_id;
        }

        public void setOrder_id(String order_id) {
            this.order_id = order_id;
        }

        public String getChat_text() {
            return chat_text;
        }

        public void setChat_text(String chat_text) {
            this.chat_text = chat_text;
        }

        public String getChat_doc_type() {
            return chat_doc_type;
        }

        public void setChat_doc_type(String chat_doc_type) {
            this.chat_doc_type = chat_doc_type;
        }

        public String getChat_image() {
            return chat_image;
        }

        public void setChat_image(String chat_image) {
            this.chat_image = chat_image;
        }

        public String getSend_by() {
            return send_by;
        }

        public void setSend_by(String send_by) {
            this.send_by = send_by;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getIs_seen() {
            return is_seen;
        }

        public void setIs_seen(String is_seen) {
            this.is_seen = is_seen;
        }

        public String getIs_read() {
            return is_read;
        }

        public void setIs_read(String is_read) {
            this.is_read = is_read;
        }

        public String getIs_deleted() {
            return is_deleted;
        }

        public void setIs_deleted(String is_deleted) {
            this.is_deleted = is_deleted;
        }
    }
}
