package validators;

import entities.Equipment;
import entities.Researcher;
import factories.BookingManager;

public class bookingValidator {

    public static boolean ValidateOS(Equipment eq) {
        return eq.getStatus() == "Out of Service";
    }

    public static boolean ValidateTimes(String start, String end) {
        return TimeValidator.ValidateOrder(start, end);
    }

    public static boolean researcherNumBookings(Researcher researcher) {
        // does backend lookup
        BookingManager man = new BookingManager();
        if (man.checkNumBookings(researcher) > 3) return false;
        else return true;
    }

    // searches for all bookings with this eq and checks theres no conflicting time slot
    // for the date
    public static boolean researcherConflictBooking(
        Equipment eq,
        String date,
        String start,
        String end
    ) {
        BookingManager man = new BookingManager();
        return man.checkTimesConflict(eq, date, start, end);
    }
}
