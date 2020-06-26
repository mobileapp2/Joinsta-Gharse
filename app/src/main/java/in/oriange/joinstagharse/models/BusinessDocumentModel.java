package in.oriange.joinstagharse.models;

public class BusinessDocumentModel {

    private String id;
    private String type;
    private String name;
    private String isVerified;

    public BusinessDocumentModel() {
    }

    public BusinessDocumentModel(String id, String type, String name) {
        this.id = id;
        this.type = type;
        this.name = name;
    }

    public BusinessDocumentModel(String id, String type, String name, String isVerified) {
        this(id, type, name);
        this.isVerified = isVerified;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(String isVerified) {
        this.isVerified = isVerified;
    }
}
