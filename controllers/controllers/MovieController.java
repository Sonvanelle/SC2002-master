package controllers;

import entities.Movie;
import entities.MovieGoer;
import entities.Seat;
import entities.showingStatus;
import entities.Cinema;
import entities.Showing;
import entities.Review;

import java.io.Serializable;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;
import java.util.Collections;
import java.util.HashMap;

/**
 * Controller class that handles the creation, deletion, and editing of
 * Movies, as well as displaying the parameters.
 */
public class MovieController implements Serializable {
    private static ArrayList<Movie> movieList;
    private static final String filepath = "movielist.ser";
    private static MovieController controllerInstance = null;

    /*
     * Instantiate a controller object when called.
     */
    @SuppressWarnings("unchecked")
    public static MovieController getController() {
        if (controllerInstance == null) {
            controllerInstance = new MovieController();
        }
        movieList = (ArrayList<Movie>) loadData();
        if (movieList == null) {
            System.out.println("No movieList found; creating new file.");
            movieList = new ArrayList<Movie>();
            saveData();
        }
        return controllerInstance;
    }

    /**
     * This function creates a new movie object and adds it to the movieList
     * 
     * @param movieName String
     * @param movieMin  duration of the movie
     * @param val       showingStatus is an enum
     * @param synopsis  String
     * @param director  String
     * @param cast      ArrayList of String
     */
    public void createMovie(String movieName, long movieMin, showingStatus val,
            String synopsis, String director, ArrayList<String> cast) {
        Movie movie = new Movie(movieName, movieMin, val, synopsis, director, cast);
        movieList.add(movie);
    }

    /**
     * This function deletes a movie from the movie list
     * 
     * @param movieName The name of the movie to be deleted
     */
    public void deleteMovie(String movieName) {
        for (int i = 0; i < movieList.size(); i++) {
            if (movieList.get(i).getMovieName() != null && movieList.get(i).getMovieName() == movieName) {
                movieList.remove(movieList.get(i));
            }
        }
    }

    /**
     * It edits movie parameters via a User Interface
     * 
     * @param movieName String
     */
    public void editMovie(String movieName) {
        Scanner sc = new Scanner(System.in);
        Movie movieToEdit = null;
        int i;
        for (i = 0; i < movieList.size(); i++) {
            if (movieList.get(i).getMovieName().equals(movieName)) {
                movieToEdit = movieList.get(i);
                break;
            }
        }
        if (movieToEdit == null) {
            System.out.println("No such movie found.");
            return;
        }
        String movieToEditName = movieToEdit.getMovieName();
        System.out.println("What do you want to edit?" +
                "\n 1. Name" +
                "\n 2. Length" +
                "\n 3. Status" +
                "\n 4. Synopsis" +
                "\n 5. Cast" +
                "\n 6. Director");

        while (!sc.hasNextInt()) {
            System.out.println("Please input a valid number.");
            sc.next();
        }
        int choice = sc.nextInt();
        sc.nextLine();
        switch (choice) {
            case 1:
                System.out.println("Enter a new name for the movie: ");
                movieToEdit.setMovieName(sc.nextLine());
                break;

            case 2:
                System.out.println("Enter a new length for the movie: ");
                while (!sc.hasNextInt()) {
                    System.out.println("Please input a valid number.");
                    sc.next();
                }
                movieToEdit.setLength(sc.nextInt());
                sc.nextLine();
                break;

            case 3:
                System.out.println("Enter a new showing status: \n" +
                        "1. COMING SOON \n" +
                        "2. PREVIEW \n" +
                        "3. NOW SHOWING \n" +
                        "4. END OF SHOWING \n");
                int showstatus = sc.nextInt();
                sc.nextLine();
                switch (showstatus) {
                    case 1:
                        movieToEdit.setStatus(showingStatus.COMING_SOON);
                        break;
                    case 2:
                        movieToEdit.setStatus(showingStatus.PREVIEW);
                        break;
                    case 3:
                        movieToEdit.setStatus(showingStatus.NOW_SHOWING);
                        break;
                    case 4:
                        movieToEdit.setStatus(showingStatus.END_OF_SHOWING);
                        break;
                    default:
                        System.out.println("Invalid option; defaulting to COMING SOON.");
                        movieToEdit.setStatus(showingStatus.COMING_SOON);
                        break;
                }
                break;

            case 4:
                System.out.println("Enter a new synopsis: ");
                movieToEdit.setSynopsis(sc.nextLine());
                break;

            case 5:
                ArrayList<String> newCast = new ArrayList<String>();
                while (true) {
                    System.out.println("Enter movie cast (format: Actor - Character); enter STOP to stop: ");
                    String castName = sc.nextLine();
                    if (castName.equals("STOP"))
                        break;
                    newCast.add(castName);
                }
                movieToEdit.setCast(newCast);
                break;

            case 6:
                System.out.println("Enter a new director name: ");
                movieToEdit.setDirector(sc.nextLine());
                break;

            default:
                System.out.println("Invalid option.");
                break;
        }
        movieList.set(i, movieToEdit);
        ShowingController.updateMovieOfShowing(movieToEdit);
        System.out.println("Showings of this movie have been updated.");
        return;
    }

