package model;

import HelperException.ValidationException;

public class Villa {
    private Integer id;
    private String name;
    private String description;
    private String address;

    public Villa(){}

    public Villa(Integer id, String name, String description, String address) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.address = address;

        if(id == null || id <= 0){
            throw new ValidationException("ID Villa tidak boleh 0 atau Null");
        }
        if(name == null || name.isEmpty() || name.isBlank()){
            throw new ValidationException("Nama Villa tidak boleh Kosong atau Null");
        }
        if(description == null || description.isEmpty() || description.isBlank()){
            throw new ValidationException("Deskripsi Villa tidak boleh Kosong atau Null");
        }
        if(address == null || address.isEmpty() || address.isBlank()){
            throw new ValidationException("Alamat Villa tidak boleh Kosong atau Null");
        }
    }

    public Villa(String name, String description, String address) {
        this.name = name;
        this.description = description;
        this.address = address;

        if(name == null || name.isEmpty() || name.isBlank()){
            throw new ValidationException("Nama Villa tidak boleh Kosong atau Null");
        }
        if(description == null || description.isEmpty() || description.isBlank()){
            throw new ValidationException("Deskripsi Villa tidak boleh Kosong atau Null");
        }
        if(address == null || address.isEmpty() || address.isBlank()){
            throw new ValidationException("Alamat Villa tidak boleh Kosong atau Null");
        }
    }

    public Integer getId() {
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

    public void setId(Integer id) {
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
