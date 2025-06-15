package model;

import java.time.LocalDateTime;

public class Voucher {
    private int id;
    private String code;
    private String description;
    private double discount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    // Constructor kosong (wajib untuk deserialisasi JSON)
    public Voucher() {}

    // Constructor lengkap
    public Voucher(int id, String code, String description, double discount, LocalDateTime startDate, LocalDateTime endDate) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.discount = discount;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getter dan Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    // Optional: for logging/debug
    @Override
    public String toString() {
        return "Voucher{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", description='" + description + '\'' +
                ", discount=" + discount +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
