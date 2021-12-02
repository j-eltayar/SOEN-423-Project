package killian.core;

public class DateFactory {

    public static Date create(int year, int month, int day) {
        return new Date(year, month, day);
    }
}
