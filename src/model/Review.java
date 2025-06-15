package model;

public class Review {
    private int booking;
    private int star;
    private String title;
    private String content;

    // Constructor kosong (untuk deserialisasi JSON)
    public Review() {}

    // Constructor lengkap
    public Review(int booking, int star, String title, String content) {
        this.booking = booking;
        this.star = star;
        this.title = title;
        this.content = content;
    }

    // Getter dan Setter
    public int getBooking() {
        return booking;
    }

    public void setBooking(int booking) {
        this.booking = booking;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    // Optional: toString()
    @Override
    public String toString() {
        return "Review{" +
                "booking=" + booking +
                ", star=" + star +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}

