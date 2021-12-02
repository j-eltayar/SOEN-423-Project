package killian.core;

import javax.xml.ws.Endpoint;
import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CampusServerImpl implements CampusServer, Runnable {

    Campus campus;
    Database database;
    DatagramSocket socket;

    public CampusServerImpl() {
    }

    public CampusServerImpl(Campus campus) throws SocketException {
        super();
        this.database = new Database(campus);
        this.campus = campus;
        try {
            this.socket = new DatagramSocket(6000 + campus.getIndex());
        } catch (SocketException e) {
            System.out.println("ERROR : " + e.getMessage());
            throw e;
        }
    }

    @Override
    public String createRoom(int roomNumber, Date date, TimeSlot[] timeSlots, String id) {
        try {
            this.database.addTimeSlot(date, roomNumber, timeSlots[0]);
            return "CREATE ROOM (SUCCESS)";
        } catch (BookingException e) {
            System.out.println("ERROR : " + e.getMessage());
            return "CREATE ROOM (FAILURE)";
        }
    }

    @Override
    public String deleteRoom(int roomNumber, Date date, TimeSlot[] timeSlots, String id) {
        Set<TimeSlot> timeSlotSet = new HashSet<>(Arrays.asList(timeSlots));
        try {
            this.database.deleteRoomRecords(date, roomNumber, timeSlotSet);
            return "DELETE ROOM (SUCCESS)";
        } catch (BookingException e) {
            System.out.println("ERROR : " + e.getMessage());
            return "DELETE ROOM (FAILURE)";
        }
    }

    @Override
    public String getAvailableTimeSlot(Date date, String id) {
        StringBuilder sb = new StringBuilder();
        sb.append(campus.name())
                .append(" : ")
                .append(this.database.getTimeSlotAvailableCount(date))
                .append("\n");

        String message = "GET_COUNT " + date.toString();
        for (Campus campus : Campus.values()) {

            //send request for counts except if this campus
            if (campus.getIndex()  != this.campus.getIndex()) {
                String response = sendRequest(campus, message);
                sb.append(response).append("\n");
            }
        }
        return sb.toString();
    }

    @Override
    public String bookRoom(Campus campus, int roomNumber, Date date, TimeSlot timeSlot, String id) {
        int bookingCount = this.database.getBookingNum(id);
        String message = "GET_BOOKING_COUNT " + id;
        try {
            for (Campus campusName : Campus.values()) {
                //send request for counts except if this campus
                if (campusName.getIndex()  != this.campus.getIndex()) {
                    String response = sendRequest(campusName, message);
                    if (response.startsWith("ERROR")) {
                        throw new BookingException("cannot book at this time since not all servers are up.");
                    } else {
                        bookingCount += Integer.parseInt(response);
                    }
                }
            }
        } catch (Exception e) {
            return "BOOK ROOM (FAILURE)";
        }

        if (bookingCount < 3) {
            return doBooking(campus, roomNumber, date, timeSlot, id);
        } else {
            return "BOOK ROOM (FAILURE)";
        }
    }


    private String doBooking(Campus campus, int roomNumber, Date date, TimeSlot timeSlot, String id) {
        String message;
        if (campus != this.campus) {
            message = "BOOK_ROOM " + roomNumber + " " + date.toString() + " " + timeSlot.getStart() + " " + timeSlot.getEnd() + " " + id;
            return sendRequest(campus, message);
        } else {
            try {
                return this.database.makeBooking(date, roomNumber, timeSlot, id);
            } catch (BookingException e) {
                System.out.println("ERROR : " + e.getMessage());
                return "BOOK ROOM (FAILURE)";
            }
        }
    }

    @Override
    public String cancelBooking(String bookingID, String id) {
        String campusName = bookingID.substring(0, 3);
        if (!campus.name().equals(campusName)) {
            String message = "CANCEL_ROOM " + bookingID + " " + id;
            Campus campus;
            try {
                 campus = Campus.valueOf(campusName);
            } catch (IllegalArgumentException e) {
                System.out.println("ERROR : " + e.getMessage());
                return "CANCEL BOOKING (FAILURE)";
            }
            return sendRequest(campus, message);
        } else {
            try {
                this.database.cancelBooking(bookingID);
                return "CANCEL BOOKING (SUCCESS)";
            } catch (BookingException e) {
                System.out.println("ERROR : " + e.getMessage());
                return "CANCEL BOOKING (FAILURE)";
            }
        }
    }

    @Override
    public String changeReservation(String bookingID, Campus newCampus, int newRoomNumber, Date newDate, TimeSlot newTimeSlot, String id) {
        String bookRoomResponse = doBooking(newCampus, newRoomNumber, newDate, newTimeSlot, id);
        if (bookRoomResponse.startsWith("SUCCESS")) {
            String newBookingID = bookRoomResponse.split("\\s")[1];
            String cancelBookingResponse = cancelBooking(bookingID, id);
            if (cancelBookingResponse.startsWith("SUCCESS")) {
                return newBookingID;
            } else {
                cancelBooking(newBookingID, id);
                return "CHANGE RESERVATION (FAILURE)";
            }
        } else {
            return "CHANGE RESERVATION (FAILURE)";
        }
    }

    @Override
    public void run() {
        //noinspection InfiniteLoopStatement
        while (true) {
            byte[] buff = new byte[256];
            DatagramPacket packet = new DatagramPacket(buff, buff.length);
            try {
                socket.receive(packet);
                new Thread(new RequestHandler(this, packet)).start();
            } catch (IOException e) {
                System.out.println("ERROR : " + e.getMessage());
            }
        }
    }

    private String sendRequest(Campus campusName, String message) {
        byte[] buf;
        InetAddress address;
        try {
            address = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            System.out.println("ERROR : " + e.getMessage());
            return "ERROR : There was an error on the server " + campus.name() + " " + e.getMessage();
        }
        int serverPort = 6000 + campusName.getIndex();
        buf = message.getBytes();
        try {
            return getResponse(buf, address, serverPort, campusName.name());
        } catch (IOException e) {
            System.out.println("ERROR : " + e.getMessage());
            return "ERROR : There was an error on the server " + campus.name() + " " + e.getMessage();
        }
    }

    private String getResponse(byte[] buf, InetAddress address, int serverPort, String dest) throws IOException {
        try {
            DatagramSocket socket = new DatagramSocket();
            socket.setSoTimeout(1000);
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, serverPort);
            socket.send(packet);
            byte[] receiveByte = new byte[256];
            packet = new DatagramPacket(receiveByte, receiveByte.length);
            socket.receive(packet);
            return new String(packet.getData(), 0, packet.getLength());
        } catch (SocketTimeoutException e) {
            System.out.println("TIMEOUT");
            return "ERROR: " + dest + "  TIMEOUT ERROR";
        }
    }

    public DatagramSocket getSocket() {
        return socket;
    }

    public Campus getCampus() {
        return this.campus;
    }

    public Database getDatabase() {
        return this.database;
    }

    public String getBookingNum(String studentID) {
        return String.valueOf(this.database.getBookingNum(studentID));
    }
}
