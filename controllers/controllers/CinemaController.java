package controllers;

import entities.Cinema;
import entities.Showing;
import entities.Seat.seatType;
import entities.Movie;
import entities.Seat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Controller class that manages the creation of cinemas and their
 * layouts, also saving and loading of the cineplexes
 */
public class CinemaController implements Serializable {

    // mapping of cineplexes and their cinemas
    public static HashMap<String, ArrayList<Cinema>> cineplexMap = new HashMap<String, ArrayList<Cinema>>();
    private static final String filepath = "cineplexes.ser";

    // holds an instance of the controller
    private static CinemaController controllerInstance = null;

    // methods
    /*
     * Instantiate a controller object when called.
     */
    @SuppressWarnings("unchecked")
    public static CinemaController getController() {
        if (controllerInstance == null) {
            controllerInstance = new CinemaController();
        }
        cineplexMap = (HashMap<String, ArrayList<Cinema>>) loadData();
        if (cineplexMap == null) {
            System.out.println("No cineplexMap found; creating new file.");
            cineplexMap = new HashMap<String, ArrayList<Cinema>>();
            saveData();
        }
        return controllerInstance;
    }

    /**
     * It creates a new cinema object and adds it to the cineplexMap
     * 
     * @param cineplex The name of the cineplex
     * @param id       Cinema ID
     * @param val      Cinema.classType
     * @param rows     number of rows in the cinema
     * @param cols     number of columns in the cinema
     */
    public void createCinema(String cineplex, int id, Cinema.classType val, int rows, int cols) {
        Cinema cinema = new Cinema(cineplex, id, val, rows, cols);
        ArrayList<Cinema> existing = cineplexMap.containsKey(cineplex) ? cineplexMap.get(cineplex)
                : new ArrayList<Cinema>();
        existing.add(cinema);
        cineplexMap.put(cineplex, existing);
        saveData();
    }

    /**
     * For each key in the cineplexMap, print the key, then print the cinemaID of
     * each cinema in the
     * cinemaList
     */
    public void listCinema() {
        for (String key : cineplexMap.keySet()) {
            System.out.println(key);
            ArrayList<Cinema> cinemaList = cineplexMap.get(key);
            for (int j = 0; j < cinemaList.size(); j++) {
                System.out.println(cinemaList.get(j).getCinemaID());
            }
        }
    }

    /**
     * It takes in a cinema ID and a cineplex name, and returns the corresponding
     * cinema object
     * 
     * @param id       the cinema id
     * @param cineplex String
     * @return The cinema object with the given id and cineplex.
     */
    public Cinema getCinemaByIdAndCineplex(int id, String cineplex) {
        ArrayList<Cinema> cinemas = cineplexMap.get(cineplex);
        for (Cinema c : cinemas) {
            if (c.getCinemaID() == id)
                return c;

        }
        return null;

        // for (HashMap.Entry<String, ArrayList<Cinema>> mapElement :
        // cineplexMap.entrySet()) {
        // ArrayList<Cinema> currCinemasArrayList = mapElement.getValue();
        // for (Cinema c: currCinemasArrayList) {
        // if (c.getCinemaID() == id &&
        // cineplex.toUpperCase().equals(mapElement.getKey())) {
        // return c;
        // }
        // }
        // }
        // return null;
    }

    /**
     * It checks if a cinema ID exists in a cineplex
     * 
     * @param id       the cinema id
     * @param cineplex String
     */
    public boolean checkCinemaIdExistsInCineplex(int id, String cineplex) {
        ArrayList<Cinema> cinemas = cineplexMap.get(cineplex);
        for (Cinema c : cinemas) {
            if (c.getCinemaID() == id)
                return true;

        }
        return false;
        // for (HashMap.Entry<String, ArrayList<Cinema>> mapElement :
        // cineplexMap.entrySet()) {
        // ArrayList<Cinema> currCinemasArrayList = mapElement.getValue();
        // for (Cinema c : currCinemasArrayList) {
        // if (c.getCinemaID() == id &&
        // cineplex.toUpperCase().equals(mapElement.getKey())) {
        // return true;
        // }
        // }
        // }
    }

