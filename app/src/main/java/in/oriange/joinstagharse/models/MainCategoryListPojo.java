package in.oriange.joinstagharse.models;

import java.util.ArrayList;

public class MainCategoryListPojo {

    private ArrayList<MainCategoryListModel> result;

    private String type;

    private String message;

    public ArrayList<MainCategoryListModel> getResult() {
        return result;
    }

    public void setResult(ArrayList<MainCategoryListModel> result) {
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
