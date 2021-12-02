package killian.core;

public interface CampusServer extends IStudentServer, IAdminServer {

    String createRoom(int roomNumber, Date date, TimeSlot[] timeSlots, String id) throws BookingException;

    String deleteRoom(int roomNumber, Date date, TimeSlot[] timeSlots, String id) throws BookingException;

    String getAvailableTimeSlot(Date date, String id);

    String bookRoom(Campus campusName, int roomNumber, Date date, TimeSlot timeSlot, String id) throws BookingException;

    String cancelBooking(String bookingID, String id) throws BookingException;

    String changeReservation(String bookingID, Campus newCampus, int newRoomNumber, Date newDate, TimeSlot newTimeSlot, String id) throws BookingException;
}
