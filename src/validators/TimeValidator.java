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

    public static boolean isValidDateFormat(String date) {
        if (!date.matches("^\\d{4}/\\d{2}/\\d{2}$")) {
            return false;
        }

        String[] parts = date.split("/");
        int month = Integer.parseInt(parts[1]);
        int day = Integer.parseInt(parts[2]);

        if (month < 1 || month > 12) return false;
        if (day < 1 || day > 31) return false;

        return true;
    }
}
