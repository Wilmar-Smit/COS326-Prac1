package factories;

import entities.Booking;

/**
 * BookingManager
 */
public class BookingManager extends BaseManager<Booking> {

    BookingManager() {
        super(Booking.class);
    }
}
