package model;

public class Review {
    private int booking;
    private int star;
    private String title;
    private String content;

    public Review(int booking, int star, String title, String content) {
        this.booking = booking;
        this.star = star;
        this.title = title;
        this.content = content;
    }
}
