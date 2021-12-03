package server;
import RSAPP.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.lang.Object;
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
import java.util.Collection;
import java.util.HashMap;


public class RSServant2 extends RSPOA{
	// Name of the RS.
	private String replicaServerName;
	// Name of the RS log file .
	private String replicaServerLogName;
	//An orb
	private ORB orb;
	private HashMap<String, HashMap<String, RoomRecord>> rooms;
	private String campusName;
	// Setter for the orb of this servant
	public void setORB(ORB orb_val) {
		orb = orb_val;
	}
	private int sequenceIntRS = 0;
	// Getter for the orb of this servant
	public ORB getORB() {
		return this.orb;
	}
	public String getName() {
		return this.campusName;
	}
	public class RoomRecord {
		private String campus;
		private String date;
		private String roomNumber;
		private HashMap<String, String[]>  availableTimes;
		
		public RoomRecord(String campus, String roomNumber, String date, String[] times) {
			this.campus = campus;
			this.date = date;
			this.roomNumber = roomNumber;
			this.setAvailableTimess(new HashMap<String, String[]>());
			for(int i = 0; i < times.length; i++) {
				this.getAvailableTimess().put(times[i], new String[] {"None","None"});
			}
			
		}
		
		
		public String setBooked(String time, String studentId ) {
			System.out.println("booking");
			
			if (this.getAvailableTimess().get(time) == null||this.getAvailableTimess().get(time)[0] != "None") 
				return "BOOK ROOM (FAILURE)";
			else {
				String bookingId = (this.campus+'|' + this.date + '|' + roomNumber+'|'+time+'|'+studentId);
				String[] value = {studentId, bookingId};
				this.getAvailableTimess().put(time, value );
				return bookingId;
			}
		}
		public  int getAvailableTimes() {
			int x = 0;
			Collection times = this.getAvailableTimess().values();
			
			for( Object rr : times) {
				if( ((String[]) rr)[0].equals("None")) {
					x++;
					
				}
			}
			return x;
		}
		
		public String cancelBooking(String bookingId) {
			String time = bookingId.substring(13, 18);
			if (this.getAvailableTimess().get(time) == null||this.getAvailableTimess().get(time)[0].equals("None")) 
				return "CANCEL BOOKING (FALURE)";
			else {
				
				String[] value = {"None", "None"};
				this.getAvailableTimess().put(time, value);
				return "CANCEL BOOKING (SUCCESS)";
			}
			
		}
		public int addTimeSlots(String[] times) {
			int counter = 0; 
			for( int i = 0; i < times.length; i++) {
				if (getAvailableTimess().containsKey(times[i])) {
					continue;
				}
				else {
					getAvailableTimess().put(times[i], new String[] {"None", "None"});
					counter++;
				}
			}
			return counter;
		}
		
		public int removeTimeSlots(String[] times) {
			int counter = 0;
			for( int i = 0; i < times.length; i++) {
				if (getAvailableTimess().containsKey(times[i])) {
					counter++;
					getAvailableTimess().remove(times[i]);
				}
				
			}
			return counter; 
		}

		public HashMap<String, String[]> getAvailableTimess() {
			return availableTimes;
		}

		public void setAvailableTimess(HashMap<String, String[]> availableTimes) {
			this.availableTimes = availableTimes;
		}
		
		
		
		
	}

	// Constructor initializes RM with its name and sets up the file for all logs and sets up other RM names.
	public RSServant2(String name) throws IOException {
        this.replicaServerName = name;
        this.campusName = name.substring(3);
        this.replicaServerLogName = "ReplicaServerLogs\\" + this.replicaServerName + ".txt";
        final File yourFile = new File(this.replicaServerLogName);
        this.rooms = new HashMap<String, HashMap<String, RoomRecord>>();
        yourFile.createNewFile();
    }
	
