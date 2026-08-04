import entities.Booking;
import entities.Equipment;
import entities.Researcher;
import factories.*;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        // 1. Instantiate managers
        ResearcherManager researcherManager = new ResearcherManager();
        EquipmentManager equipmentManager = new EquipmentManager();
        BookingManager bookingManager = new BookingManager();

        try {
            // 2. Create entities
            Researcher researcher = new Researcher();
            researcher.setFullName("Dr. Aris Thorne");
            researcher.setDepartment("Computer Science");

            Equipment equipment = new Equipment();
            equipment.setName("Spectrometer X-200");
            equipment.setCategory("Lab Gear");

            // 3. Save parent entities first so they get assigned IDs
            researcherManager.save(researcher);
            equipmentManager.save(equipment);

            // 4. Create and link booking
            Booking booking = new Booking(
                "2026-08-10",
                "09:00",
                "12:00",
                "Algorithm Performance Analysis",
                "CONFIRMED",
                equipment,
                researcher
            );

            // 5. Save booking
            bookingManager.save(booking);

            System.out.println("--- Saved Entities Successfully ---");

            // 6. Fetch All Researchers
            List<Researcher> researchers = researcherManager.findAll();
            System.out.println("\nResearchers (" + researchers.size() + "):");
            for (Researcher r : researchers) {
                System.out.println(
                    " - ID: " +
                        r.getResearchId() +
                        " | Name: " +
                        r.getFullName()
                );
            }

            // 7. Fetch All Equipment
            List<Equipment> equipmentList = equipmentManager.findAll();
            System.out.println("\nEquipment (" + equipmentList.size() + "):");
            for (Equipment e : equipmentList) {
                System.out.println(
                    " - ID: " + e.getId() + " | Name: " + e.getName()
                );
            }

            // 8. Fetch All Bookings
            List<Booking> bookings = bookingManager.findAll();
            System.out.println("\nBookings (" + bookings.size() + "):");
            for (Booking b : bookings) {
                System.out.println(
                    " - ID: " +
                        b.getId() +
                        " | Date: " +
                        b.getDate() +
                        " | Researcher: " +
                        b.getBookedBy().getFullName() +
                        " | Equipment: " +
                        b.getBookedEQ().getName()
                );
            }
        } finally {
            // 9. Clean up factory resources on shutdown
            DbFactory.close();
        }
    }
}
