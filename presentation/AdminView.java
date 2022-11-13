package presentation;

import entities.Cinema;
import entities.Movie;
import controllers.CinemaController;
import controllers.MovieController;
import controllers.ShowingController;
import controllers.SettingsController;
import entities.showingStatus;
import java.util.Scanner;
import java.util.ArrayList;

/**
 * This class is the view for the admin front end of the application.
 */
public class AdminView {
    public void printMenu() {
        Scanner input = new Scanner(System.in);
        MovieController moviecontroller = MovieController.getController();
        ShowingController showingcontroller = ShowingController.getController();
        SettingsController settingscontroller = SettingsController.getController();
        CinemaController cinemacontroller = CinemaController.getController();

        int cont = 1;
        while (cont != 0) {
            System.out.println("\nAdmin View \n" +
                    "------------\n" +
                    "1. Configure system settings \n" +
                    "2. Create a Movie Listing \n" +
                    "3. Update a Movie Listing \n" +
                    "4. View top 5 movies by Sales/Ratings \n" +
                    "5. View showings \n" +
                    "6. Add showing \n" +
                    "7. Edit showing \n" +
                    "8. Delete showing \n" +
                    "9. Return to main menu \n" +
                    "------------");

            while (!input.hasNextInt()) {
                System.out.println("Please input a number.");
                input.next();
            }
            int option = input.nextInt();
            input.nextLine();

            switch (option) {
                case 1:
                    System.out.println("\nAdmin System Settings\n" +
                            "1. Add a holiday \n" +
                            "2. Delete a holiday \n" +
                            "3. Configure ticket prices \n" +
                            "4. Add cinema \n" +
                            "5. Define cinema layout \n" +
                            "6. Show all configs/settings \n" +
                            "7. Return to admin menu \n");

                    while (!input.hasNextInt()) {
                        System.out.println("Please input a number.");
                        input.next();
                    }
                    int configOption = input.nextInt();
                    input.nextLine();

                    if (configOption == 1)
                        settingscontroller.addHoliday();
                    else if (configOption == 2)
                        settingscontroller.deleteHoliday();
                    else if (configOption == 3)
                        settingscontroller.setNewPrice();
                    else if (configOption == 4) {

                        // Prints Cineplex hashmap
                        ArrayList<Cinema> cinemaList = new ArrayList<Cinema>();
                        for (String key : CinemaController.cineplexMap.keySet()) {
                            cinemaList = CinemaController.cineplexMap.get(key);
                            for (Cinema c : cinemaList) {
                                System.out.println(key + ": Cinema " + c.getCinemaID());
                                System.out.println(c.getClassType() + ", " + c.getRows() + "x" + c.getColumns() + '\n');
                            }
                        }

                        System.out.println("Enter cineplex name: ");
                        String cineplex = input.nextLine().toUpperCase();

                        // Prompt for cinema id
                        System.out.println("Enter cinema ID: ");
                        while (!input.hasNextInt()) {
                            System.out.println("Please input a number.");
                            input.next();
                        }
                        int cinemaId = input.nextInt();
                        input.nextLine();

                        // Prompt for cinema type
                        System.out.println("1. NORMAL");
                        System.out.println("2. GOLDEN");
                        System.out.println("3. PLATINUM");
                        System.out.println("Enter cinema type");
                        while (!input.hasNextInt()) {
                            System.out.println("Please input a number.");
                            input.next();
                        }
                        int cinemaTypeInt = input.nextInt();
                        input.nextLine();
                        Cinema.classType cinemaType = null;
                        if (cinemaTypeInt == 1) {
                            cinemaType = Cinema.classType.NORMAL;
                        } else if (cinemaTypeInt == 2) {
                            cinemaType = Cinema.classType.GOLDEN;
                        } else if (cinemaTypeInt == 3) {
                            cinemaType = Cinema.classType.PLATINUM;
                        } else {
                            System.out.println("Invalid input. Defaulted to normal cinema type.");
                            cinemaType = Cinema.classType.NORMAL;
                        }

                        // Prompt for number of rows and cols
                        System.out.println("Enter number of rows");
                        while (!input.hasNextInt()) {
                            System.out.println("Please input a number.");
                            input.next();
                        }
                        int rows = input.nextInt();
                        input.nextLine();

                        System.out.println("Enter number of columns");
                        while (!input.hasNextInt()) {
                            System.out.println("Please input a number.");
                            input.next();
                        }
                        int cols = input.nextInt();
                        input.nextLine();

                        cinemacontroller.createCinema(cineplex, cinemaId, cinemaType, rows, cols);
                        CinemaController.saveData();
                    } else if (configOption == 5) {
                        cinemacontroller.defineLayout(getCinemaChoiceFromUser());
                        CinemaController.saveData();
                    } else if (configOption == 6) {
                        SettingsController.getController().printSettings();
                    } else if (configOption == 7) {
                        System.out.println("Returning...");
                        break;
                    } else
                        System.out.println("Invalid option. Returning...");
                    break;

                case 2: // create movie listing
                    String movieName;
                    long movieMin;
                    showingStatus val;
                    String synopsis;
                    String director;
                    ArrayList<String> cast = new ArrayList<String>();

                    System.out.println("Enter movie name: ");
                    movieName = input.nextLine();
                    System.out.println("Enter length of the movie in minutes: ");
                    movieMin = input.nextLong();
                    System.out.println("Enter showing status: \n" +
                            "1. COMING SOON \n" +
                            "2. PREVIEW \n" +
                            "3. NOW SHOWING \n" +
                            "4. END OF SHOWING \n");
                    int showstatus = input.nextInt();
                    input.nextLine();
                    switch (showstatus) {
                        case 1:
                            val = showingStatus.COMING_SOON;
                            break;
                        case 2:
                            val = showingStatus.PREVIEW;
                            break;
                        case 3:
                            val = showingStatus.NOW_SHOWING;
                            break;
                        case 4:
                            val = showingStatus.END_OF_SHOWING;
                            break;
                        default:
                            System.out.println("Invalid option; defaulting to COMING SOON.");
                            val = showingStatus.COMING_SOON;
                            break;
                    }
                    System.out.println("Enter movie synopsis: ");
                    synopsis = input.nextLine();
                    System.out.println("Enter movie director: ");
                    director = input.nextLine();
                    while (true) {
                        System.out.println("Enter movie cast (format: Actor - Character); enter STOP to stop: ");
                        String castName = input.nextLine();
                        if (castName.equals("STOP")) {
                            break;
                        }
                        cast.add(castName);
                    }
                    moviecontroller.createMovie(movieName, movieMin, val, synopsis, director, cast);
                    System.out.println("New movie: " + movieName + " created; maybe create some showings for it next!");
                    MovieController.saveData();
                    moviecontroller.printMovieList(moviecontroller.getMovieList());
                    break;

                case 3: // update a movie listing.
                    System.out.println("Printing all current movie names...");
                    ArrayList<Movie> movieList = moviecontroller.getMovieList();
                    for (int i = 0; i < movieList.size(); i++) {
                        System.out.println("Movie Name: " + movieList.get(i).getMovieName());
                    }
                    System.out.println("Enter the movie name to edit: ");
                    String name = input.nextLine();
                    moviecontroller.editMovie(name);
                    break;

                case 4:
                    moviecontroller.viewTop5();
                    break;

                case 5: // TODO View showings
                    showingcontroller.displayShowings();

                    break;
                case 6: // Add showing to a cinema
                    Cinema cinemaToBeAdded = getCinemaChoiceFromUser();
                    if (showingcontroller.addShowing(cinemaToBeAdded)) {
                        System.out.println("Showing successfully added.");
                    }
                    ShowingController.saveData();
                    break;
                case 7: // Edit showing to a cinema
                    Cinema cinemaToBeEdited = getCinemaChoiceFromUser();
                    showingcontroller.editShowing(cinemaToBeEdited);
                    ShowingController.saveData();
                    break;
                case 8: // Delete showing to a cinema
                    Cinema cinemaToBeDeleted = getCinemaChoiceFromUser();
                    showingcontroller.deleteShowing(cinemaToBeDeleted);
                    ShowingController.saveData();
                    break;

                case 9: // return to main menu.
                    System.out.println("Exiting to main menu...");

                    // Save all controller's data to file when exiting this module.
                    MovieController.saveData();
                    ShowingController.saveData();
                    SettingsController.saveAllData();
                    CinemaController.saveData();
                    cont = 0;
                    break;

                default:
                    System.out.println("Invalid option. Try again.");
                    break;
            }
        }
    }

