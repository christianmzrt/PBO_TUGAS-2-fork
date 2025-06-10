package model;

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

    public Roomtype(int id, int villa, String name, int quantity, int capacity, int price, String bedSize, boolean hasDesk, boolean hasAc, boolean hasTv, boolean hasWifi, boolean hasShower, boolean hasHotwater, boolean hasFridge) {
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
    }
}
