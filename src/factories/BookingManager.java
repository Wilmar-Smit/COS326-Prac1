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

            if (booking != null && booking.getId() != null) {
                query.setParameter("bookingId", booking.getId());
            } else {
                query.setParameter("bookingId", -1);
            }

            Long count = query.getSingleResult();
            return count > 0;
        } finally {
            man.close();
        }
    }

    public boolean researchEqDateCheck(
        Equipment eq,
        String date,
        Researcher res,
        Booking booking
    ) {
        if (eq == null || eq.getId() == null) {
            return false;
        }
        if (res == null || res.getResearchId() == null) {
            return false;
        }

        EntityManager man = DbFactory.createManager();

        try {
            TypedQuery<Long> query = man.createQuery(
                "SELECT COUNT(b) FROM entities.Booking b WHERE b.date = :inputDate AND b.bookedEQ = :eq " +
                    "AND  b.bookedBy = :res " +
                    "AND b.id <> :bookingId ",
                Long.class
            );

            query.setParameter("eq", eq);
            query.setParameter("inputDate", date);
            query.setParameter("res", res);

            if (booking != null && booking.getId() != null) {
                query.setParameter("bookingId", booking.getId());
            } else {
                query.setParameter("bookingId", -1);
            }

            Long count = query.getSingleResult();
            // if count > 0 returns true -> means there is conflict
            return count > 0;
        } finally {
            man.close();
        }
    }
}
