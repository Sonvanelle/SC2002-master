package controllers;

import entities.Showing;
import entities.showingStatus;
import entities.Seat.seatType;
import entities.Movie;
import entities.Seat;
import entities.Cinema;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Controller class that handles the creation, deletion, and modification of Showings for Movies.
 */
public class ShowingController implements Serializable {

    private static ArrayList<Showing> showingList;

    // holds an instance of the controller
    private static ShowingController controllerInstance = null;
    private static final String filepath = "showings.ser";

    // methods
    /*
     * Instantiate a controller object when called.
     * Deserializes showingList from file; if not found/null object, create new one
     * and store in new file.
     */
    @SuppressWarnings("unchecked")
    public static ShowingController getController() {
        if (controllerInstance == null) {
            controllerInstance = new ShowingController();
        }

        showingList = (ArrayList<Showing>) loadData();
        if (showingList == null) {
            System.out.println("No showingList found; creating new file.");
            showingList = new ArrayList<Showing>();
            saveData();
        }
        return controllerInstance;
    }

    /**
     * It returns an ArrayList of Showing objects that are in the same cineplex and
     * cinema as the
     * cinema object passed in
     * 
     * @param cinema Cinema object
     * @return An ArrayList of Showing objects.
     */
    public ArrayList<Showing> getShowingListByCinema(Cinema cinema) {
        ArrayList<Showing> temp = new ArrayList<Showing>();
        for (Showing s : showingList) {

            if (s.getShowingCinema().getCineplex().equals(cinema.getCineplex())
                    && s.getShowingCinema().getCinemaID() == (cinema.getCinemaID())) { // matches cinema
                temp.add(s); // add to temp list
            }
        }
        return temp;
    }

    /**
     * It prints the cinema seating plan of the specified showing
     * in a way that is easy to read
     * 
     * @param showing a Showing object
     */
    public void printCinema(Showing showing) {
        String screen = "----------SCREEN----------";
        // Print screen in the center of the 0th row
        for (int i = 0; i < (showing.getShowingCinema().getColumns() * 3 + 6) / 2 - screen.length() / 2; i++) {
            System.out.print(" ");
        }
        System.out.println(screen);

        char rowLetter = 'A';
        int col = 0; // for a cinema with 16 cols, print row letter when col is 0 or (numOfCols + 1)
        // the aisle spaces won't be a separate column in the code logic, but will be
        // printed at the midpoint

        int seatNum = 0;
        while (seatNum < showing.getShowingCinema().getSeatingPlan().size()) {
            if (col == 0) { // if start of row
                System.out.print(rowLetter + " ");
            } else if (col == showing.getShowingCinema().getColumns() + 1) { // if end of row
                System.out.print(" " + rowLetter);
            } else { // if col is a seat
                if (col == showing.getShowingCinema().getColumns() / 2 + 1) { // if col is first seat in right half of
                                                                              // the row, add spaces before it to
                                                                              // represent aisle
                    System.out.print("  ");
                }
                Seat currentSeat = showing.getShowingCinema().getSeatingPlan().get(seatNum);
                if (currentSeat.getOccupancy() == true) {
                    if (currentSeat.getSeatType() == seatType.COUPLE || currentSeat.getSeatType() == seatType.ELITE
                            || currentSeat.getSeatType() == seatType.ULTIMA) {
                        System.out.print("[x][x]");
                    } else {
                        System.out.print("[x]");
                    }
                } else {
                    if (currentSeat.getSeatType() == seatType.REGULAR) {
                        System.out.print("[ ]");
                    }
                    if (currentSeat.getSeatType() == seatType.EMPTY) {
                        System.out.print("   ");
                    }
                    if (currentSeat.getSeatType() == seatType.COUPLE) {
                        System.out.print("[c  c]");
                    }
                    if (currentSeat.getSeatType() == seatType.ELITE) {
                        System.out.print("[e  e]");
                    }
                    if (currentSeat.getSeatType() == seatType.ULTIMA) {
                        System.out.print("[u  u]");
                    }
                }
            }

            // Increment col number, rowLetter and seatNum
            if (col == 0) { // If start of row
                col++;
            } else if (col == showing.getShowingCinema().getColumns() + 1) { // If end of row reached
                col = 0;
                rowLetter += 1;
                System.out.println();
            } else if (showing.getShowingCinema().getSeatingPlan().get(seatNum).getSeatType() == seatType.REGULAR
                    || showing.getShowingCinema().getSeatingPlan().get(seatNum).getSeatType() == seatType.EMPTY) {
                col++;
                seatNum++;
            } else { // If seat type takes 2 spaces
                col += 2;
                seatNum++;
            }

        }
        System.out.print(" " + rowLetter + "\n\n");
    }