    /**
     * This function returns the movieList
     * 
     * @return The movieList arraylist is being returned.
     */
    public ArrayList<Movie> getMovieList() {
        return movieList;
    }

    /**
     * It returns an ArrayList of Movie objects that are not in the END_OF_SHOWING
     * or COMING_SOON
     * status
     * 
     * @return An ArrayList of Movie objects.
     */
    public ArrayList<Movie> getShowingMovieList() {

        ArrayList<Movie> selectedMovieList = new ArrayList<Movie>();

        for (int i = 0; i < movieList.size(); i++) {
            if (movieList.get(i).getStatus() != showingStatus.END_OF_SHOWING ||
                    movieList.get(i).getStatus() != showingStatus.COMING_SOON) {
                selectedMovieList.add(movieList.get(i));
            }
        }
        return selectedMovieList;
    }

    /**
     * Helper for printMovieListByStatus, prints out the movie list with the movie
     * name, status, and average rating
     * 
     * @param moviesToPrint ArrayList of Movie objects
     */
    public void printMovieList(ArrayList<Movie> moviesToPrint) {
        System.out.println("------------\n");
        for (int i = 0; i < moviesToPrint.size(); i++) {
            System.out.println((i + 1) + ". " + moviesToPrint.get(i).getMovieName());
            System.out.println("Status: " + moviesToPrint.get(i).getStatus());
            if (moviesToPrint.get(i).averageRating() == 0.0 || moviesToPrint.get(i).averageRating() == -0.0) {
                System.out.println("Avg Rating: N/A\n");
            } else {
                System.out.println("Avg Rating: " + moviesToPrint.get(i).averageRating() + "\n");
            }
        }
        System.out.println("------------\n");
    }

    /**
     * It prints a list of movies based on the user's input and then calls
     * movieSelector
     * on the chosen movie for further action (booking, review, etc)
     */
    public void printMovieListByStatus() {

        int statusOption;
        Scanner sc = new Scanner(System.in);

        do {
            System.out.println(
                    "\nViewing Movies... \n" +
                            "------------\n" +
                            "Please select movie showing status: \n" +
                            "1. Now Showing\n" +
                            "2. Preview\n" +
                            "3. Coming Soon\n" +
                            "4. List All Movies\n" +
                            "0. Back\n" +
                            "------------\n");

            System.out.println("Enter option: ");

            while (!sc.hasNextInt()) {
                System.out.println("Please input a number value.");
                sc.next();
            }

            statusOption = sc.nextInt();
            sc.nextLine();
            ArrayList<Movie> moviesToPrint = new ArrayList<Movie>();

            switch (statusOption) {
                case 1:
                    for (int i = 0; i < movieList.size(); i++) {
                        if (movieList.get(i).getStatus() == showingStatus.NOW_SHOWING) {
                            moviesToPrint.add(movieList.get(i));
                        }
                    }

                    printMovieList(moviesToPrint);
                    movieSelector(moviesToPrint);
                    System.out.println("Returning...");
                    break;

                case 2:
                    for (int i = 0; i < movieList.size(); i++) {
                        if (movieList.get(i).getStatus() == showingStatus.PREVIEW) {
                            moviesToPrint.add(movieList.get(i));
                        }
                    }

                    printMovieList(moviesToPrint);
                    movieSelector(moviesToPrint);
                    System.out.println("Returning...");
                    break;

                case 3:
                    for (int i = 0; i < movieList.size(); i++) {
                        if (movieList.get(i).getStatus() == showingStatus.COMING_SOON) {
                            moviesToPrint.add(movieList.get(i));
                        }
                    }

                    printMovieList(moviesToPrint);
                    movieSelector(moviesToPrint);
                    System.out.println("Returning...");
                    break;

                case 4:
                    for (int i = 0; i < movieList.size(); i++) {
                        if (movieList.get(i).getStatus() != showingStatus.END_OF_SHOWING) {
                            moviesToPrint.add(movieList.get(i));
                        }
                    }

                    printMovieList(moviesToPrint);
                    movieSelector(moviesToPrint);
                    System.out.println("Returning...");
                    break;

                case 0:
                    System.out.println("Navigating back to main menu.");
                    break;

                default:
                    System.out.println("Please input an option from 1 to 4.");
                    break;
            }
        } while (statusOption != 0);
    }

