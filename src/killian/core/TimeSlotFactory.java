package killian.core;

public class TimeSlotFactory {

    public static TimeSlot create(Time start, Time end) {
        return new TimeSlot(start, end);
    }

    public static TimeSlot create(int startHour, int startMin, int endHour, int endMin) {
        return new TimeSlot(TimeFactory.create(startHour, startMin), TimeFactory.create(endHour, endMin));
    }
}
