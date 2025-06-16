package model;

public class Villa {
    private int id;
    private String name;
    private String description;
    private String address;

    public Villa(){}

    public Villa(int id, String name, String description, String address) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.address = address;
    }

    public Villa(String name, String description, String address) {
        this.name = name;
        this.description = description;
        this.address = address;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getAddress() {
        return address;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isValid() {
        return name != null && !name.trim().isEmpty() &&
                description != null && !description.trim().isEmpty() &&
                address != null && !address.trim().isEmpty();
    }
}
