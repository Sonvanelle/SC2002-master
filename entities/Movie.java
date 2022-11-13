package entities;

import java.util.ArrayList;
import java.lang.Comparable;
import java.io.Serializable;

/**
 * Movie is a class that stores information about a movie, including its name,
 * length, status,
 * synopsis, director, cast, total rating, review count, and ticket sales
 */
public class Movie implements Comparable<Movie>, Serializable {

    private String movieName;
    private long movieMin;
    private showingStatus status; // COMING_SOON, PREVIEW, NOW_SHOWING, END_OF_SHOWING
    private String synopsis;
    private String director;
    private ArrayList<String> cast;
    private float totalRating;
    private int reviewCount;
    private double ticketSales = 0;

    @Override
    public int compareTo(Movie movie) {
        if (this.averageRating() < movie.averageRating()) {
            return -1;
        }
        return 1;
    }

    // Constructor
    public Movie(
            String movieName, long movieMin,
            showingStatus status, String synopsis, String director, ArrayList<String> cast) {
        this.movieName = movieName;
        this.movieMin = movieMin;
        this.status = status;
        this.synopsis = synopsis;
        this.director = director;
        this.cast = cast;
        this.totalRating = 0;
        this.reviewCount = 0;
        this.ticketSales = 0;
    }

    // methods

    /**
     * Calculates the average rating of the movie
     * 
     * @return The average rating of the movie.
     */
    public float averageRating() {
        if (reviewCount == 0 || reviewCount == 1) {
            return 0;
        }
        return totalRating / reviewCount;
    }

    /**
     * Prints out the details of the movie in a pretty manner.
     */
    public void printDetails() {
        System.out.println("------------\n");
        System.out.printf("Title: %s\n", getMovieName());
        System.out.printf("Showing Status: " + status.toString() + "\n");
        System.out.printf("Runtime: %d minutes\n", getMovieMin());
        if (reviewCount != 0) {
            System.out.printf("Average score: %.2f\n", averageRating());
        }
        System.out.printf("Director: %s\n", getDirector());
        System.out.println("Cast:");
        for (int i = 0; i < getCast().size(); i++) {
            System.out.printf(getCast().get(i) + "\n");
        }
        System.out.printf("Synopsis:\n%s\n", getSynopsis());
        System.out.println("------------\n");

    }

    // getters
    public String getMovieName() {
        return movieName;
    }

    public long getMovieMin() {
        return movieMin;
    }

    public showingStatus getStatus() {
        return status;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public String getDirector() {
        return director;
    }

    public ArrayList<String> getCast() {
        return cast;
    }

    public Double getTicketSales() {
        return ticketSales;
    }

    // setters
    public void setMovieName(String name) {
        this.movieName = name;
    }

    public void setLength(int length) {
        this.movieMin = length;
    }

    public void setStatus(showingStatus status) {
        this.status = status;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public void setCast(ArrayList<String> cast) {
        this.cast = cast;
    }

    public void addRating(float newRating) {
        this.totalRating += newRating;
        reviewCount++;
    }

    public void addSale(double price) {
        ticketSales += price;
    }
}