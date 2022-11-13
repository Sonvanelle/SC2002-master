package entities;

/**
 * Enum for the showing status of the Movie, which determines if Bookings
 * can be made for it. Movies that are END_OF_SHOWING do not appear to moviegoers.
 */
public enum showingStatus {
    COMING_SOON("COMING SOON"),
    PREVIEW("PREVIEW"),
    NOW_SHOWING("NOW_SHOWING"),
    END_OF_SHOWING("END_OF_SHOWING");

    private final String nameText;

    private showingStatus(String nameText) {
        this.nameText = nameText;
    }

    public boolean isEqual(String nameToCompare) {
        return nameText.equals(nameToCompare);
    }

    public String toString() {
        return this.nameText;
    }
}