    /**
     * It takes in a showing object, prints the seating plan of the showing, prompts
     * the user to enter
     * a seat position, validates the seat position, sets the occupancy of the seat
     * to true and returns
     * the seat object
     * 
     * @param showing Showing object
     * @return The seat object that was just set to occupied.
     */
    public Seat setSeatingForShowing(Showing showing) {

        Scanner sc = new Scanner(System.in);

        // Print current showing's seating plan
        printCinema(showing);

        // Prompt user to enter seat position they want
        // (e.g. for a 2-seater spanning A1-A2, entering either A1 or A2 should be
        // sufficient)
        System.out.println("Enter seat position");
        String seatPos = sc.nextLine();

        // Validate that seat pos chosen is valid format, seatType isn't EMPTY and that
        // it isn't occupied
        // (can reuse CinemaController validation methods)
        while (!validateSeatSelection(showing, seatPos)) {
            System.out.println("Enter seat position");
            seatPos = sc.nextLine();
        }
        int seatPosRow = ((int) seatPos.charAt(0)) - 65;
        System.out.println("for debugging: current seatPos char: " + seatPos.charAt(0) + ", seatPosRow: " + seatPosRow);
        int seatPosCol = Integer.parseInt(seatPos.substring(1)) - 1;

        // Set occupancy of specific seat element in seating (ArrayList<Seat>) in the
        // Showing object to true
        int seatIndex = findSeatIndexByRowAndCol(showing, seatPosRow, seatPosCol);
        showing.getSeating().get(seatIndex).setOccupancy(true);

        saveData();
        return showing.getSeating().get(seatIndex);
    }

    /**
     * Validate that the seat selected is not EMPTY (invalid) and not occupied
     * Format of seatPos is Letter-Number
     * Row and col cannot exceed cinema's dimensions (seat exists in the cinema)
     * 
     * @param showing the showing object
     * @param seatPos "A1"
     * @return A boolean value.
     */
    public boolean validateSeatSelection(Showing showing, String seatPos) {

        Cinema cinema = showing.getShowingCinema();
        char seatPosRowChar = seatPos.charAt(0);

        // 1. Validate format of seatPos is <Letter><Number>
        if (!Character.isAlphabetic(seatPosRowChar)) { // Validate row char is a letter
            System.out.println("Invalid seat. First character of seat must be a letter.");
            return false;
        }
        int seatPosCol;
        try { // Validate col String is numeric
            seatPosCol = Integer.parseInt(seatPos.substring(1)) - 1;
        } catch (NumberFormatException e) {
            System.out.println("Invalid seat. Second character onwards of seat must be a number.");
            return false;
        }
        int seatPosRow = (int) seatPosRowChar - 65;

        // 2. Validate row and col don't exceed cinema's dimensions
        if (seatPosRow >= cinema.getRows() || seatPosRow < 0 || seatPosCol >= cinema.getColumns() || seatPosCol < 0) {
            System.out.println("Invalid seat.");
            return false;
        }

        // 3, 4. Validate seat selected is not EMPTY (invalid) and not occupied
        int seatIndex = findSeatIndexByRowAndCol(showing, seatPosRow, seatPosCol);
        Seat seat = cinema.getSeatingPlan().get(seatIndex);
        seatType currSeatType = seat.getSeatType();
        if (currSeatType == seatType.EMPTY) {
            System.out.println("Invalid seat.");
            return false;
        } else if (seat.getOccupancy()) {
            System.out.println("Seat already occupied.");
            return false;
        }

        return true;
    }

