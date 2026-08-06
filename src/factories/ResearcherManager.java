package factories;

import entities.Booking;
import entities.Researcher;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;

/**
 * researcherManager
 */
public class ResearcherManager extends BaseManager<Researcher> {

    // has the crud operations
    public ResearcherManager() {
        super(Researcher.class);
    }

    public Researcher SearchResearcher(Long id) {
        EntityManager man = DbFactory.createManager();
        try {
            return man.find(Researcher.class, id);
        } finally {
            man.close();
        }
    }

    public class ResearcherWithBookings {

        public Researcher researcher;
        public Long bookingCount;

        public ResearcherWithBookings() {}
    }

    public List<ResearcherWithBookings> findAllResearchers(
        boolean orderByBookings
    ) {
        ResearcherManager resMan = new ResearcherManager();
        BookingManager bMan = new BookingManager();
        try {
            List<Researcher> researchers = resMan.findAll();

            List<Booking> bookings = bMan.findAll();

            Map<Long, ResearcherWithBookings> map = new HashMap<>();

            for (Researcher researcher : researchers) {
                ResearcherWithBookings rwb = new ResearcherWithBookings();
                rwb.researcher = researcher;
                rwb.bookingCount = 0L;
                map.put(researcher.getResearchId(), rwb);
            }

            for (Booking booking : bookings) {
                Researcher researcher = booking.getBookedBy();
                ResearcherWithBookings rwb = map.get(
                    researcher.getResearchId()
                );
                if (rwb != null) {
                    rwb.bookingCount++;
                }
            }

            List<ResearcherWithBookings> result = new ArrayList<>(map.values());

            if (orderByBookings) {
                result.sort((a, b) ->
                    Long.compare(b.bookingCount, a.bookingCount)
                );
            }

            return result;
        } finally {
        }
    }
}

// covered functions
// Register researcher basemanager.save
// Search researcher ->
