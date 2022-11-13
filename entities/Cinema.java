package entities;

import java.util.ArrayList;
import java.io.Serializable;
import entities.Seat.seatType;

/**
 * Cinema is a class that represents a cinema in a cineplex
 * Contains the cinemaID, classType, seatingPlan, row, column, and cineplex
 */
public class Cinema implements Serializable {
    /**
     * Enum for the type of Cinema, which will affect the final booking price.
     */
    public enum classType {
        PLATINUM, GOLDEN, NORMAL
    }

    private int cinemaID;
    private classType classtype;
    private ArrayList<Seat> seatingPlan;
    // private ArrayList<Showing> showingList; //sorted by DateTime.
    private int row;
    private int column; // --> print column/2 add space 1234 5678
    private String cineplex;

    // Constructor
    public Cinema(String cineplex, int id, classType val, int rows, int cols) {
        this.cineplex = cineplex;
        this.cinemaID = id;
        this.classtype = val;
        this.row = rows;
        this.column = cols; // Assumed to have even number of columns
        this.seatingPlan = new ArrayList<Seat>();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Seat tempSeat = new Seat(i, j, seatType.REGULAR, "nullId", false);
                this.seatingPlan.add(tempSeat);
            }
        }
    }

    // Getters
    public int getCinemaID() {
        return this.cinemaID;
    }

    public String getCineplex() {
        return this.cineplex;
    }

    public classType getClassType() {
        return this.classtype;
    }

    public ArrayList<Seat> getSeatingPlan() {
        return this.seatingPlan;
    }

    public int getColumns() {
        return this.column;
    }

    public int getRows() {
        return this.row;
    }

    // Setters
    public void setCinemaID(int cinemaID) {
        this.cinemaID = cinemaID;
    }

    public void setClassType(classType classType) {
        this.classtype = classType;
    }

    public void setSeatingPlan(ArrayList<Seat> seatingPlan) {
        this.seatingPlan = seatingPlan;
    }

    public void setColumns(int column) {
        this.column = column;
    }

    public void setCineplex(String cineplex) {
        this.cineplex = cineplex;
    }

}