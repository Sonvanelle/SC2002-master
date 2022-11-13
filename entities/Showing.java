package entities;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * The Showing class contains a showing movie, a showing time, a cinema, and a
 * seating plan.
 * Bookings are made for Showings of Movies.
 */
public class Showing implements Serializable {
    private Movie showingMovie;
    private LocalDateTime showingTime;
    private ArrayList<Seat> seating;
    private Cinema showingCinema;

    // Constructor
    public Showing(Cinema showingCinema, LocalDateTime showTime, Movie movie) {
        this.showingMovie = movie;
        this.showingTime = showTime;
        this.showingCinema = showingCinema;
        this.seating = showingCinema.getSeatingPlan();
    }

    // Accessors
    public Movie getMovie() {
        return this.showingMovie;
    }

    public Cinema getShowingCinema() {
        return this.showingCinema;
    }

    public LocalDateTime getShowtime() {
        return showingTime;
    }

    public ArrayList<Seat> getSeating() {
        return this.seating;
    }

    // Mutators
    public void setMovie(Movie movie) {
        this.showingMovie = movie;
    }

    public void setShowingTime(LocalDateTime showTime) {
        this.showingTime = showTime;
    }
}