package controllers;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Scanner;

import entities.MovieGoer;

/**
 * Controller class that handles the creation, deletion, and modification of moviegoer accounts
 */
public class MovieGoerController implements Serializable {

    private static final String filepath = "movieGoerList.ser"; // testing purposes

    private static ArrayList<MovieGoer> movieGoerList;

    private MovieGoer currentMovieGoer = null;

    // holds an instance of the controller
    private static MovieGoerController controllerInstance = null;

    // methods
    /*
     * Instantiate a controller object when called.
     * Also deserializes movieGoerList from file; if none found/null object, create
     * a new one.
     */
    @SuppressWarnings("unchecked")
    public static MovieGoerController getController() {
        if (controllerInstance == null) {
            controllerInstance = new MovieGoerController();
        }
        movieGoerList = (ArrayList<MovieGoer>) loadData();
        if (movieGoerList == null) {
            System.out.println("No movieGoerList found; creating new file.");
            movieGoerList = new ArrayList<MovieGoer>();
            saveData();
        }
        return controllerInstance;
    }

    /**
     * This function returns the current moviegoer that is logged in
     * 
     * @return The currentMovieGoer object.
     */
    public MovieGoer getCurrentMovieGoer() {
        if (currentMovieGoer == null)
            System.out.println("No account has been logged in.");
        return this.currentMovieGoer;
    }

    /**
     * This function sets the current moviegoer to the moviegoer passed in as a
     * parameter
     * 
     * @param movieGoer The moviegoer to be set as the current moviegoer.
     */
    public void setCurrentMovieGoer(MovieGoer movieGoer) {
        this.currentMovieGoer = movieGoer;
    }

    /**
     * This function resets the current moviegoer to null (logout)
     */
    public void resetCurrentMovieGoer() {
        this.currentMovieGoer = null;
    }

    /**
     * It creates a new MovieGoer object and returns it - can then
     * be passed to createMovieGoer
     * 
     * @return A MovieGoer object
     */
    public MovieGoer createMovieGoerHelper() {
        String name = "", email = "placeholder", number = "placeholder";
        int age = -1;
        Scanner sc = new Scanner(System.in);

        System.out.println("Creating new account. \nWe will require a valid name, email, mobile number and age:");

        do {
            System.out.print("Enter your name: ");
            name = sc.nextLine();
            System.out.print("Enter your email address: ");
            email = sc.nextLine();
            System.out.print("Enter your mobile number: ");
            number = sc.nextLine();
            System.out.print("Enter your age: ");
            age = sc.nextInt();
            sc.nextLine();
        } while (!isEmailValid(email) || !isMobileNumberValid(number) || !isMovieGoerValid(email, number));

        MovieGoer movieGoer = new MovieGoer(name, email, number, age);
        return movieGoer;
    }

    /**
     * This function checks if the moviegoer's email and number are valid
     * and does not already exist in movieGoerList
     * 
     * @param email  String
     * @param number String
     * @return Boolean
     */
    public Boolean isMovieGoerValid(String email, String number) {
        for (MovieGoer m : movieGoerList) {
            if (m.getMovieGoerNumber().equals(number)) {
                System.out.println("This number has been used to create another account. Please try again.");
                return false;
            }
            if (m.getEmailAddress().equals(email)) {
                System.out.println("This email has been used to create another account. Please try again.");
                return false;
            }
        }
        return true;
    }

    /**
     * This function adds a moviegoer to the moviegoer list and saves the data
     * 
     * @param movieGoer The moviegoer object to be added to the list
     */
    public void createMovieGoer(MovieGoer movieGoer) {
        movieGoerList.add(movieGoer);
        saveData();
    }

    /**
     * If the email address is longer than 254 characters, or if it doesn't match
     * the OWASP regex, then
     * it's invalid
     * 
     * @param email the email address to be validated
     * @return A Boolean value.
     */
    public Boolean isEmailValid(String email) {
        // max char length of an email is 254
        if (email.length() > 254) {
            System.out.println("Sorry, your email address is too long.");
            return false;
        }

        // pattern for email addr in general, using OWASP regex
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        if (!email.matches(emailRegex)) {
            System.out.println("Sorry, your email address seems to be invalid.");
            return false;
        }
        return true;
    }

    /**
     * It checks if the length of the number is 8, and if all the characters are
     * digits
     * 
     * @param number The mobile number to be validated.
     * @return A boolean value.
     */
    public Boolean isMobileNumberValid(String number) {
        char[] mobileNoChars = number.toCharArray();

        // check for valid length and phone no
        if (number.length() != 8) {
            System.out.println("Sorry, but your mobile number has to be 8 digits long!");
            return false;
        }

        for (char c : mobileNoChars) {
            if (!Character.isDigit(c)) {
                System.out.println("Sorry, your mobile number must consist only of numeric digits!");
                return false;
            }
        }
        return true;
    }

    /**
     * This function searches for a moviegoer in the moviegoer list by email address
     * 
     * @param email String
     * @return The moviegoer object is being returned.
     */
    public MovieGoer searchMovieGoerEmail(String email) {
        System.out.printf("Searching for account..\n");

        for (MovieGoer m : movieGoerList) {
            if (m.getEmailAddress().equals(email)) {
                return m;
            }
        }
        return null;
    }

    /**
     * This function searches for a moviegoer by their mobile number
     * 
     * @param number the mobile number of the moviegoer
     * @return The moviegoer object is being returned.
     */
    public MovieGoer searchMovieGoerNumber(String number) {
        System.out.println("Searching for mobile no..\n");

        for (MovieGoer m : movieGoerList) {
            if (m.getMovieGoerNumber().equals(number)) {
                return m;
            }
        }
        return null;
    }

    /**
     * It prints out all the moviegoers in the moviegoer list (used for debug)
     */
    public void printMovieGoers() {
        System.out.print("Printing all accounts..\n");
        for (int i = 0; i < movieGoerList.size(); i++) {
            System.out.printf((i + 1) + ". " + movieGoerList.get(i).getMovieGoerName() + "\n" +
                    movieGoerList.get(i).getEmailAddress() + "\n" +
                    movieGoerList.get(i).getMovieGoerNumber() + "\n" +
                    movieGoerList.get(i).getMovieGoerAge() + "\n");
        }
    }

    /**
     * It writes the movieGoerList object to a file
     */
    public static void saveData() {
        try {
            FileOutputStream fileOut = new FileOutputStream(filepath);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);

            objectOut.writeObject(movieGoerList);
            objectOut.close();
        } catch (IOException e) {
            System.out.println("Got an error while saving moviegoer account data: " + e);
            // e.printStackTrace();
        }
    }

    /**
     * It reads the movieGoerList data from the file and returns it
     * 
     * @return The movieGoerList being returned is the object that is being read
     *         from the file.
     */
    private static Object loadData() {
        try {
            FileInputStream fileIn = new FileInputStream(filepath);
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);

            Object obj = objectIn.readObject();
            objectIn.close();
            return obj;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Got an error while loading moviegoer account data: " + e);
            // e.printStackTrace();
            return null;
        }
    }
}