    /**
     * It finds the index of a seat in the seating plan of a cinema, given the row
     * and column of the seat.
     * Returns -1 if no seat exists at that specified row and col.
     * If there's a 2-seater from A1-A2, this method returns same index for both A1
     * and A2.
     * The row and col arguments would already have been validated when this method
     * runs, such that seat will always be found. -1 should never be returned.
     * 
     * @param showing The showing object that the seat is in.
     * @param row     The row of the seat to be found
     * @param col     The column of the seat to be found.
     * @return The index of the seat in the seating plan.
     */
    public int findSeatIndexByRowAndCol(Showing showing, int row, int col) {
        for (int i = 0; i < showing.getSeating().size(); i++) { // Find seat that matches in the seats ArrayList
            Seat currentSeat = showing.getSeating().get(i);
            if (currentSeat.getRow() == row && currentSeat.getCol() == col) { // If
                                                                              // single
                                                                              // seat,
                                                                              // or
                                                                              // left
                                                                              // half
                                                                              // of
                                                                              // 2-seater
                                                                              // found
                return i;
            }

            else if (currentSeat.getRow() == row && currentSeat.getCol() == col - 1) {
                // If seat-to-be-found has been "skipped" over, that means the seat-to-be-found
                // is the prev seat,
                // which is a 2-seater with the col argument being the right half of the seat.
                seatType prevSeatType = showing.getSeating().get(i).getSeatType();
                if (prevSeatType == seatType.COUPLE || prevSeatType == seatType.ELITE
                        || prevSeatType == seatType.ULTIMA) {
                    return i;
                }
            } else if (currentSeat.getRow() > row && currentSeat.getCol() == 0) {
                seatType prevSeatType = showing.getSeating().get(i - 1).getSeatType();
                if (prevSeatType == seatType.COUPLE || prevSeatType == seatType.ELITE
                        || prevSeatType == seatType.ULTIMA) {
                    return i - 1;
                }
            }
        }
        return -1;
    }

