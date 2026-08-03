package validators;

import entities.Equipment;

public class bookingValidator {

    boolean ValidateOS(Equipment eq) {
        return eq.getStatus() == "Out of Service";
    }
}
