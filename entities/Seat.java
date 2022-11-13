package entities;

import java.io.Serializable;

/**
 * A seat is a space in the Cinema that can be occupied by a moviegoer.
 * It will hold row and column data, occupancy status, and seat type.
 */
public class Seat implements Serializable {

  /**
   * Enum for the type of Seat, which will affect the final booking price.
   * Also includes empty seats, which will affect the drawn Cinema layout.
   */
  public enum seatType {
    REGULAR, COUPLE, ELITE, ULTIMA, EMPTY
  }

  private int row;
  private int col;
  private String seatId;
  private seatType sType;
  private boolean occupied;

  // row and column are identifiers for a space, the seat ID is the identifier
  // a seat -- couple seats take up 2 spaces but share the same seat id
  public Seat(int row, int col, seatType sType, String seatId, boolean occupied) {
    this.row = row;
    this.col = col;
    this.seatId = seatId;
    this.sType = sType;
    this.occupied = false;
  }

  // getters

  public int getRow() {
    return this.row;
  }

  public int getCol() {
    return this.col;
  }

  public boolean getOccupancy() {
    return this.occupied;
  }

  public String getSeatId() {
    return this.seatId;
  }

  public seatType getSeatType() {
    return this.sType;
  }

  // setters

  public void setOccupancy(boolean occupancy) {
    this.occupied = occupancy;
  }

  public void setSeatId(String id) {
    this.seatId = id;
  }

  public void setSeatType(seatType sType) {
    this.sType = sType;
  }
}