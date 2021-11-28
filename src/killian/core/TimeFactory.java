package killian.core;

public class TimeFactory {

    public static Time create(int hour, int min) {
        return new Time(hour, min);
    }
}
