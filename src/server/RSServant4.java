package server;

import RSAPP.*;
import server.RSServant2.RoomRecord;

import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.*;
import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POA;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RSServant4 extends RSPOA {

	// Name of the RS.
	private String replicaServerName;
	// Name of the RS log file .
	private String replicaServerLogName;
	// An orb
	private ORB orb;

	// Setter for the orb of this servant
	public void setORB(ORB orb_val) {
		orb = orb_val;
	}
	private int sequenceIntRS = 0;
	// Getter for the orb of this servant
	public ORB getORB() {
		return this.orb;
	}

	// The database
	ConcurrentHashMap<Integer, RoomObject> roomRecords = new ConcurrentHashMap<Integer, RoomObject>();

	// Constructor initializes RM with its name and sets up the file for all logs
	// and sets up other RM names.
	public RSServant4(String name) throws IOException {
		this.replicaServerName = name;
		this.replicaServerLogName = "ReplicaServerLogs\\" + this.replicaServerName + ".txt";
		final File yourFile = new File(this.replicaServerLogName);
		yourFile.createNewFile();
	}

	@Override
	public String sayHello() {

		return "\nHello World From: " + this.replicaServerName + "\n";

	}

	// Log method that takes a string and then inputs it into the RM's log file.
	public void replicaManagerLog(String input) {
		try {
			BufferedWriter outStream = new BufferedWriter(new FileWriter(this.replicaServerLogName, true));
			outStream.newLine();
			outStream.write("[" + this.getDateTime() + "]" + this.replicaServerName + " Log: " + input);
			outStream.close();
		} catch (IOException e) {
			System.out.println("An error occurred logging: " + input);
			e.printStackTrace();
		}
	}

	// Returns the current time. Used in the logging process.
	public String getDateTime() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		return dtf.format(now);
	}
	
	public void handleError(String s) {
		this.roomRecords = new ConcurrentHashMap<Integer, RoomObject>();
		
		String[] calls = s.split("!");
		this.sequenceIntRS = 0;
		for ( int i = 0; i< calls.length;i++) {
			String[] params = calls[i].split(";");
			if(params[0].equals("createRoom")) {
				this.createRoomHere(Integer.parseInt(params[1]), params[2], params[3], params[4], params[5]);
			} else if(params[0].equals("deleteRoom")) {
				this.deleteRoomHere(Integer.parseInt(params[1]), params[2], params[3], params[4], params[5]);
			} else if(params[0].equals("bookRoom")) {
				this.bookRoomHere(params[1], Integer.parseInt(params[2]), params[3], params[4], params[5], params[6]);
			} else if(params[0].equals("getAvailableTimeSlot")) {
				this.getAvailableTimeSlotHere(params[1], params[2], params[3]);
			} else if(params[0].equals("cancelBooking")){
				this.cancelBookingHere(params[1], params[2], params[3]);
			}
		}
	}

	@Override
	public String createRoomHere(int roomNumber, String date, String String_Of_Time_Slots, String id, String location) {
		if (roomNumber == -1) {
			this.handleError(date);
			return "restarting server";
		}
		String replicaServerAnswer = "";
		String[] loca = location.split("!"); 
    	location = loca[0];
    	String sequenceint = loca[1];
    	
    	if(this.sequenceIntRS-1 != Integer.parseInt(sequenceint)) {
    		return "CREATE ROOM (FAILURE) SEQUENCER";
    	}
    	else {
    		sequenceIntRS++;
    	}
    	
		// String to proper list
		List<String[]> List_Of_Time_Slots = new ArrayList<String[]>();
		String[] stringArrays = String_Of_Time_Slots.split("\\.");
		for (int i = 0; i < stringArrays.length; i++) {
			String[] temp = new String[] { stringArrays[i].split(",")[0], stringArrays[i].split(",")[1],
					stringArrays[i].split(",")[2] };
			List_Of_Time_Slots.add(temp);
		}
		String addedTS = "";

		// Checks to see if the all the keys exists, and depending on where it ends up
		// being null it will create either just
		// timeslots, days or even rooms
		for (int i = 0; i < List_Of_Time_Slots.size(); i++) {
			for (int j = 0; i < List_Of_Time_Slots.get(i).length; j++) {
				if (roomRecords.containsKey(roomNumber)) {
					if (roomRecords.get(roomNumber).Dates.containsKey(date)) {
						if (roomRecords.get(roomNumber).Dates.get(date)
								.contains(List_Of_Time_Slots.get(i)[j]) == false) {
							roomRecords.get(roomNumber).Dates.get(date).put(List_Of_Time_Slots.get(i)[j], "Not Booked");
							addedTS = addedTS + "[" + List_Of_Time_Slots.get(i)[j] + "]";
						}
					} else {

						roomRecords.get(roomNumber).Dates.put(date, new ConcurrentHashMap<String, String>());
						if (roomRecords.get(roomNumber).Dates.get(date)
								.containsKey(List_Of_Time_Slots.get(i)[j]) == false) {
							roomRecords.get(roomNumber).Dates.get(date).put(List_Of_Time_Slots.get(i)[j], "Not Booked");
							addedTS = addedTS + "[" + List_Of_Time_Slots.get(i)[j] + "]";
						}
					}
				} else {
					roomRecords.put(roomNumber, new RoomObject(roomNumber));
					roomRecords.get(roomNumber).Dates.put(date, new ConcurrentHashMap<String, String>());
					if (roomRecords.get(roomNumber).Dates.get(date).containsKey(List_Of_Time_Slots.get(i)) == false) {
						roomRecords.get(roomNumber).Dates.get(date).put(List_Of_Time_Slots.get(i)[j], "Not Booked");
						addedTS = addedTS + "[" + List_Of_Time_Slots.get(i)[j] + "]";
					}
				}
			}
		}
		if (addedTS.equals("")) {
			replicaServerAnswer = "CREATE ROOM (FAILURE)";
		} else {
			replicaServerAnswer = "CREATE ROOM (SUCCESS)";
		}

		this.replicaManagerLog(replicaServerAnswer);
		return replicaServerAnswer;
	}

	@Override
	public String deleteRoomHere(int roomNumber, String date, String String_Of_Time_Slots, String id, String location) {
		String replicaServerAnswer = "";
		String[] loca = location.split("!"); 
    	location = loca[0];
    	String sequenceint = loca[1];
    	
    	if(this.sequenceIntRS-1 != Integer.parseInt(sequenceint)) {
    		return "CREATE ROOM (FAILURE) SEQUENCER";
    	}
    	else {
    		sequenceIntRS++;
    	}
    	
		// Implement here
		List<String[]> List_Of_Time_Slots = new ArrayList<String[]>();
		String[] stringArrays = String_Of_Time_Slots.split("\\.");
		for (int i = 0; i < stringArrays.length; i++) {
			String[] temp = new String[] { stringArrays[i].split(",")[0], stringArrays[i].split(",")[1],
					stringArrays[i].split(",")[2] };
			List_Of_Time_Slots.add(temp);
		}
		String removedTS = "";
		for (int i = 0; i < List_Of_Time_Slots.size(); i++) {
			for (int j = 0; i < List_Of_Time_Slots.get(i).length; j++) {
				if (roomRecords.containsKey(roomNumber)) {
					if (roomRecords.get(roomNumber).Dates.containsKey(date)) {
						if (roomRecords.get(roomNumber).Dates.get(date)
								.containsKey(List_Of_Time_Slots.get(i)[j]) == true) {
							roomRecords.get(roomNumber).Dates.get(date).remove(List_Of_Time_Slots.get(i)[j]);
							removedTS = removedTS + "[" + List_Of_Time_Slots.get(i)[j] + "]";
						}
					}
				}
			}
		}
		if (removedTS.equals("")) {
			replicaServerAnswer = "DELETE ROOM (FAILURE)";
		} else {
			replicaServerAnswer = "DELETE ROOM (SUCCESS)";
		}

		this.replicaManagerLog(replicaServerAnswer);
		return replicaServerAnswer;
	}

	@Override
	public String bookRoomHere(String campusName, int roomNumber, String date, String timeslot, String id,
			String location) {
		String replicaServerAnswer = "";
		String[] loca = location.split("!"); 
    	location = loca[0];
    	String sequenceint = loca[1];
    	
    	if(this.sequenceIntRS-1 != Integer.parseInt(sequenceint)) {
    		return "CREATE ROOM (FAILURE) SEQUENCER";
    	}
    	else {
    		sequenceIntRS++;
    	}
    	
		// Implement here
		String bookingID = campusName + "|" + date+  "|" + roomNumber + "|"  + timeslot + "|" + id;
		
		//Checks to see how many rooms the student has booked
		if(roomRecords.containsKey(roomNumber)) {
			if(roomRecords.get(roomNumber).Dates.containsKey(date)) {
				if ((roomRecords.get(roomNumber).Dates.get(date).containsKey(timeslot))) {
					//Checks if the room has no one booking it
					if((roomRecords.get(roomNumber).Dates.get(date).get(timeslot).equals("Not Booked"))){
						roomRecords.get(roomNumber).Dates.get(date).put(timeslot, id);
						replicaServerAnswer = bookingID;
					} else {
						replicaServerAnswer = "BOOK ROOM (FAILURE)";
					}
				} else {
					replicaServerAnswer = "BOOK ROOM (FAILURE)";
				}
			} else {
				replicaServerAnswer = "BOOK ROOM (FAILURE)";
			}
		} else {
			replicaServerAnswer = "BOOK ROOM (FAILURE)";
		}
			
		this.replicaManagerLog("BOOK ROOM (SUCCESS)"+ replicaServerAnswer);
		return replicaServerAnswer;
	}

	@Override
	public String getAvailableTimeSlotHere(String date, String id, String location) {
		String replicaServerAnswer = "";
		String[] loca = location.split("!"); 
    	location = loca[0];
    	String sequenceint = loca[1];
    	
    	if(this.sequenceIntRS-1 != Integer.parseInt(sequenceint)) {
    		return "CREATE ROOM (FAILURE) SEQUENCER";
    	}
    	else {
    		sequenceIntRS++;
    	}
    	
		// Implement here
		int availableSlots = 0;
		for (int roomId : roomRecords.keySet()) {
				for (String timeSlot : roomRecords.get(roomId).Dates.get(date).keySet()) {
					if(roomRecords.get(roomId).Dates.get(date).get(timeSlot).equals("Not Booked")) {
						availableSlots = availableSlots + 1;
					}
				}
			
		}
		replicaServerAnswer = String.valueOf(availableSlots);
		this.replicaManagerLog("Slots here: " + replicaServerAnswer);
		return replicaServerAnswer;
	}

	@Override
	public String cancelBookingHere(String bookingID, String id, String location) {
		String replicaServerAnswer = "";
		String[] loca = location.split("!"); 
    	location = loca[0];
    	String sequenceint = loca[1];
    	
    	if(this.sequenceIntRS-1 != Integer.parseInt(sequenceint)) {
    		return "CREATE ROOM (FAILURE) SEQUENCER";
    	}
    	else {
    		sequenceIntRS++;
    	}
    	
		// Implement here
		String[] bookingIDArray = bookingID.split("\\|");
		
		int roomID = Integer.parseInt(bookingIDArray[2]);
		String date = bookingIDArray[1];
		String timeslot = bookingIDArray[3];
		String studentID = bookingIDArray[4];
		if(roomRecords.containsKey(roomID)) {
			if(roomRecords.get(roomID).Dates.containsKey(date)) {
				if(roomRecords.get(roomID).Dates.get(date).containsKey(timeslot)) {
					if(roomRecords.get(roomID).Dates.get(date).get(timeslot).equals(studentID)) {
						roomRecords.get(roomID).Dates.get(date).put(timeslot, "Not Booked");
						replicaServerAnswer = "CANCEL BOOKING (SUCCESS)";
					} else {
						replicaServerAnswer = "CANCEL BOOKING (FAILURE)";
					}
				} else {
					replicaServerAnswer = "CANCEL BOOKING (FAILURE)";
				}
			} else {
				replicaServerAnswer = "CANCEL BOOKING (FAILURE)";
			}
		}

		this.replicaManagerLog(replicaServerAnswer);
		return replicaServerAnswer;
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub

	}
}
