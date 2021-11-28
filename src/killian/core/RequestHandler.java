package killian.core;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class RequestHandler implements Runnable {

    private final CampusServerImpl server;
    DatagramSocket socket;
    InetAddress responseAddress;
    int port;
    String request;
    String response;


    public RequestHandler(CampusServerImpl server, DatagramPacket packet) {
        this.server = server;
        this.socket = server.getSocket();
        this.responseAddress = packet.getAddress();
        this.port = packet.getPort();
        this.request = new String(packet.getData(), 0, packet.getLength());
    }

    public void run() {

        System.out.println(request);
        String[] args = request.split("\\s");

        switch (args[0]) {
            case "GET_COUNT":
                getCount(args);
                break;
            case "GET_BOOKING_COUNT":
                getBookingNum(args);
            case "BOOK_ROOM":
                bookRoom(args);
                break;
            case "CANCEL_ROOM":
                cancelRoom(args);
                break;
        }
    }

    private void getBookingNum(String[] args) {
        if (args.length == 2) {
            String studentID = args[1];
            response = server.getBookingNum(studentID);
        } else {
            response = "ERROR : Not correct number of arguments.";
        }
        sendResponse();
    }

    private void getCount(String[] args) {
        if (args.length == 2 && args[1].matches("\\d{2}-\\d{2}-\\d{4}")) {
            int day = Integer.parseInt(args[1].split("-")[0]);
            int month = Integer.parseInt(args[1].split("-")[1]);
            int year = Integer.parseInt(args[1].split("-")[2]);
            response = server.getCampus().name() + " : " + server.getDatabase().getTimeSlotAvailableCount(DateFactory.create(year, month, day));
        } else {
            response = "ERROR : Not correct number of arguments or does not match date format.";
        }
        sendResponse();
    }

    private void bookRoom(String[] args) {
        if (args.length == 6) {
            int roomNumber = Integer.parseInt(args[1]);
            int day = Integer.parseInt(args[2].split("-")[0]);
            int month = Integer.parseInt(args[2].split("-")[1]);
            int year = Integer.parseInt(args[2].split("-")[2]);
            int startHour = Integer.parseInt(args[3].split(":")[0]);
            int startMin = Integer.parseInt(args[3].split(":")[1]);
            int endHour = Integer.parseInt(args[4].split(":")[0]);
            int endMin = Integer.parseInt(args[4].split(":")[1]);
            String id = args[5];
            StringBuilder sb = new StringBuilder();
            for (String arg : args) {
                sb.append(arg).append(" ");
            }
            System.out.println(sb);

            response = server.bookRoom(
                    server.getCampus(),
                    roomNumber,
                    DateFactory.create(year, month, day),
                    TimeSlotFactory.create(startHour, startMin, endHour, endMin),
                    id);
        } else {
            response = "ERROR : Not correct number of arguments.";
        }
        sendResponse();
    }

    private void cancelRoom(String[] args) {
        if (args.length == 3) {
            response = server.cancelBooking(args[1], args[2]);
        } else {
            response = "ERROR : Not correct number of arguments.";
        }
        sendResponse();
    }

    private void sendResponse() {
        byte[] buff = response.getBytes();
        DatagramPacket responsePacket = new DatagramPacket(buff, buff.length, responseAddress, port);

        try {
            socket.send(responsePacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

