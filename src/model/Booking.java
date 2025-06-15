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

    // Constructor kosong (penting untuk JSON deserialization)
    public Booking() {
    }

    // Constructor lengkap
    public Booking(int id, int customer, int roomType, LocalDateTime checkinDate, LocalDateTime checkoutDate,
                   int price, Integer voucher, int finalPrice, String paymentStatus,
                   boolean hasCheckedin, boolean hasCheckedout) {
        this.id = id;
        this.customer = customer;
        this.roomType = roomType;
        this.checkinDate = checkinDate;
        this.checkoutDate = checkoutDate;
        this.price = price;
        this.voucher = voucher;
        this.finalPrice = finalPrice;
        this.paymentStatus = paymentStatus;
        this.hasCheckedin = hasCheckedin;
        this.hasCheckedout = hasCheckedout;
    }

    // Getter & Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomer() {
        return customer;
    }

    public void setCustomer(int customer) {
        this.customer = customer;
    }

    public int getRoomType() {
        return roomType;
    }

    public void setRoomType(int roomType) {
        this.roomType = roomType;
    }

    public LocalDateTime getCheckinDate() {
        return checkinDate;
    }

    public void setCheckinDate(LocalDateTime checkinDate) {
        this.checkinDate = checkinDate;
    }

    public LocalDateTime getCheckoutDate() {
        return checkoutDate;
    }

    public void setCheckoutDate(LocalDateTime checkoutDate) {
        this.checkoutDate = checkoutDate;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Integer getVoucher() {
        return voucher;
    }

    public void setVoucher(Integer voucher) {
        this.voucher = voucher;
    }

    public int getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(int finalPrice) {
        this.finalPrice = finalPrice;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public boolean isHasCheckedin() {
        return hasCheckedin;
    }

    public void setHasCheckedin(boolean hasCheckedin) {
        this.hasCheckedin = hasCheckedin;
    }

    public boolean isHasCheckedout() {
        return hasCheckedout;
    }

    public void setHasCheckedout(boolean hasCheckedout) {
        this.hasCheckedout = hasCheckedout;
    }

    // Optional: toString()
    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", customer=" + customer +
                ", roomType=" + roomType +
                ", checkinDate=" + checkinDate +
                ", checkoutDate=" + checkoutDate +
                ", price=" + price +
                ", voucher=" + voucher +
                ", finalPrice=" + finalPrice +
                ", paymentStatus='" + paymentStatus + '\'' +
                ", hasCheckedin=" + hasCheckedin +
                ", hasCheckedout=" + hasCheckedout +
                '}';
    }
}
