package killian.core;

public interface IAdminServer {

    String createRoom(int roomNumber, Date date, TimeSlot[] timeSlots, String id) throws BookingException;

    String deleteRoom(int roomNumber, Date date, TimeSlot[] timeSlots, String id) throws BookingException;

}
