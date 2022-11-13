package presentation;

import java.util.Scanner;

import entities.MovieGoer;
import controllers.BookingController;
import controllers.MovieController;
import controllers.MovieGoerController;

/**
 * This class is the view for the movie-goer application. It prints the main
 * menu and the menu after
 * the user has logged in
 * Depending on the user's choice, it calls
 * methods from the appropriate controller class.
 */
public class MovieGoerView {

    public void printMenu() {
        int option;
        Boolean isLogin = false;
        Scanner sc = new Scanner(System.in);

        // init and create controller instances
        BookingController bookingController = BookingController.getController();
        MovieController movieController = MovieController.getController();
        MovieGoerController movieGoerController = MovieGoerController.getController();

        /*
         * MAIN MENU of the movie-goer application
         */

        // prompt user for login
        do {
            System.out.println(
                    "\nMovie-Goer Menu\n" +
                            "------------\n" +
                            "1. Log In\n" +
                            "2. Create New Account\n" +
                            "0. Back \n" +
                            "------------");

            movieGoerController.printMovieGoers();

            System.out.print("Enter option: ");

            // reject non-integer input
            while (!sc.hasNextInt()) {
                System.out.println("Please input a number value.");
                sc.next();
            }

            option = sc.nextInt();
            sc.nextLine();
            switch (option) {
                case 1:
                    // prompt for customer number or email, then log in and get the associated
                    // movieGoer object
                    MovieGoer movieGoerCheck = null;

                    while (movieGoerCheck == null) {
                        System.out.print("Input your email address: ");
                        String emailInput = sc.nextLine();

                        // check for valid login - if the MovieGoer exists in the movieGoerList
                        movieGoerCheck = movieGoerController.searchMovieGoerEmail(emailInput);
                        if (movieGoerCheck != null) {
                            // set current account to the one used to log in - account is held by the
                            // controller
                            movieGoerController.setCurrentMovieGoer(movieGoerCheck);
                            isLogin = true;
                            continue;
                        } else {
                            System.out.println("Invalid email!");
                        }
                    }
                    break;

                case 2:
                    // create movieGoer object
                    MovieGoer newAccount = movieGoerController.createMovieGoerHelper();

                    System.out.println("New Account Details:\n");
                    System.out.printf("Name:" + newAccount.getMovieGoerName() + "\n" +
                            "Number:" + newAccount.getMovieGoerNumber() + "\n" +
                            "Email:" + newAccount.getEmailAddress() + "\n");

                    // add the movieGoer object to the list and use this account for the current
                    // session
                    movieGoerController.createMovieGoer(newAccount);
                    movieGoerController.setCurrentMovieGoer(newAccount);

                    movieGoerController.printMovieGoers();
                    break;

                case 0:
                    System.out.println("Navigating back to the movie-goer menu.");

                    // reset the currently-loaded movieGoer account
                    movieGoerController.resetCurrentMovieGoer();

                    // save all the serializables in the controllers
                    MovieGoerController.saveData();
                    MovieController.saveData();
                    BookingController.saveData();
                    break;

                default:
                    System.out.println("Invalid option.");
                    break;
            }

        } while (option != 0 && !isLogin);

        /*
         * this menu is printed AFTER user has successfully logged in or created an
         * account
         */
        do {
            System.out.println(
                    "\nWelcome, " + movieGoerController.getCurrentMovieGoer().getMovieGoerName() + "!\n" +
                            "------------\n" +
                            "1. View Booking History \n" +
                            "2. View Top 5 Movies \n" +
                            "3. View Movies\n" +
                            "0. Back \n" +
                            "------------");

            System.out.println("Enter option: ");

            // reject non-integer input
            while (!sc.hasNextInt()) {
                System.out.println("Please input a number value.");
                sc.next();
            }

            option = sc.nextInt();
            sc.nextLine();
            switch (option) {
                case 1:
                    System.out.println(
                            "\nViewing bookings... \n" +
                                    "------------\n");

                    // pass the current movieGoer object to the BookingController
                    bookingController.listBookingViaAccount(
                            movieGoerController.getCurrentMovieGoer());
                    break;

                case 2:
                    movieController.viewTop5();
                    break;

                // use the view movies method as an entry to book movies
                case 3:
                    movieController.printMovieListByStatus();
                    break;

                case 0:
                    System.out.println("Exiting...");
                    isLogin = false;
                    movieGoerController.resetCurrentMovieGoer();
                    break;

                default:
                    System.out.println("Invalid option.");
                    break;

            }

        } while (option != 0 || movieGoerController.getCurrentMovieGoer() != null);
    }
}
