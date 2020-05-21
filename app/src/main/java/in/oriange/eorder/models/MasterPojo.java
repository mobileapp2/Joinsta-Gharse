package in.oriange.eorder.models;

import java.util.ArrayList;

public class MasterPojo {

    private ArrayList<MasterModel> result;

    private String type;

    private String message;

    public ArrayList<MasterModel> getResult() {
        return result;
    }

    public void setResult(ArrayList<MasterModel> result) {
        this.result = result;
    }

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


}
