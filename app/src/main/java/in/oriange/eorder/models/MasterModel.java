package in.oriange.eorder.models;

public class MasterModel {

    private String name;

    private String id;

    private boolean isChecked;

    public MasterModel() {
    }

    public MasterModel(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public MasterModel(String name, String id, boolean isChecked) {
        this(name, id);
        this.isChecked = isChecked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
