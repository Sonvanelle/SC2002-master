package entities;

import java.io.Serializable;

/**
 * The Review class represents a review of a movie. It has four fields:
 * movie name, rating, review body, and reviewer
 */
public class Review implements Serializable {
    private String movieName;
    private int rating;
    private String review;
    private String reviewer;

    public Review(String movieName, int rating, String review, String reviewer) {
        this.movieName = movieName;
        this.rating = rating;
        this.review = review;
        this.reviewer = reviewer;
    }

    // Getters
    public String getMovieName() {
        return this.movieName;
    }

    public int getRating() {
        return this.rating;
    }

    public String getReview() {
        return this.review;
    }

    public String getReviewer() {
        return this.reviewer;
    }

    // Setters
    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public void setReviewer(String reviewer) {
        this.reviewer = reviewer;
    }
}