    /**
     * It allows the user to define the layout of the cinema
     * using a UI
     * 
     * @param cinema the cinema object
     */
    public void defineLayout(Cinema cinema) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("------------");
            System.out.println("1. Use default layout (only for cinema with 10 rows and 16 columns)");
            System.out.println("2. Define custom layout");
            System.out.println("0. Exit");
            System.out.println("------------");

            while (!sc.hasNextInt()) {
                System.out.println("Please input a number value.");
                sc.next();
            }
            int userChoice = sc.nextInt();
            sc.nextLine();

            if (userChoice == 1) { // Use default layout
                if (cinema.getRows() != 10 || cinema.getColumns() != 16) { // Validate that cinema has exactly 10 rows
                                                                           // and 16 cols
                    System.out.println("Unsuccessful. Default layout cannot be used with cinema's current dimensions");
                } else {
                    // Regardless of whether cinema already had a defined layout, reset it and start
                    // from scratch
                    ArrayList<Seat> newSeatingPlan = new ArrayList<Seat>();
                    for (int i = 0; i < cinema.getRows(); i++) {
                        for (int j = 0; j < cinema.getColumns(); j++) {
                            Seat newSeat = new Seat(i, j, seatType.REGULAR, "nullId", false);
                            newSeatingPlan.add(newSeat);
                        }
                    }
                    cinema.setSeatingPlan(newSeatingPlan);

                    defineLayoutHelperDefineSeatType(cinema, seatType.EMPTY, true);
                    defineLayoutHelperDefineSeatType(cinema, seatType.COUPLE, true);
                    defineLayoutHelperDefineSeatType(cinema, seatType.ELITE, true);
                    defineLayoutHelperDefineSeatType(cinema, seatType.ULTIMA, true);
                    System.out.println("Layout is currently: ");
                    printCinema(cinema);
                }
            } else if (userChoice == 2) {
                // Define custom layout
                // Assume a complete, rectangular grid of regular seats.
                // First line of user input defines all the non-seats.
                // Second line defines couple seats, third line defines elite seats, fourth line
                // defines ultima seats.

                // Regardless of whether cinema already had a defined layout, reset it and start
                // from scratch
                ArrayList<Seat> newSeatingPlan = new ArrayList<Seat>();
                for (int i = 0; i < cinema.getRows(); i++) {
                    for (int j = 0; j < cinema.getColumns(); j++) {
                        Seat newSeat = new Seat(i, j, seatType.REGULAR, "nullId", false);
                        newSeatingPlan.add(newSeat);
                    }
                }
                cinema.setSeatingPlan(newSeatingPlan);

                System.out.println("Layout is currently: ");
                printCinema(cinema);

                // Set empty (invalid), couple, elite, ultima seats
                defineLayoutHelperDefineSeatType(cinema, seatType.EMPTY, false);
                defineLayoutHelperDefineSeatType(cinema, seatType.COUPLE, false);
                defineLayoutHelperDefineSeatType(cinema, seatType.ELITE, false);
                defineLayoutHelperDefineSeatType(cinema, seatType.ULTIMA, false);
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
     * It takes in a cinema object, a seatType enum, and a boolean flag. If the
     * boolean flag is true,
     * it will use a hardcoded array of seat positions to define the layout of the
     * cinema. If the
     * boolean flag is false, it will prompt the user to input a string of seat
     * positions, and then use
     * that string to define the layout of the cinema
     * 
     * @param cinema            the cinema object
     * @param sType             seatType enum
     * @param defaultLayoutFlag boolean that determines whether the admin wants to
     *                          use the default
     *                          layout or not
     */
    public void defineLayoutHelperDefineSeatType(Cinema cinema, seatType sType, boolean defaultLayoutFlag) {
        Scanner sc = new Scanner(System.in);
        String[] seatPositionsArray = null;
        if (defaultLayoutFlag) { // If admin wants to use default layout, hardcode seatPositionsArray
            if (sType == seatType.EMPTY) {
                seatPositionsArray = new String[] { "A1", "A2", "B1", "B2", "C1", "C2", "D1", "D2", "J9", "J10" };
            } else if (sType == seatType.COUPLE) {
                seatPositionsArray = new String[] { "H1", "H3", "H5", "H7", "H9", "H11", "H13", "H15" };
            } else if (sType == seatType.ELITE) {
                seatPositionsArray = new String[] { "I2", "I4", "I6", "I10", "I12", "I14" };
            } else if (sType == seatType.ULTIMA) {
                seatPositionsArray = new String[] { "J1", "J3", "J5", "J7", "J13", "J15" };
            }
        } else { // if admin wants a custom layout, create seatPositionsArray from user input
                 // Get 1 line of user input, which defines all instances of the particular
                 // seatType in the cinema
            if (sType == seatType.EMPTY) {
                System.out.println("Enter all invalid seats separated by spaces (e.g. a1 A2 B1 B2 C1 C2 D1 D2 J9 J10)");
            } else { // if 2-seater (the argument sType won't be a REGULAR seat)
                System.out.printf(
                        "Enter all %s seats separated by spaces (e.g. h1 H3 H7 H9 H11 H13 H15)\nNote: A1 represents a %s seat from A1-A2\n",
                        sType.name(), sType.name());
            }

            String userin = sc.nextLine();
            if (userin == "") { // if user input is an empty line, don't define for that seat type
                System.out.println("Layout is currently: ");
                printCinema(cinema);
                return;
            }

            // Validate user input
            boolean userInputFormatFlag = validateDefineLayoutUserInput(cinema, userin);

            while (!userInputFormatFlag) {
                if (sType == seatType.EMPTY) {
                    System.out.println("Enter all invalid seats separated by spaces (e.g. a1 A16 B1 B16)");
                } else { // if 2-seater (the argument sType won't be a REGULAR seat)
                    System.out.printf(
                            "Enter all %s seats separated by spaces (e.g. e1 E15 F2 F14)\nNote: A1 represents a %s seat from A1-A2\n",
                            sType.name(), sType.name());
                }
                userin = sc.nextLine();
                userInputFormatFlag = validateDefineLayoutUserInput(cinema, userin);
            }
            seatPositionsArray = userin.trim().replaceAll("\\s{2,}", " ").split(" "); // Splits by space, regardless of
                                                                                      // number of spaces
        }

        // Iterate through each seatPosition from the user input and update cinema's
        // seatingPlan accordingly
        for (String seatPos : seatPositionsArray) {
            char seatPosRowChar = Character.toUpperCase(seatPos.charAt(0));
            int seatPosRow = (int) seatPosRowChar - 65;
            int seatPosCol = Integer.parseInt(seatPos.substring(1)) - 1;
            int seatIndex = findSeatIndexByStartRowAndCol(cinema, seatPosRow, seatPosCol);
            if (validateDefineLayoutNoClash(cinema, seatPos)) {
                if (sType == seatType.EMPTY) {
                    cinema.getSeatingPlan().get(seatIndex).setSeatType(seatType.EMPTY);
                } else {
                    // Since 2-seater spans 2 seats, remove the right seat and set the left seat to
                    // the appropriate seatType
                    cinema.getSeatingPlan().remove(seatIndex + 1);
                    cinema.getSeatingPlan().get(seatIndex).setSeatType(sType);
                }
            }
        }

        if (defaultLayoutFlag == false) { // Print layout at each step only if admin is doing custom layout
            System.out.println("Layout is currently: ");
            printCinema(cinema);
        }
    }

    /**
     * If the seat is not found in the seating plan, it means it has already been
     * deleted to make way
     * for a 2-seater to the left
     * 
     * @param cinema  the cinema object
     * @param seatPos The position of the seat to be added.
     * @return The method returns a boolean value.
     */
    public boolean validateDefineLayoutNoClash(Cinema cinema, String seatPos) {
        // Check for 3 things:
        // 1. 2-seaters are not placed outside cinema's dimensions
        // 2. 2-seaters do not clash with sides or aisle
        // 3. 2-seaters do not clash with invalid seats or other predefined 2-seaters

        // (seatPos has been validated by the time this method runs)

        int lastColOfLeftHalf = cinema.getColumns() / 2;

        char seatPosRowChar = Character.toUpperCase(seatPos.charAt(0));
        int seatPosRow = (int) seatPosRowChar - 65; // 0-based indexing
        int seatPosCol = Integer.parseInt(seatPos.substring(1)) - 1; // 0-based indexing
        // 1. Validate row and col don't exceed cinema's dimensions
        if (seatPosRow >= cinema.getRows() || seatPosRow < 0 || seatPosCol >= cinema.getColumns() || seatPosCol < 0) {
            System.out.printf("Seat %c%d not added. It exceeds cinema's dimensions. \n", seatPosRowChar,
                    seatPosCol + 1);
            return false;
        }

        // 2. If last seat in left half clashes with aisle, or last seat in right half
        // clashes with RHS of cinema
        if (seatPosCol + 1 == lastColOfLeftHalf || seatPosCol + 1 == cinema.getColumns()) {
            System.out.printf("Seat %c%d not added. It clashes with aisle or sides. \n", seatPosRowChar,
                    seatPosCol + 1);
            return false;
        } else {
            int seatIndex = findSeatIndexByStartRowAndCol(cinema, seatPosRow, seatPosCol);
            if (seatIndex != -1) { // If seat match found
                seatType currSeatType = cinema.getSeatingPlan().get(seatIndex).getSeatType();
                if (currSeatType == seatType.EMPTY) { // 3a. If 2-seater clashes with invalid seat at that specific spot
                    System.out.printf("Seat %c%d not added. It is an invalid seat. \n", seatPosRowChar, seatPosCol + 1);
                    return false;
                } else if (currSeatType == seatType.COUPLE || currSeatType == seatType.ELITE
                        || currSeatType == seatType.ULTIMA) { // 3b. If 2-seater clashes with other 2-seaters at exact
                                                              // same spot
                    System.out.printf("Seat %c%d not added. It clashes exactly with another 2-seater. \n",
                            seatPosRowChar, seatPosCol + 1);
                    return false;
                } else if (cinema.getSeatingPlan().get(seatIndex + 1).getSeatType() == seatType.EMPTY
                        || cinema.getSeatingPlan().get(seatIndex + 1).getSeatType() == seatType.COUPLE
                        || cinema.getSeatingPlan().get(seatIndex + 1).getSeatType() == seatType.ELITE
                        || cinema.getSeatingPlan().get(seatIndex + 1).getSeatType() == seatType.ULTIMA) { // 3c. If
                                                                                                          // 2-seater
                                                                                                          // clashes
                                                                                                          // with
                                                                                                          // invalid
                                                                                                          // seats or
                                                                                                          // 2-seaters
                                                                                                          // to the
                                                                                                          // right
                    System.out.printf(
                            "Seat %c%d not added. It clashes with an invalid seat or another 2-seater to the right. \n",
                            seatPosRowChar, seatPosCol + 1);
                    // Above conditional shouldn't have index out of range error cos previous
                    // validations will alr check for end of row reached
                    return false;
                } else {
                    return true; // No clashes
                }
            } else { // If seat match not found
                     // 3d. If seat could not be found in the seating plan,
                     // that means it has already been deleted to make way for a 2-seater to the left
                System.out.printf("Seat %c%d not added. It clashes with another 2-seater to the left. \n",
                        seatPosRowChar, seatPosCol + 1);
                return false;
            }
        }
    }

    /**
     * It takes a user input string and validates that it has the expected
     * formatting (e.g. "A1 A16 b1 b16")
     * (Logic for validating no clashes is in validateDefineLayoutNoClash())
     * 
     * @param cinema the cinema object
     * @param userin "A1 A16 B1 B16"
     * @return The method is returning a boolean value.
     */
    public boolean validateDefineLayoutUserInput(Cinema cinema, String userin) {

        String[] seatPositionsArray = userin.trim().replaceAll("\\s{2,}", " ").split(" "); // Splits by space,
                                                                                           // regardless of number of
                                                                                           // spaces
        for (String seatPos : seatPositionsArray) {
            // Separate each supposed seat position string (like "A1") into the row and col
            // parts
            char seatPosRowChar = seatPos.charAt(0);

            if (!Character.isAlphabetic(seatPosRowChar)) { // Validate row char is a letter
                System.out.println("Invalid. First character of each seat must be a letter.");
                return false;
            }
            try { // Validate col String is numeric
                int seatPosCol = Integer.parseInt(seatPos.substring(1)) - 1;
            } catch (NumberFormatException e) {
                System.out.println("Invalid. Column for each seat must be a number.");
                return false;
            }
        }
        return true;
    }

    /**
     * It returns the index of the seat in the seats ArrayList that starts from the
     * specified row and
     * col
     *
     * Returns -1 if no seat begins from that specified row and col
     * If there's a 2-seater from A1-A2, findSeatIndexByStartRowAndCol() returns -1
     * when
     * row and col corresponding to "A2" is passed, since even though there's
     * technically a seat there,
     * its starting position is not A2 but instead at A1.
     * 
     * @param cinema The cinema object that contains the seating plan
     * @param row    The row of the seat
     * @param col    The column of the seat
     * @return The index of the seat in the ArrayList.
     */
    public int findSeatIndexByStartRowAndCol(Cinema cinema, int row, int col) {

        for (int i = 0; i < cinema.getSeatingPlan().size(); i++) { // Find seat that matches in the seats ArrayList
            if (cinema.getSeatingPlan().get(i).getRow() == row && cinema.getSeatingPlan().get(i).getCol() == col) { // If
                                                                                                                    // seat
                                                                                                                    // match
                                                                                                                    // found
                return i;
            }
        }
        return -1;
    }

    /**
     * It prints the cinema seating plan in a way that is easy to understand
     * 
     * @param cinema the cinema object
     */
    public void printCinema(Cinema cinema) {
        String screen = "----------SCREEN----------";
        // Print screen in the center of the 0th row
        for (int i = 0; i < (cinema.getColumns() * 3 + 6) / 2 - screen.length() / 2; i++) {
            System.out.print(" ");
        }
        System.out.println(screen);

        char rowLetter = 'A';
        int col = 0;

        // for a cinema with 16 cols, print row letter when col is 0 or (numOfCols + 1)
        // the aisle spaces won't be a separate column in the code logic, but will be
        // printed at the midpoint

        int seatNum = 0;
        while (seatNum < cinema.getSeatingPlan().size()) {
            if (col == 0) { // if start of row
                System.out.print(rowLetter + " ");
            } else if (col == cinema.getColumns() + 1) { // if end of row
                System.out.print(" " + rowLetter);
            } else { // if col is a seat
                if (col == cinema.getColumns() / 2 + 1) { // if col is first seat in right half of the row, add spaces
                                                          // before it to represent aisle
                    System.out.print("  ");
                }
                Seat currentSeat = cinema.getSeatingPlan().get(seatNum);
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
            } else if (col == cinema.getColumns() + 1) { // If end of row reached
                col = 0;
                rowLetter += 1;
                System.out.println();
            } else if (cinema.getSeatingPlan().get(seatNum).getSeatType() == seatType.REGULAR
                    || cinema.getSeatingPlan().get(seatNum).getSeatType() == seatType.EMPTY) {
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
     * It saves the cineplexMap object to a file
     */
    public static void saveData() {
        try {
            FileOutputStream fileOut = new FileOutputStream(filepath);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);

            objectOut.writeObject(cineplexMap);
            objectOut.close();
            System.out.println("Saved cineplex data.");
        } catch (Exception e) {
            System.out.println("Got an error while saving cineplexes data: " + e);
            // e.printStackTrace();
        }
    }

    /**
     * It reads the data from the filepath and returns the data as an object
     * 
     * @return The cineplexMap object that is being read from the file.
     */
    public static Object loadData() {
        try {
            FileInputStream fileIn = new FileInputStream(filepath);
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);

            Object obj = objectIn.readObject();
            objectIn.close();
            return obj;
        } catch (Exception e) {
            System.out.println("Got an error while loading cineplexes data: " + e);
            // e.printStackTrace();
            return null;
        }
    }
}