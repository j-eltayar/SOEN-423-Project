package killian.core;

public class RoomRecordFactory {

    public static RoomRecord create(Date date, int roomNumber, TimeSlot timeSlot) {
        return new RoomRecord(date, roomNumber, timeSlot);
    }
}