	@Override
	public String sayHello() {

		return "\nHello World From: " + this.replicaServerName + "\n";
		
	}
	public void handleError(String s) {
		this.rooms = new HashMap<String, HashMap<String, RoomRecord>>();
		
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
	// Log method that takes a string and then inputs it into the RM's log file.
	public void replicaManagerLog(String input) {
        try {
            BufferedWriter outStream = new BufferedWriter(new FileWriter(this.replicaServerLogName, true));
            outStream.newLine();
            outStream.write("[" + this.getDateTime() + "]" + this.replicaServerName + " Log: " + input);
            outStream.close();
        }
        catch (IOException e) {
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

	@Override
	public String createRoomHere(int roomNumber, String date, String timeSlots, String id, String location) {
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
    	
        // Implement here
        String[] ts = timeSlots.split(",");
		if(rooms.get(date)==null) {
			RoomRecord rr = new RoomRecord(this.getName(),String.valueOf(roomNumber), date, ts);
			HashMap<String, RoomRecord> rrs = new HashMap<String, RoomRecord>();
			rrs.put(String.valueOf(roomNumber), rr);
			rooms.put(date, rrs);
		
		}
		else if(rooms.get(date).get(roomNumber)==null){
			HashMap<String, RoomRecord> roomsOnDay = rooms.get(date);
			RoomRecord rr = new RoomRecord(this.getName(),String.valueOf(roomNumber), date, ts);
			roomsOnDay.put(String.valueOf(roomNumber), rr);
			
			
		}
		else {
			RoomRecord rr = rooms.get(date).get(String.valueOf(roomNumber));
			int response = rr.addTimeSlots(ts);
			if(response == 0) {
				replicaServerAnswer = "CREATE ROOM (FAILURE)";
			} else {
				replicaServerAnswer = "CREATE ROOM (SUCCESS)";
			}
		}
        this.replicaManagerLog(replicaServerAnswer);
        return replicaServerAnswer;
	}

	@Override
	public String deleteRoomHere(int roomNumber, String date, String timeSlots, String id, String location) {
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
        String[] ts = timeSlots.split(",");
		// TODO Auto-generated method stub
		if(rooms.get(date)!=null&&rooms.get(date).get(String.valueOf(roomNumber))!= null) {
			RoomRecord rr = rooms.get(date).get(String.valueOf(roomNumber));
			int response = rr.removeTimeSlots(ts);
			if(response == 0) {
				replicaServerAnswer = "DELETE ROOM (FAILURE)";
			} else {
				replicaServerAnswer = "DELETE ROOM (SUCCESS)";
			}
		
		}
        this.replicaManagerLog(replicaServerAnswer);
        return replicaServerAnswer;
	}

	@Override
	public String bookRoomHere(String campusName, int roomNumber, String date, String timeslot, String id, String location) {
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
        HashMap<String, RoomRecord> roomRecords = rooms.get(date);
    
		if(roomRecords == null) {
			replicaServerAnswer = "BOOK ROOM (FAILURE)";
			this.replicaManagerLog(replicaServerAnswer);
	        return replicaServerAnswer;
		}
		RoomRecord rr = roomRecords.get(String.valueOf(roomNumber));
		if(rr==null) {
			replicaServerAnswer = "BOOK ROOM (FAILURE)";
			this.replicaManagerLog(replicaServerAnswer);
	        return replicaServerAnswer;
			
		}
		replicaServerAnswer = rr.setBooked(timeslot, id);
        this.replicaManagerLog(replicaServerAnswer);
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
        HashMap<String, RoomRecord> roomRecords = rooms.get(date);
		if(roomRecords == null)
			return "0";
		int counter = 0;
		
		Collection times = roomRecords.values();
		
		
		for( Object rr : times) {
			counter += ((RoomRecord) rr).getAvailableTimes();
		}
		this.replicaManagerLog(replicaServerAnswer);
		return String.valueOf(counter);
       
        
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
        HashMap<String, RoomRecord> roomRecords = rooms.get(bookingID.substring(4, 14));
		if(roomRecords == null) {
			
			replicaServerAnswer = "CANCEL BOOKING (FAILURE)";
			this.replicaManagerLog(replicaServerAnswer);
	        return replicaServerAnswer;
			
		} 
		RoomRecord rr = roomRecords.get(bookingID.substring(15,18));
		System.out.println(bookingID.substring(15,18));
		if( rr == null) {
			
			replicaServerAnswer = "CANCEL BOOKING (FAILURE)";
			this.replicaManagerLog(replicaServerAnswer);
	        return replicaServerAnswer;
		}
		this.replicaManagerLog(replicaServerAnswer);
		return rr.cancelBooking(bookingID);
       
        
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}
	


}
