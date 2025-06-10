package model;

import java.time.LocalDateTime;

public class Booking {
    private int id;
    private int customer;
    private int roomType;
    private LocalDateTime checkinDate;
    private LocalDateTime checkoutDate;
    private int price;
    private Integer voucher;
    private int finalPrice;
    private String paymentStatus;
    private boolean hasCheckedin;
    private boolean hasCheckedout;

    public Booking(boolean hasCheckedout, boolean hasCheckedin, String paymentStatus, int finalPrice, Integer voucher, int price, LocalDateTime checkoutDate, LocalDateTime checkinDate, int roomType, int customer, int id) {
        this.hasCheckedout = hasCheckedout;
        this.hasCheckedin = hasCheckedin;
        this.paymentStatus = paymentStatus;
        this.finalPrice = finalPrice;
        this.voucher = voucher;
        this.price = price;
        this.checkoutDate = checkoutDate;
        this.checkinDate = checkinDate;
        this.roomType = roomType;
        this.customer = customer;
        this.id = id;
    }
}
