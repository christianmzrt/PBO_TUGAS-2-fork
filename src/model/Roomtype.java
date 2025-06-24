package model;

import HelperException.ValidationException;

public class Roomtype {
    private int id;
    private int villa;
    private String name;
    private int quantity;
    private int capacity;
    private int price;
    private String bedSize;
    private boolean hasDesk;
    private boolean hasAc;
    private boolean hasTv;
    private boolean hasWifi;
    private boolean hasShower;
    private boolean hasHotwater;
    private boolean hasFridge;

    public Roomtype() {}

    public Roomtype(int id, int villa, String name, int quantity, int capacity, int price, String bedSize,
                    boolean hasDesk, boolean hasAc, boolean hasTv, boolean hasWifi,
                    boolean hasShower, boolean hasHotwater, boolean hasFridge) {
        this.id = id;
        this.villa = villa;
        this.name = name;
        this.quantity = quantity;
        this.capacity = capacity;
        this.price = price;
        this.bedSize = bedSize;
        this.hasDesk = hasDesk;
        this.hasAc = hasAc;
        this.hasTv = hasTv;
        this.hasWifi = hasWifi;
        this.hasShower = hasShower;
        this.hasHotwater = hasHotwater;
        this.hasFridge = hasFridge;

        if(id <= 0){
            throw new ValidationException("ID Room tidak boleh Kosong atau Null");
        }
        if(villa <= 0){
            throw new ValidationException("ID Villa tidak boleh Kosong atau Null");
        }
        if(name == null || name.isEmpty() || name.isBlank()){
            throw new ValidationException("Nama Room tidak boleh Kosong atau Null");
        }
        if(quantity <= 0){
            throw new ValidationException("Quantity tidak boleh kurang dari 1");
        }
        if(capacity <= 0){
            throw new ValidationException("Capacity tidak boleh kurang dari 1");
        }
        if(price <= 0){
            throw new ValidationException("Price tidak boleh kurang dari 1");
        }
        if(bedSize == null || bedSize.isEmpty() || bedSize.isBlank()){
            throw new ValidationException("Bed Size Room tidak boleh Kosong atau Null");
        }
        if(hasDesk != true || hasDesk != false){
            throw new ValidationException("Has Desk tidak boleh Kosong atau Null");
        }
        if(hasAc != true || hasAc != false){
            throw new ValidationException("Has AC tidak boleh Kosong atau Null");
        }
        if(hasTv != true || hasTv != false){
            throw new ValidationException("Has TV tidak boleh Kosong atau Null");
        }
        if(hasWifi != true || hasWifi != false){
            throw new ValidationException("Has Wifi tidak boleh Kosong atau Null");
        }
        if(hasShower != true || hasShower != false){
            throw new ValidationException("Has Shower tidak boleh Kosong atau Null");
        }
        if(hasHotwater != true || hasHotwater != false){
            throw new ValidationException("Has Hot Water tidak boleh Kosong atau Null");
        }
        if(hasFridge != true || hasFridge != false){
            throw new ValidationException("Has Fridge tidak boleh Kosong atau Null");
        }
    }

    // Getters dan Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVilla() {
        return villa;
    }

    public void setVilla(int villa) {
        this.villa = villa;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getBedSize() {
        return bedSize;
    }

    public void setBedSize(String bedSize) {
        this.bedSize = bedSize;
    }

    public boolean isHasDesk() {
        return hasDesk;
    }

    public void setHasDesk(boolean hasDesk) {
        this.hasDesk = hasDesk;
    }

    public boolean isHasAc() {
        return hasAc;
    }

    public void setHasAc(boolean hasAc) {
        this.hasAc = hasAc;
    }

    public boolean isHasTv() {
        return hasTv;
    }

    public void setHasTv(boolean hasTv) {
        this.hasTv = hasTv;
    }

    public boolean isHasWifi() {
        return hasWifi;
    }

    public void setHasWifi(boolean hasWifi) {
        this.hasWifi = hasWifi;
    }

    public boolean isHasShower() {
        return hasShower;
    }

    public void setHasShower(boolean hasShower) {
        this.hasShower = hasShower;
    }

    public boolean isHasHotwater() {
        return hasHotwater;
    }

    public void setHasHotwater(boolean hasHotwater) {
        this.hasHotwater = hasHotwater;
    }

    public boolean isHasFridge() {
        return hasFridge;
    }

    public void setHasFridge(boolean hasFridge) {
        this.hasFridge = hasFridge;
    }

    @Override
    public String toString() {
        return "Roomtype{" +
                "id=" + id +
                ", villa=" + villa +
                ", name='" + name + '\'' +
                ", quantity=" + quantity +
                ", capacity=" + capacity +
                ", price=" + price +
                ", bedSize='" + bedSize + '\'' +
                ", hasDesk=" + hasDesk +
                ", hasAc=" + hasAc +
                ", hasTv=" + hasTv +
                ", hasWifi=" + hasWifi +
                ", hasShower=" + hasShower +
                ", hasHotwater=" + hasHotwater +
                ", hasFridge=" + hasFridge +
                '}';
    }

}

