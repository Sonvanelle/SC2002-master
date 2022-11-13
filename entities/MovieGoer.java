package entities;

import java.io.Serializable;

/**
 * The MovieGoer class contains the attributes of a moviegoer,
 * representing something like an account.
 */
public class MovieGoer implements Serializable {
    private String movieGoerName;
    private String emailAddress;
    private String movieGoerNumber;
    private int movieGoerAge;

    public MovieGoer(String name, String email, String number, int age) {
        this.movieGoerName = name;
        this.emailAddress = email;
        this.movieGoerNumber = number;
        this.movieGoerAge = age;
    }

    // methods

    // getters
    public String getMovieGoerName() {
        return this.movieGoerName;
    }

    public String getEmailAddress() {
        return this.emailAddress;
    }

    public String getMovieGoerNumber() {
        return this.movieGoerNumber;
    }

    public int getMovieGoerAge() {
        return this.movieGoerAge;
    }

    // setters
    public void setMovierGoerName(String name) {
        this.movieGoerName = name;
    }

    public void setEmailAddress(String email) {
        this.emailAddress = email;
    }

    public void setmovieGoerNumber(String number) {
        this.movieGoerNumber = number;
    }

    public void setMovieGoerAge(int age) {
        this.movieGoerAge = age;
    }
}