    /**
     * Controller method that allows the user to select a movie from a list of
     * movies
     * Calls a menu (movieActionList) for a user to view and leave reviews, make
     * bookings
     * 
     * @param movieSelectionList ArrayList of Movie objects
     */
    public void movieSelector(ArrayList<Movie> movieSelectionList) {
        int option = 0;
        Scanner sc = new Scanner(System.in);

        do {
            System.out.println("\nPlease select a movie (0 to exit menu): ");

            while (!sc.hasNextInt()) {
                System.out.printf("Ivalid input. Please enter option from 1 to %d.\n", movieSelectionList.size());
                sc.next();
            }

            // displayed list starts at index 1
            option = sc.nextInt() - 1;
            sc.nextLine();

            // user inputs 0 and exits selector
            if (option == -1) {
                System.out.println("Returning...");
                return;
            } else if (option < 0 || option >= movieSelectionList.size()) {
                System.out.printf("Invalid input. Please enter option from 1 to %d.\n", movieSelectionList.size());
            }

        } while (option < 0 || option >= movieSelectionList.size());

        // print movie details and display action menu
        movieSelectionList.get(option).printDetails();

        if (movieSelectionList.get(option).getStatus() == showingStatus.COMING_SOON) {
            // selection menu for movies that are COMING SOON, ie. movies unable to be
            // booked
            System.out.println("This movie is coming soon!");

        } else {
            // selection menu for movies that are NOW SHOWING and PREVIEW, ie. movies able
            // to be booked
            movieActionList(movieSelectionList.get(option), MovieGoerController.getController().getCurrentMovieGoer());
        }
    }

