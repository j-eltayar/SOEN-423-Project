package killian.core;

import java.util.HashMap;
import java.util.Set;

public class Database {

    private final Campus campus;
    private final HashMap<Date, HashMap<Integer, HashMap<TimeSlot, RoomRecord>>> dates;
    private final HashMap<String, Integer> studentReservationsCount;

    public Database(Campus campus) {
        this.campus = campus;
        this.studentReservationsCount = new HashMap<>();
        dates = new HashMap<>();
    }

    synchronized public String deleteRoomRecords(Date date, int roomNumber, Set<TimeSlot> timeSlotSet) throws BookingException {
        if (dates.containsKey(date)) {
            if (dates.get(date).containsKey(roomNumber)) {
                for (TimeSlot timeSlot : timeSlotSet) {
                    if (dates.get(date).get(roomNumber).containsKey(timeSlot)) {
                        return "timeslot: " + dates.get(date).get(roomNumber).remove(timeSlot) + " is deleted\n";
                    } else {
                        throw new BookingException("Wrong timeslot for room. room: " + roomNumber + " timeslot: " + timeSlot);
                    }
                }
                throw new BookingException("No time slot found");
            } else {
                throw new BookingException("No room number for that date.");
            }
        } else {
            throw new BookingException("No room records for that date.");
        }
    }

    synchronized public String addTimeSlot(Date date, int roomNumber, TimeSlot timeSlot) throws BookingException {
        if (!dates.containsKey(date)) {
            addDate(date);
        }
        if (!dates.get(date).containsKey(roomNumber)) {
            addRoom(date, roomNumber);
        }
        if (dates.get(date).get(roomNumber).containsKey(timeSlot)) {
            throw new BookingException("The TimeSlot already exists.");
        } else if (overlapsTimeSlots(date, roomNumber, timeSlot)){
            throw new BookingException("The TimeSlot overlaps another TimeSlot.");
        } else {
            dates.get(date).get(roomNumber).put(timeSlot, RoomRecordFactory.create(date, roomNumber, timeSlot));
            return "SUCCESS";
        }
    }

    synchronized public int getTimeSlotAvailableCount(Date some_date) {
        int count = 0;
        if (!dates.isEmpty() && dates.containsKey(some_date)) for (int roomNumber : dates.get(some_date).keySet()) {
            if (dates.get(some_date).containsKey(roomNumber))
                for (RoomRecord record : dates.get(some_date).get(roomNumber).values()) {
                    if (!record.isBooked()) {
                        count++;
                    }
                }
        }
        return count;
    }

    synchronized public String cancelBooking(String bookingID) throws BookingException {
        try {
            RoomRecord record = dates.values().stream()
                    .flatMap(integerHashMapHashMap -> integerHashMapHashMap.values().stream()
                            .flatMap(timeSlotRoomRecordHashMap -> timeSlotRoomRecordHashMap.values().stream()))
                    .filter(roomRecord -> roomRecord.getBookingID().equals(bookingID)).findFirst().orElse(null);
            if (record == null) {
                throw new BookingException("There is no bookingID matching what was provided.");
            } else {
                String studentID = record.getBookedBy();
                record.cancelReservation(bookingID);
                int count = studentReservationsCount.get(studentID);
                count--;
                studentReservationsCount.put(studentID, count);
                return "SUCCESS: " + bookingID + " was successfully cancelled.";
            }
        } catch (NullPointerException e) {
            throw new BookingException("ERROR: No bookingID matches.");
        }
    }

    synchronized public String makeBooking(Date date, int roomNumber, TimeSlot timeSlot, String studentID) throws BookingException {
        System.out.println(Thread.currentThread().getId());
        String bookingID;
        if (hasTimeSlot(date, roomNumber, timeSlot)) {
            try {
                int count = studentReservationsCount.getOrDefault(studentID, 0);
                bookingID = dates.get(date).get(roomNumber).get(timeSlot).bookRoom(campus.name(), studentID);
                count++;
                String altBookingID = campus.name() + "|" + date.toString() + "|" + roomNumber + "|" + timeSlot.getStart() + "|" + studentID;
                studentReservationsCount.put(studentID, count);
                return altBookingID;
            } catch (BookingException e) {
                throw new BookingException(e.getMessage());
            }
        } else {
            throw new BookingException("There are no timeslots matching what was provided.");
        }
    }

    synchronized public int getBookingNum(String studentID) {
        return studentReservationsCount.getOrDefault(studentID, 0);
    }

    private void addDate(Date date) {
        if (!dates.containsKey(date)) {
            dates.put(date, new HashMap<>());
        }
    }

    private void addRoom(Date date, int roomNumber) {
        if (!dates.containsKey(date)) {
            addDate(date);
        }
        if (!dates.get(date).containsKey(roomNumber)) {
            dates.get(date).put(roomNumber, new HashMap<>());
        }
    }

    private boolean overlapsTimeSlots(Date date, int roomNumber, TimeSlot timeSlot) {
        return dates.get(date).get(roomNumber).entrySet().stream().anyMatch(timeSlotRoomRecordEntry -> timeSlot.overlaps(timeSlotRoomRecordEntry.getKey()));
    }

    private boolean hasDate(Date date) {
        return dates.containsKey(date);
    }

    private boolean hasRoom(Date date, int roomNumber) {
        return hasDate(date) && (dates.get(date).containsKey(roomNumber));
    }

    private boolean hasTimeSlot(Date date, int roomNumber, TimeSlot timeSlot) {
        return hasRoom(date, roomNumber) && (dates.get(date).get(roomNumber).containsKey(timeSlot));
    }
}
