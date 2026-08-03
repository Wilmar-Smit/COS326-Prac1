package validators;

import entities.Booking;
import entities.Equipment;
import entities.Researcher;

public class bookingValidator {

    boolean ValidateOS(Equipment eq) {
        return eq.getStatus() == "Out of Service";
    }

    boolean ValidateTimes(String start, String end) {
        return TimeValidator.ValidateOrder(start, end);
    }

    boolean researcherNumBookings(Researcher researcher) {
        // does backend lookup
        return false;
    }

    // searches for all bookings with this eq and checks theres no conflicting time slot
    // for the date
    boolean researcherConflictBooking(
        Equipment eq,
        String date,
        String start,
        String end
    ) {
        return false;
    }
}
