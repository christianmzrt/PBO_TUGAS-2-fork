package model;

import java.time.LocalDateTime;

public class Voucher {
    private int id;
    private String code;
    private String description;
    private double discount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public Voucher(int id, String code, String description, double discount, LocalDateTime startDate, LocalDateTime endDate) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.discount = discount;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