    /**
     * Controller method that allows a user to view reviews, create reviews, and
     * make
     * bookings for a selected movie.
     * 
     * @param selectedMovie Movie object
     * @param movieGoer     MovieGoer object
     */
    public void movieActionList(Movie selectedMovie, MovieGoer movieGoer) {
        int option;
        Scanner sc = new Scanner(System.in);

        do {
            System.out.println(
                    "\nWhat do you want to do? \n" +
                            "-------\n" +
                            "1. View Reviews\n" +
                            "2. Create Review\n" +
                            "3. Make Booking\n" +
                            "0. Back \n" +
                            "-------");

            System.out.println("Enter option: ");
            while (!sc.hasNextInt()) {
                System.out.println("Please input a number value.");
                sc.next();
            }

            option = sc.nextInt();
            sc.nextLine();

            switch (option) {
                case 1: // View reviews
                    ReviewController.getController().listReviews(selectedMovie.getMovieName());
                    break;

                case 2: // Create review
                    int rating;
                    String reviewBody;
                    System.out.printf("Leave a review for %s!\n", selectedMovie.getMovieName());
                    System.out.print("Enter your rating from 0-5: ");

                    // users can leave ratings in integers from 0-5
                    while (!sc.hasNextInt()) {
                        System.out.println("Please input an integer value. \n");
                        sc.next();
                    }
                    while (!(sc.nextInt() >= '0' && sc.nextInt() <= '5')) {
                        System.out.println("Please input an integ value between 0 to 5. \n");
                        sc.next();
                    }

                    rating = sc.nextInt();
                    sc.nextLine();

                    // enter review body
                    System.out.print("What did you think about the movie? \nInput ENTER when done: ");
                    reviewBody = sc.nextLine();

                    ReviewController.getController().createReview(
                            selectedMovie.getMovieName(), rating, reviewBody, movieGoer.getMovieGoerName());
                    break;

                case 3: // Make booking
                    CinemaController cinemaController = CinemaController.getController();
                    ShowingController showingController = ShowingController.getController();
                    BookingController bookingController = BookingController.getController();

                    String cineplex = null;
                    boolean isCineplex = false;
                    int choice = -1;

                    System.out.println("Which Cineplex do you want to view the movie from?");

                    // Prints Cineplex list for Users to choose from
                    System.out.println("-------");
                    for (String key : CinemaController.cineplexMap.keySet()) {
                        System.out.println(key);
                    }
                    System.out.println("-------\n");

                    // Checks if user input is a valid cineplex
                    while (!isCineplex) {
                        System.out.println("Choose a Cineplex: ");
                        cineplex = sc.nextLine().toUpperCase();

                        for (String key : CinemaController.cineplexMap.keySet()) {
                            isCineplex = (key.compareTo(cineplex) == 0) ? true : false;
                            if (isCineplex) {
                                break;
                            }
                        }
                        // isCineplex = CinemaController.cineplexMap.containsKey(cineplex);
                    }

                    ArrayList<Showing> initialShowingList = ShowingController.getController()
                            .listShowingsByCineplex(cineplex);

                    ArrayList<Showing> showingList = new ArrayList<Showing>();

                    for (Showing s : initialShowingList) {
                        if (s.getMovie().getMovieName().equals(selectedMovie.getMovieName())) {
                            showingList.add(s);
                        }
                    }

                    System.out.println("List of Showings at " + cineplex);
                    for (int i = 0; i < showingList.size(); i++) {
                        Movie currentMovie = showingList.get(i).getMovie();
                        System.out.printf("%d. %-30s | %td-%tm-%tY %tT - %tT\n", i + 1,
                                showingList.get(i).getMovie().getMovieName(), showingList.get(i).getShowtime(),
                                showingList.get(i).getShowtime(), showingList.get(i).getShowtime(),
                                showingList.get(i).getShowtime(),

                                showingList.get(i).getShowtime().plusMinutes(currentMovie.getMovieMin()));
                    }
                    System.out.println("-------");
                    if (showingList.size() == 0) {
                        System.out.println("No showings found.");
                        break;
                    }
                    while (choice > showingList.size() || choice < 0) {
                        System.out.println("Choose a showing: ");
                        choice = sc.nextInt();
                    }

                    Showing chosenShowing = showingList.get(choice - 1);

                    for (Seat s : chosenShowing.getShowingCinema().getSeatingPlan()) {
                        System.out.println("Row: " + s.getRow() + " Col: " + s.getCol());
                    }

                    // User chooses seat - seats will be set as occupied for the chosen showing
                    Seat chosenSeat = showingController.setSeatingForShowing(chosenShowing);

                    // creates and saves booking object with the chosen parameters
                    bookingController.createBooking(chosenShowing, movieGoer, chosenSeat,
                            chosenShowing.getShowingCinema().getCinemaID(), cineplex);

                    break;

                case 0:// wont print invalid option on first 0.
                    break;
                default:
                    System.out.println("Invalid option. Try again.");
                    break;
            }
        } while (option != 0);
    }

