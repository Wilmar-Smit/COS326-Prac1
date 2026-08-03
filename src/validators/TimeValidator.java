package validators;

public class TimeValidator {

    public static boolean ValidateFormat(String time) {
        if (time == null) {
            return false;
        }
        return time.matches("^(?:[01]\\d|2[0-3]):[0-5]\\d$");
    }

    public static boolean ValidateOrder(String start, String end) {
        if (ValidateFormat(start) && ValidateFormat(end)) {
            return start.compareTo(end) < 0;
        } else {
            return false;
        }
    }
}
