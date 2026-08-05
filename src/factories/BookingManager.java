package factories;

import entities.Booking;
import entities.Equipment;
import entities.Researcher;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

/**
 * BookingManager
 */
public class BookingManager extends BaseManager<Booking> {

    public BookingManager() {
        super(Booking.class);
    }

    public int checkNumBookings(Researcher researcher) {
        if (researcher == null || researcher.getResearchId() == null) {
            return 0;
        }
        EntityManager man = DbFactory.createManager();
        try {
            TypedQuery<Long> query = man.createQuery(
                "SELECT COUNT(b) FROM Booking b WHERE b.bookedBy = :researcher AND b.status = :bookingStatus",
                Long.class
            );
            query.setParameter("researcher", researcher);
            query.setParameter("bookingStatus", Booking.ACTIVE);

            return query.getSingleResult().intValue();
        } finally {
            man.close();
        }
    }

    public boolean checkTimesConflict(
        Equipment eq,
        String date,
        String start,
        String end,
        Booking booking
    ) {
        if (eq == null || eq.getId() == null) {
            return false;
        }

        EntityManager man = DbFactory.createManager();

        try {
            TypedQuery<Long> query = man.createQuery(
                "SELECT COUNT(b) FROM entities.Booking b " +
                    "WHERE b.bookedEQ = :eq " +
                    "AND b.date = :inputDate " +
                    "AND b.startTime < :newEnd " +
                    "AND b.endTime > :newStart " +
                    "AND b.status = 'Active'" +
                    "AND b.id <> :bookingId",
                Long.class
            );

            query.setParameter("eq", eq);
            query.setParameter("inputDate", date);
            query.setParameter("newStart", start);
            query.setParameter("newEnd", end);

            query.setParameter(
                "bookingId",
                booking != null ? booking.getId() : -1
            );

            Long count = query.getSingleResult();
            return count > 0;
        } finally {
            man.close();
        }
    }
}