    /**
     * It takes in a list of movies, and prints out the top 5 movies in the list,
     * sorted by either
     * ticket sales or average rating
     */
    public void viewTop5() {
        int option;
        Scanner sc = new Scanner(System.in);

        do {
            System.out.println(
                    "View top 5 movies by:\n" +
                            "-------\n" +
                            "1. By ticket sales\n" +
                            "2. By overall rating\n" +
                            "0. Back\n" +
                            "-------\n");

            System.out.println("Enter option: ");

            while (!sc.hasNextInt()) {
                System.out.println("Please input a number value.");
                sc.next();
            }

            option = sc.nextInt();
            sc.nextLine();

            // after selection, fill the array list based on the selected parameters

            ArrayList<Movie> top5List = new ArrayList<Movie>();
            switch (option) {
                case 1:
                    // returns first 5 entries of preview and showing movies, sorted by sales
                    // numbers
                    System.out.print("Showing top 5 movies by sales:\n\n");
                    for (int i = 0; i < movieList.size(); i++) {
                        if (movieList.get(i).getStatus().isEqual("NOW_SHOWING") ||
                                movieList.get(i).getStatus().isEqual("PREVIEW")) {
                            top5List.add(movieList.get(i));
                        }
                    }
                    // sort list and print top 5 (or any number less than 5 but above 0)
                    top5List.sort(Comparator.comparingDouble(Movie::getTicketSales).reversed());
                    if (top5List.size() != 0) {
                        for (int i = 0; i < Math.min(top5List.size(), 5); i++) {
                            System.out.printf("%d. %s \n Sales: $%.2f\n", (i + 1), top5List.get(i).getMovieName(),
                                    top5List.get(i).getTicketSales());
                        }

                        movieSelector(top5List);

                    } else {
                        System.out.println("No movies match these terms.");
                        break;
                    }
                    break;

                case 2:
                    // returns first 5 entries of preview and showing movies, sorted by review
                    // ratings
                    System.out.print("Showing top 5 movies by average rating:\n\n");

                    for (int i = 0; i < movieList.size(); i++) {
                        if (movieList.get(i).getStatus().isEqual("NOW_SHOWING") ||
                                movieList.get(i).getStatus().isEqual("PREVIEW")) {
                            top5List.add(movieList.get(i));
                        }
                    }
                    // sort list and print top 5 (or any number less than 5 but above 0)
                    Collections.sort(top5List);
                    Collections.reverse(top5List);

                    if (top5List.size() != 0) {
                        for (int i = 0; i < Math.min(top5List.size(), 5); i++) {
                            System.out.printf("%d. %s \n Rating: %.2f\n", (i + 1), top5List.get(i).getMovieName(),
                                    top5List.get(i).averageRating());
                        }

                        movieSelector(top5List);

                    } else {
                        System.out.println("No movies match these terms.");
                        break;
                    }
                    break;
            }
            top5List = null;

        } while (option != 0);
    }

    /**
     * This function takes in a movie name and a rating, and adds the rating to the
     * movie's list of
     * ratings
     * 
     * @param movieName The name of the movie to add a rating to.
     * @param rating    float
     */
    public void addRating(String movieName, float rating) {
        for (int i = 0; i < movieList.size(); i++) {
            if (movieList.get(i).getMovieName().equals(movieName)) {
                movieList.get(i).addRating(rating);
                System.out.println("Review added to " + movieName + "!");
                saveData();
                return;
            }
        }
        System.out.println("No movie to add a rating to was found.");
    }

    /**
     * This function takes in a movie name and a price, and then adds the price to
     * the movie's total
     * sales
     * 
     * @param movieName The name of the movie to add a sale to.
     * @param price     double
     */
    public void addSale(String movieName, double price) {
        for (int i = 0; i < movieList.size(); i++) {
            if (movieList.get(i).getMovieName().equals(movieName)) {
                movieList.get(i).addSale(price);
                saveData();
                return;
            }
        }
        System.out.println("No movie to add a sale to was found.");
    }

    /**
     * It takes the movieList object and writes it to a file
     */
    public static void saveData() {
        try {
            FileOutputStream fileOut = new FileOutputStream(filepath);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);

            objectOut.writeObject(movieList);
            objectOut.close();
        } catch (IOException e) {
            System.out.println("Got an error while saving movie data: " + e);
            // e.printStackTrace();
        }
    }

    /**
     * It reads the movieList object at the filepath, and returns the object that
     * was saved there
     * 
     * @return The movieList object that was read from the file.
     */
    public static Object loadData() {
        try {
            FileInputStream fileIn = new FileInputStream(filepath);
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);

            Object obj = objectIn.readObject();
            objectIn.close();
            return obj;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Got an error while loading movie data: " + e);
            // e.printStackTrace();
            return null;
        }
    }
}