    /**
     * Prints out the cineplexes and cinemas in each cineplex, then asks the user to
     * choose a
     * cineplex, then asks the user to choose a cinema in that cineplex
     * 
     * @return Cinema object
     */
    private Cinema getCinemaChoiceFromUser() {
        Scanner sc = new Scanner(System.in);
        CinemaController cinemacontroller = new CinemaController();

        // Prints Cineplex hashmap
        ArrayList<Cinema> cinemaList = new ArrayList<Cinema>();
        for (String key : CinemaController.cineplexMap.keySet()) {
            cinemaList = CinemaController.cineplexMap.get(key);
            for (Cinema c : cinemaList) {
                System.out.println(key + ": Cinema " + c.getCinemaID());
                System.out.println(c.getClassType() + ", " + c.getRows() + "x" + c.getColumns() + '\n');
            }
        }

        System.out.println("Which Cineplex ?");// changed because it doesnt make sense in delete showing
        boolean isCineplex = false;
        String cineplex = null;
        // Checks if user input is a valid cineplex
        while (!isCineplex) {
            System.out.println("Choose a Cineplex: ");
            cineplex = sc.nextLine().toUpperCase();

            for (String key : CinemaController.cineplexMap.keySet()) {
                isCineplex = cineplex.compareTo(key) == 0 ? true : false;
                if (isCineplex) {
                    break;
                }
            }
        }

        System.out.println("Enter id of cinema that you want to edit: ");

        int cinemaId = -1;
        while (true) {
            while (!sc.hasNextInt()) {
                System.out.println("Please input a number.");
                sc.next();
            }
            cinemaId = sc.nextInt();
            sc.nextLine();

            if (!cinemacontroller.checkCinemaIdExistsInCineplex(cinemaId, cineplex)) {
                System.out.println("No such cinema exists.");
            } else {
                return cinemacontroller.getCinemaByIdAndCineplex(cinemaId, cineplex);
            }
        }
    }

}
