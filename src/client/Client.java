package client;

import java.util.Scanner;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import RMAPP.*;

public class Client
{
    private String id;
    private String clientLogName;
    private String location;
    static RM currentReplicaManager;
    
    public Client(String id, String location, ORB orb) throws IOException {
        this.id = id;
        this.clientLogName = "ClientLogs\\" + id + ".txt";
        final File yourFile = new File(this.clientLogName);
        yourFile.createNewFile();
        this.location=location;
        // Set up which replica manager to communicate with
        try{
			// get the root naming context
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			// Use NamingContextExt instead of NamingContext. This is part of the Interoperable naming Service.
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
			// resolve the Object Reference in Naming
			String name = location;
			currentReplicaManager = RMHelper.narrow(ncRef.resolve_str(name));
			System.out.println(currentReplicaManager.sayHello());
		} 		
		catch (Exception e) {		
			e.printStackTrace(System.out);
			System.out.println("ERROR : " + e);
		}
    }
    
    public String getId() {
        return this.id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getLocation() {
        return this.location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public void clientLog(String input) {
        try {
            BufferedWriter outStream = new BufferedWriter(new FileWriter(this.clientLogName, true));
            outStream.newLine();
            outStream.write("[" + this.getDateTime() + "]" + this.id + " Log: " + input);
            outStream.close();
        }
        catch (IOException e) {
            System.out.println("An error occurred logging: " + input);
            e.printStackTrace();
        }
    }
    
    public String getDateTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }
    
    public String createRoom(int room_Number, String date, String list_Of_Time_Slots, String location) {
        String serverAnswer = "";
        currentReplicaManager.createRoom(room_Number, date, list_Of_Time_Slots, this.id, location);
        this.clientLog(serverAnswer);
        return serverAnswer;
    }
    
    public String deleteRoom(int room_Number, String date, String list_Of_Time_Slots, String location) {
        String serverAnswer = "";
        currentReplicaManager.deleteRoom(room_Number, date, list_Of_Time_Slots, this.id, location);
        this.clientLog(serverAnswer);
        return serverAnswer;
    }
    
    public String bookRoom(String campusName, int roomNumber, String date, String timeslot, String location) {
        String serverAnswer = "";
        currentReplicaManager.bookRoom(campusName, roomNumber, date, timeslot, this.id, location);
        this.clientLog(serverAnswer);
        return serverAnswer;
    }
    
    public String getAvailableTimeSlot(String date, String location) {
        String serverAnswer = "";
        currentReplicaManager.getAvailableTimeSlot(date, this.id, location);
        this.clientLog(serverAnswer);
        return serverAnswer;
    }
    
    public String cancelBooking(String bookingID, String location) {
        String serverAnswer = "";
        currentReplicaManager.cancelBooking(bookingID, this.id, location);
        this.clientLog(serverAnswer);
        return serverAnswer;
    }
    
    public String changeReservation(String bookingID, String selectedCampus, int selectedRoom, String selectedDate, String selectedTimeslot, String id, String location) {
        String serverAnswer = "";
        currentReplicaManager.changeReservation(bookingID, selectedCampus, selectedRoom, selectedTimeslot, selectedDate, this.id, location);
        this.clientLog(serverAnswer);
        return serverAnswer;
    }
    
    public static void main(final String[] args) {
        try {
            Scanner sc = new Scanner(System.in);
            System.out.println("\n\n\n====================  Login  ====================");
            System.out.print("Please enter your ID: ");
            String id = sc.next();
            ORB orb = ORB.init(args, null);
            Client client = new Client(id, id.substring(0, 3), orb);
            int permission = id.charAt(3);
            while (true) {
                if (65 == permission) {
                    System.out.println("\n\n\n====================  Admin Main Menu  ====================");
                    System.out.println("[1] Create Room");
                    System.out.println("[2] Delete Room");
                    System.out.println("[3] Exit");
                    System.out.print("Select one from the above options: ");
                    int option = sc.nextInt();
                    if (option == 1) {
                        System.out.print("Enter Room Number: ");
                        int selectedRoomNBCreate = sc.nextInt();
                        System.out.print("Enter Date dd-mm-yyyy: ");
                        String selectedDateCreate = sc.next();
                        String listEntry = "";
                        while (true) {
                            String loopExit = "Y";
                            System.out.print("Enter Time Slot: ");
                            String timeSelected = sc.next();
                            listEntry = String.valueOf(listEntry) + timeSelected + "," + "Not Booked" + "," + " " + ",";
                            System.out.print("Add another(Y/N)?: ");
                            loopExit = sc.next();
                            if (loopExit.equals("N")) {
                                break;
                            }
                            listEntry = String.valueOf(listEntry) + ".";
                        }
                        System.out.println(client.createRoom(selectedRoomNBCreate, selectedDateCreate, listEntry, client.getLocation()));
                    }
                    else if (option == 2) {
                        System.out.print("Enter Room Number: ");
                        int selectedRoomNBDelete = sc.nextInt();
                        System.out.print("Enter Date dd-mm-yyyy: ");
                        String selectedDateDelete = sc.next();
                        String listEntry = "";
                        while (true) {
                            String loopExit = "Y";
                            System.out.print("Enter Time Slot: ");
                            String timeSelected = sc.next();
                            listEntry = String.valueOf(listEntry) + timeSelected + "," + "Not Booked" + "," + " " + ",";
                            System.out.print("Add another(Y/N)?: ");
                            loopExit = sc.next();
                            if (loopExit.equals("N")) {
                                break;
                            }
                            listEntry = String.valueOf(listEntry) + ".";
                        }
                        System.out.println(client.deleteRoom(selectedRoomNBDelete, selectedDateDelete, listEntry, client.getLocation()));
                    }
                    else {
                        if (option != 3) {
                            continue;
                        }
                        System.out.print("\nGoodbye!\n");
                        sc.close();
                        System.exit(0);
                    }
                }
                else {
                    System.out.println("\n\n\n====================  " + client.getLocation() + " Student Main Menu  ====================");
                    System.out.println("[1] Book Room");
                    System.out.println("[2] Get Available Time Slots");
                    System.out.println("[3] Cancel Booking");
                    System.out.println("[4] Change Reservations");
                    System.out.println("[5] Exit");
                    System.out.print("Select one from the above options: ");
                    int option = sc.nextInt();
                    String selectedCampus = "";
                    int selectedRoom = 0;
                    String selectedDate = "";
                    String selectedTimeslot = "";
                    if (option == 1) {
                        System.out.println("In which campus would you like to book a room?");
                        selectedCampus = sc.next();
                        System.out.println("In which room would you like to book a room?");
                        selectedRoom = sc.nextInt();
                        System.out.println("For what day would you like to book a room? dd-mm-yyyy");
                        selectedDate = sc.next();
                        System.out.println("For what timeslot would you like to book a room?");
                        selectedTimeslot = sc.next();
                        System.out.println(client.bookRoom(selectedCampus, selectedRoom, selectedDate, selectedTimeslot, client.getLocation()));
                    }
                    else if (option == 2) {
                        System.out.println("For what day would you like to see the number of available time slots?");
                        selectedDate = sc.next();
                        System.out.println(client.getAvailableTimeSlot(selectedDate, client.getLocation()));
                    }
                    else if (option == 3) {
                        System.out.println("For what booking id would you like to cancel your booking?");
                        String bookingID = sc.next();
                        System.out.println(client.cancelBooking(bookingID, client.getLocation()));
                    }
                    else if (option == 4) {
                        System.out.println("For what booking id would you like to change your booking?");
                        String bookingID = sc.next();
                        System.out.println("In which campus would you like to book a room?");
                        selectedCampus = sc.next();
                        System.out.println("In which room would you like to book a room?");
                        selectedRoom = sc.nextInt();
                        System.out.println("For what day would you like to book a room? dd-mm-yyyy");
                        selectedDate = sc.next();
                        System.out.println("For what timeslot would you like to book a room?");
                        selectedTimeslot = sc.next();
                        System.out.println(client.changeReservation(bookingID, selectedCampus, selectedRoom, selectedDate, selectedTimeslot, client.getId(), client.getLocation()));
                    }
                    else {
                        if (option != 5) {
                            continue;
                        }
                        sc.close();
                        System.out.print("\nGoodbye!\n");
                        System.exit(0);
                    }
                }
            }
        }
        catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}