    /**
     * It checks that the showing to be added does not overlap with any other
     * showings in the cinema
     * 
     * @param cinema  Cinema object
     * @param showing the showing to be added
     * @return The method is returning a boolean value.
     */
    public boolean verifyNoShowingOverlaps(Cinema cinema, Showing showing) {
        // if the selected movie has no showings, return false
        if (getShowingListByCinema(cinema).size() == 0) {
            return true;
        }

        //
        for (int i = 0; i < getShowingListByCinema(cinema).size(); i++) {

            // find the showing that starts after showing to-be-added
            if (getShowingListByCinema(cinema).get(i).getShowtime().compareTo(showing.getShowtime()) > 0) {
                // new showing is the earliest in the showingList
                if (i == 0) {
                    Movie previousMovie = showing.getMovie();
                    LocalDateTime previousShowingEnd = showing.getShowtime().plusMinutes(previousMovie.getMovieMin());
                    // check that (current showing + its running time) doesn't overlap with next
                    // showing start time
                    if (previousShowingEnd.compareTo(getShowingListByCinema(cinema).get(0).getShowtime()) < 0) {
                        return true;
                    } else {
                        return false;
                    }
                }

                Movie previousMovie = getShowingListByCinema(cinema).get(i - 1).getMovie();
                LocalDateTime previousShowingEnd = getShowingListByCinema(cinema).get(i - 1).getShowtime()
                        .plusMinutes(previousMovie.getMovieMin());

                // check that (showing before + its movietime) doesn't overlap with (showing
                // TBA)
                if (previousShowingEnd.compareTo(showing.getShowtime()) < 0) {
                    Movie showingMovie = showing.getMovie();
                    LocalDateTime showingEnd = showing.getShowtime().plusMinutes(showingMovie.getMovieMin());

                    // check that (showing after) doesn't overlap with (showing to be added + its
                    // running time)
                    if (getShowingListByCinema(cinema).get(i).getShowtime().compareTo(showingEnd) >= 0) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        }

        // showing to be added is later than all current showings, so check the latest
        // showing in showingList for overlap.
        Movie previousMovie = getShowingListByCinema(cinema).get(getShowingListByCinema(cinema).size() - 1).getMovie();
        LocalDateTime previousShowingEnd = getShowingListByCinema(cinema).get(getShowingListByCinema(cinema).size() - 1)
                .getShowtime().plusMinutes(previousMovie.getMovieMin());
        if (previousShowingEnd.compareTo(showing.getShowtime()) < 0) {
            return true;
        }
        return false;
    }

    /**
     * It adds a new showing to the cinema's showing list and returns true if it is
     * successful.
     * 
     * @param cinema Cinema object
     * @return The method returns a boolean value
     */
    public boolean addShowing(Cinema cinema) {

        // Get movie and showtime from admin
        Scanner sc = new Scanner(System.in);

        // Print list of currently-showing movies
        ArrayList<Movie> movieList = new MovieController().getShowingMovieList();
        System.out.println("Currently showing movies:\n");

        for (int i = 0; i < movieList.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, movieList.get(i).getMovieName());
        }

        // Get movie choice from admin
        System.out.println("\nEnter movie index: ");
        while (!sc.hasNextInt()) {
            System.out.println("Please input a number value.");
            sc.next();
        }
        int movieChoice = sc.nextInt();
        sc.nextLine();

        while (movieChoice < 1 || movieChoice > movieList.size()) {
            System.out.printf("Error. Enter an index from %d to %d: \n", 1, movieList.size());
            movieChoice = sc.nextInt();
            sc.nextLine();
        }
        movieChoice -= 1;

        // Get showtime from admin
        System.out.printf(movieList.get(movieChoice).getMovieName() + " selected..\n");
        System.out.println("Enter new show time in the format DD-MM-YYYY hh:mm ");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        boolean validDateTime = false;
        LocalDateTime newShowTime = null;
        while (!validDateTime) { // Validate date and time input
            try {
                String newShowTimeString = sc.nextLine();
                newShowTime = LocalDateTime.parse(newShowTimeString, dateTimeFormatter);
                validDateTime = true;
            } catch (DateTimeParseException e) {
                System.out.println("Error. Please enter a valid date in the format DD-MM-YYYY hh:mm");
            }
        }

        // Create the newShowing and add it to showingList
        Showing newShowing = new Showing(cinema, newShowTime, movieList.get(movieChoice));
        boolean flag = addShowingHelper(cinema, newShowing);
        saveData();
        return flag;
    }

    // Returns true if show successfully added without clashes
    /**
     * It adds a new showing to the list of showings for a particular cinema, and
     * returns true if the
     * new showing is added successfully
     * 
     * @param cinema     the cinema that the showing is to be added to
     * @param newShowing the new showing to be added
     */
    public boolean addShowingHelper(Cinema cinema, Showing newShowing) { // helper method so can be used in
                                                                         // editShowing()
        ArrayList<Showing> showingListForCinema = getShowingListByCinema(cinema);
        if (verifyNoShowingOverlaps(cinema, newShowing)) { // validate if no showing overlaps
            for (int i = 0; i < showingListForCinema.size(); i++) { // Add at correct location in chronological order
                if (showingListForCinema.get(i).getShowtime().compareTo(newShowing.getShowtime()) > 0) { // !!! If got
                                                                                                         // showingsadded
                                                                                                         // across diff
                                                                                                         // cinemas/cineplexes
                                                                                                         // and got bug,
                                                                                                         // this line
                                                                                                         // prob cuplrit
                    showingList.add(i, newShowing);
                    return true;
                }
            }
            // If this branch reached, add new showing as last element in
            // showingList(ForCinema)?
            showingList.add(showingList.size(), newShowing);
            // cinema.setShowingList(showingList);
            return true;
        }
        System.out.println("Error. Clash of show time");
        return false;
    }

    /**
     * It allows the user to edit a showing by changing the movie or the show time
     * 
     * @param cinema Cinema
     */
    public void editShowing(Cinema cinema) {
        ArrayList<Showing> showingListForCinema = getShowingListByCinema(cinema); // TODO size 0 after exiting adminview
                                                                                  // and reentering (fixed)
        if (showingListForCinema.size() == 0) {
            System.out.println("This cinema has no showings currently.");
            return;
        }
        Scanner sc = new Scanner(System.in);

        // Print out all showings with indexes
        System.out.println("\n-------\nList of showings:");
        for (int i = 0; i < showingListForCinema.size(); i++) {
            Movie currentMovie = showingListForCinema.get(i).getMovie();
            if (currentMovie.getStatus() != showingStatus.END_OF_SHOWING) {

                System.out.printf("%d. %-30s | %td-%tm-%tY %tT - %tT\n", i + 1,
                        showingListForCinema.get(i).getMovie().getMovieName(), showingList.get(i).getShowtime(),
                        showingList.get(i).getShowtime(), showingList.get(i).getShowtime(),
                        showingListForCinema.get(i).getShowtime(),
                        showingListForCinema.get(i).getShowtime().plusMinutes(currentMovie.getMovieMin()));
            }

        }
        System.out.println("-------");

        // Let user select index of showing they want to change
        System.out.println("\nEnter index of showing you would like to edit: ");
        int index = sc.nextInt();
        while (index < 1 || index > showingListForCinema.size()) {
            System.out.printf("Error. Please enter a number from %d to %d: \n");
            index = sc.nextInt();
        }
        index -= 1;

        // Let user choose which aspect of the showing they would like to change (either
        // the movie, or show time)
        while (true) {
            System.out.println("1. Change movie");
            System.out.println("2. Change show time");
            System.out.println("0. Stop editing");

            int userChoice = sc.nextInt();

            if (userChoice == 1) { // Change movie

                // Print list of currently-showing movies
                System.out.println("List of currently showing movies:");
                ArrayList<Movie> movieList = new MovieController().getMovieList();
                for (int i = 0; i < movieList.size(); i++) {
                    System.out.printf("Movie %d: %s\n", i + 1, movieList.get(i).getMovieName());
                }
                // Get user's choice of new movie
                System.out.println("\nEnter movie index you would like to change to: ");
                int movieChoice = sc.nextInt();
                while (movieChoice < 1 || movieChoice > movieList.size()) {
                    System.out.printf("Error. Enter an index from %d to %d: \n", 1, movieList.size());
                    movieChoice = sc.nextInt();
                }
                movieChoice -= 1;
                // Change movie
                showingListForCinema.get(index).setMovie(movieList.get(movieChoice));

            } else if (userChoice == 2) { // Edit showing's show time
                System.out.println("\nEnter new show time in the format DD-MM-YYYY hh:mm");
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
                boolean validDateTime = false;
                LocalDateTime newShowTime = null;
                sc.nextLine();
                // Validate date and time input
                while (!validDateTime) {
                    try {
                        String newShowTimeString = sc.nextLine();
                        newShowTime = LocalDateTime.parse(newShowTimeString, dateTimeFormatter);
                        validDateTime = true;
                    } catch (DateTimeParseException e) {
                        System.out.println("Error. Please enter a valid date in the format DD-MM-YYYY hh:mm");
                    }
                }
                Showing newShowing = new Showing(cinema, newShowTime, showingListForCinema.get(index).getMovie());
                Showing tempOldShowing = showingListForCinema.get(index);

                if (addShowingHelper(cinema, newShowing)) {
                    showingList.remove(tempOldShowing);
                    System.out.println("Showing successfully edited");
                }
            } else if (userChoice == 0) {
                break;

            } else {
                System.out.println("Invalid. Please enter a number from 0 to 2: ");
                userChoice = sc.nextInt();
            }
        }
        saveData();
    }

    /**
     * It prints out a list of showings, then asks the user to select one of the
     * showings to delete
     * 
     * @param cinema Cinema object
     */
    public void deleteShowing(Cinema cinema) {
        ArrayList<Showing> showingListForCinema = getShowingListByCinema(cinema);
        Scanner sc = new Scanner(System.in);

        // Print out all showings with indexes
        System.out.println("\n-------\nList of showings:");
        for (int i = 0; i < showingListForCinema.size(); i++) {
            Movie currentMovie = showingListForCinema.get(i).getMovie();
            if (currentMovie.getStatus() != showingStatus.END_OF_SHOWING) {

                System.out.printf("%d. %-30s | %td-%tm-%tY %tT - %tT\n", i + 1,
                        showingListForCinema.get(i).getMovie().getMovieName(), showingList.get(i).getShowtime(),
                        showingList.get(i).getShowtime(), showingList.get(i).getShowtime(),
                        showingListForCinema.get(i).getShowtime(),
                        showingListForCinema.get(i).getShowtime().plusMinutes(currentMovie.getMovieMin()));
            }
        }
        System.out.println("-------");

        // Let user select index of showing they want to delete
        System.out.println("\nEnter index of showing you would like to delete: ");
        int index = sc.nextInt();
        while (index < 1 || index > showingListForCinema.size()) {
            System.out.printf("Error. Please enter a number from %d to %d: \n");
            index = sc.nextInt();
        }
        index -= 1;

        showingList.remove(showingListForCinema.get(index));
        saveData();
    }

    /**
     * Returns list of showings by movie
     * 
     * @param movie Movie
     * @return The method is returning an ArrayList of Showings.
     */
    public ArrayList<Showing> listShowings(Movie movie) {
        ArrayList<Showing> showingSelectionList = new ArrayList<Showing>();

        for (int i = 0; i < showingList.size(); i++) {
            if (showingList.get(i).getMovie().equals(movie)) {
                showingSelectionList.add(showingList.get(i));
            }
        }
        return showingSelectionList;
    }

    /**
     * Get all the showings in a cineplex, based on the cineplex string
     * 
     * @param cineplex String
     * @return An ArrayList of Showing objects.
     */
    public ArrayList<Showing> listShowingsByCineplex(String cineplex) {
        ArrayList<Showing> result = new ArrayList<Showing>();
        CinemaController cinemaController = CinemaController.getController();
        ArrayList<Cinema> cinemas = CinemaController.cineplexMap.get(cineplex); // e.g. cinemas: [JURONG 1, JURONG 2,
                                                                                // JURONG 3]
        if (cinemas == null) {
            System.out.println("No such cineplex found.");
            return null;
        }

        for (Showing s : showingList) {
            for (Cinema c : cinemas) {// if String cineplex.equals cineplexMap.get(s.getShowingCinema())
                if ((s.getShowingCinema().getCinemaID() == c.getCinemaID())
                        && (s.getShowingCinema().getCineplex().equals(c.getCineplex()))) {
                    result.add(s);
                }
            }
        }
        System.out.println("-----");
        return result;
    }

    /**
     * This function updates the movie of a showing if the movie name of the showing
     * is the same as the
     * movie name to be updated
     * 
     * @param updatedMovie The movie that has been updated
     */
    public static void updateMovieOfShowing(Movie updatedMovie) {
        for (Showing s : showingList) {
            if (s.getMovie().getMovieName().equals(updatedMovie.getMovieName())) {
                s.setMovie(updatedMovie);
            }
        }
    }

    /**
     * It prints out the movie name, showtime and cinema id of each showing in the
     * showingList
     */
    public void displayShowings() {
        CinemaController cc = CinemaController.getController();

        for (Showing s : showingList) {

            System.out.println(s.getMovie().getMovieName() + " " + s.getShowtime() + " Cinema id is " +
                    s.getShowingCinema().getCinemaID());
        }
    }

    /**
     * Saves the showingList object to a file
     */
    public static void saveData() {
        try {
            FileOutputStream fileOut = new FileOutputStream(filepath);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);

            objectOut.writeObject(showingList);
            objectOut.close();
        } catch (Exception e) {
            System.out.println("Got an error while saving showing data: " + e);
            // e.printStackTrace();
        }
    }

    /**
     * It loads the showingList data from the filepath and returns it
     * 
     * @return The showingList object that was saved.
     */
    public static Object loadData() {
        try {
            FileInputStream fileIn = new FileInputStream(filepath);
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);

            Object obj = objectIn.readObject();
            objectIn.close();
            return obj;
        } catch (Exception e) {
            System.out.println("Got an error while loading showing data: " + e);
            // e.printStackTrace();
            return null;
        }
    }
}