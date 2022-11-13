package entities;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import controllers.SettingsController;

/**
 * Class that stores the booking details of a moviegoer, for a movie showing.
 */
public class Booking implements Serializable {
	private String TID;
	private MovieGoer movieGoer;
	private Showing showing;
	private int cinemaID;
	private String cineplex;
	private LocalDateTime bookingTime;
	private Seat seat;

	public Booking(String TID, MovieGoer movieGoer, Seat seat, Showing showing, int cinemaID, String cineplex) {
		this.TID = TID;
		this.movieGoer = movieGoer;
		this.seat = seat;
		this.showing = showing;
		this.cinemaID = cinemaID;
		this.cineplex = cineplex;
		this.seat.setOccupancy(true);
		this.bookingTime = LocalDateTime.now();
	}

	// methods
	/**
	 * It prints out the booking details in a pretty manner
	 */
	public void printBooking() {
		System.out.printf("Booking Transaction ID: %s\n", TID);
		System.out.printf("Booking timestamp: %s\n",
				bookingTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		System.out.printf("Cineplex: %s\n", cineplex);
		System.out.printf("Cinema: %d\n", cinemaID);
		System.out.print("Showing: " + showing.getMovie().getMovieName() + " at " + showing.getShowtime() + "\n");

		int tempRowNum = seat.getRow() + 65;
		String seatPosRowString = Character.toString((char) tempRowNum);
		int seatPosRowCol = seat.getCol() + 1;
		System.out.printf("Seat: %s%d\n", seatPosRowString, seatPosRowCol);
		System.out.print(
				"Amount paid: $" + SettingsController.getController().getPrice(this.seat.getSeatType(), this.showing,
						this.movieGoer) + "\n\n");
	}

	// getters

	public String getTID() {
		return this.TID;
	}

	public MovieGoer getMovieGoer() {
		return this.movieGoer;
	}

	public Seat getSeat() {
		return this.seat;
	}

	public Showing getShowing() {
		return this.showing;
	}

	public int getCinemaID() {
		return this.cinemaID;
	}

	public String getCineplex() {
		return this.cineplex;
	}

	public LocalDateTime getBookingTime() {
		return this.bookingTime;
	}

	// setters

	public void setTID(String TID) {
		this.TID = TID;
	}

	public void setMovieGoer(MovieGoer movieGoer) {
		this.movieGoer = movieGoer;
	}
}