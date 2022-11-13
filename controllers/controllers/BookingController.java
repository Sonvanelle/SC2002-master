package controllers;

import entities.Booking;
import entities.MovieGoer;
import entities.Seat;
import entities.Showing;

import java.io.Serializable;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Controller class that handles the creation of bookings, and also
 * handles the saving and
 * loading of booking data
 */
public class BookingController implements Serializable {

    private static final String filepath = "bookinghistory.ser"; // testing purposes

    private static ArrayList<Booking> bookingHistory;

    // holds an instance of the controller
    private static BookingController controllerInstance = null;

    // methods
    /*
     * Instantiate a controller object when called.
     * Also deserializes bookingHistory from file; if none found/null object, create
     * a new one.
     */
    @SuppressWarnings("unchecked")
    public static BookingController getController() {
        if (controllerInstance == null) {
            controllerInstance = new BookingController();
        }
        bookingHistory = (ArrayList<Booking>) loadData();
        if (bookingHistory == null) {
            System.out.println("No bookingHistory found; creating new file.");
            bookingHistory = new ArrayList<Booking>();
            saveData();
        }
        return controllerInstance;
    }

    /**
     * This function gets the price of a booking
     * 
     * @param booking the booking object
     * @return The price of the booking.
     */
    public double getPrice(Booking booking) {
        Showing showing = booking.getShowing();
        Seat seat = booking.getSeat();

        double price = SettingsController.getController().getPrice(seat.getSeatType(), showing, booking.getMovieGoer());
        return price;
    }

    /**
     * This function creates a booking object and adds it to the booking history
     * list
     * 
     * @param showing   Showing object
     * @param movieGoer MovieGoer object
     * @param seat      Seat
     * @param cinemaID  int
     * @param cineplex  String
     */
    public void createBooking(Showing showing, MovieGoer movieGoer, Seat seat, int cinemaID, String cineplex) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatStr = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        String tid = now.format(formatStr);
        tid = showing.getShowingCinema().getCinemaID() + tid;

        Booking booking = new Booking(tid, movieGoer, seat, showing, cinemaID, cineplex);
        MovieController.getController().addSale(showing.getMovie().getMovieName(),
                SettingsController.getController().getPrice(seat.getSeatType(), showing, movieGoer));

        bookingHistory.add(booking);
        booking.printBooking();
        saveData();
    }

    /**
     * This function is used to list all the bookings made by a moviegoer
     * 
     * @param movieGoer the moviegoer who is making the booking
     */
    public void listBookingViaAccount(MovieGoer movieGoer) {
        if (bookingHistory.size() == 0) {
            System.out.println("No bookings found.");
            return;
        }
        for (int i = 0; i < bookingHistory.size(); i++) {
            if (bookingHistory.get(i).getMovieGoer().equals(movieGoer)) {
                bookingHistory.get(i).printBooking();
            }
        }
    }

    /**
     * It takes the bookingHistory object and writes it to a file
     */
    public static void saveData() {
        try {
            FileOutputStream fileOut = new FileOutputStream(filepath);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);

            objectOut.writeObject(bookingHistory);
            objectOut.close();
        } catch (IOException e) {
            System.out.println("Got an error while saving booking data: " + e);
            // e.printStackTrace();
        }
    }

    /**
     * It reads the file (bookingHistory) at the filepath, and returns the object
     * that was saved there
     * 
     * @return The object that was read from the file.
     */
    public static Object loadData() {
        try {
            FileInputStream fileIn = new FileInputStream(filepath);
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);

            Object obj = objectIn.readObject();
            objectIn.close();
            return obj;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Got an error while loading booking data: " + e);
            return null;
        }
    }
}