package server;
import RSAPP.*;
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

public class RSServant1 extends RSPOA{

	// Name of the RS.
	private String replicaServerName;
	private String parentName;
	// Name of the RS log file .
	private String replicaServerLogName;
	//An orb
	private ORB orb;
	// Create Database
	private HashMap<String, HashMap<Integer, List<String[]>>> dataBase;
	private int sequenceIntRS = 0;
	
	// Setter for the orb of this servant
	public void setORB(ORB orb_val) {
		orb = orb_val;
	}
	
	// Getter for the orb of this servant
	public ORB getORB() {
		return this.orb;
	}
	
	// Getter for the database
	public HashMap<String, HashMap<Integer, List<String[]>>> getDataBase() {
        return this.dataBase;
    }
    
	// Setter for the database
    public void setDataBase(HashMap<String, HashMap<Integer, List<String[]>>> dataBase) {
        this.dataBase = dataBase;
    }
	
	// Constructor initializes RM with its name and sets up the file for all logs and sets up other RM names.
	public RSServant1(String name) throws IOException {
        this.replicaServerName = name;
        this.replicaServerLogName = "ReplicaServerLogs\\" + this.replicaServerName + ".txt";
        this.dataBase = new HashMap<String, HashMap<Integer, List<String[]>>>();
        final File yourFile = new File(this.replicaServerLogName);
        yourFile.createNewFile();
    }
	
	@Override
	public String sayHello() {
		
		return "\nHello World From: " + this.replicaServerName + "\n" ;
		
	}
	public void handleError(String s) {
		this.dataBase = new HashMap<String, HashMap<Integer, List<String[]>>>();
		
		String[] calls = s.split("@");
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
	
	// List to a string
	public String listToString(List<String[]> list){
		String output = "";
		for (String[] element : list){
			output = output + " [ " + element[0] + " | " + element[1] + " | " + element[2] + " ] ";
		}
		return output;
	}

	@Override
	public String createRoomHere(int roomNumber, String date, String String_Of_Time_Slots, String id, String location) {
		if(roomNumber == -1) {
			this.handleError(date);
			return "restarting server";
		} else {
	        String replicaServerAnswer = "";
	        String[] loca = location.split("!"); 
	    	location = loca[0];
	    	String sequenceint = loca[1];
	    	int test = this.sequenceIntRS+1;
	    	if(this.sequenceIntRS+1 != Integer.parseInt(sequenceint)) {
	    		return "CREATE ROOM (FAILURE) SEQUENCER";
	    	}
	    	else {
	    		this.sequenceIntRS+=1;
	    	}
	
	        // Implement here
	        // String to proper list
	 		List<String[]> List_Of_Time_Slots = new ArrayList<String[]>();
	 		String[] stringArrays =  String_Of_Time_Slots.split("\\.");
	 		for(int i = 0; i<stringArrays.length; i++) {
	 			String[] temp = new String[]{stringArrays[i].split(",")[0], stringArrays[i].split(",")[1], stringArrays[i].split(",")[2]};
	 			List_Of_Time_Slots.add(temp);
	 		}
	 		String addedTS = "";
	
	     	if(this.getDataBase().containsKey(date)){
	     		if(this.getDataBase().get(date).containsKey(roomNumber)){				
	     			for (int i = 0; i<List_Of_Time_Slots.size(); i++) {
	 					int match = 0;
	     				for (int j = 0; j<this.getDataBase().get(date).get(roomNumber).size(); j++) {
	     					if(List_Of_Time_Slots.get(i)[0].equals(this.getDataBase().get(date).get(roomNumber).get(j)[0])) {
	     						match = 1;						
	     					}
	     					else {   													
	     					}
	     				} 
	 					if(match == 0){
	 						this.getDataBase().get(date).get(roomNumber).add(List_Of_Time_Slots.get(i));
	 						addedTS = addedTS + " [ " + List_Of_Time_Slots.get(i)[0] + " | " + List_Of_Time_Slots.get(i)[1] + " | " + List_Of_Time_Slots.get(i)[2] + " ] ";
	 					}   					
	     			}
	 				if(addedTS.equals("")){
	 					replicaServerAnswer = "CREATE ROOM (FAILURE)";
	 					this.replicaManagerLog(replicaServerAnswer);
	 			        return replicaServerAnswer;
	 				} 
	 				replicaServerAnswer = "CREATE ROOM (SUCCESS)";
	 				this.replicaManagerLog(replicaServerAnswer);
	 		        return replicaServerAnswer;
	     		}
	     		else {
	     			this.getDataBase().get(date).put(roomNumber, List_Of_Time_Slots);
	     			replicaServerAnswer = "CREATE ROOM (SUCCESS)";
	     			this.replicaManagerLog(replicaServerAnswer);
	    	        return replicaServerAnswer;
	     		}
	     	}
	     	else {
	     		HashMap<Integer, List<String[]>> tempHP = new HashMap<Integer, List<String[]>>();
	     		tempHP.put(roomNumber, List_Of_Time_Slots);
	     		this.getDataBase().put(date, tempHP);
	     		replicaServerAnswer = "CREATE ROOM (SUCCESS)";
	     		this.replicaManagerLog(replicaServerAnswer);
		        return replicaServerAnswer;
	     		
	     	}
			}
	}

	@Override
	public String deleteRoomHere(int roomNumber, String date, String String_Of_Time_Slots, String id, String location) {
        String replicaServerAnswer = "";
        String[] loca = location.split("!"); 
    	location = loca[0];
    	String sequenceint = loca[1];
    	
    	if(this.sequenceIntRS+1 != Integer.parseInt(sequenceint)) {
    		return "CREATE ROOM (FAILURE) SEQUENCER";
    	}
    	else {
    		sequenceIntRS++;
    	}
        // Implement here
        // String to proper list
 		List<String[]> List_Of_Time_Slots = new ArrayList<String[]>();
 		String[] stringArrays =  String_Of_Time_Slots.split("\\.");
 		for(int i = 0; i<stringArrays.length; i++) {
 			String[] temp = new String[]{stringArrays[i].split(",")[0], stringArrays[i].split(",")[1], stringArrays[i].split(",")[2]};
 			List_Of_Time_Slots.add(temp);
 		}
 		String removedTS = "";
     	if(this.getDataBase().containsKey(date)){
     		if(this.getDataBase().get(date).containsKey(roomNumber)) {
     			this.getDataBase().get(date).get(roomNumber);    			
     			for (int i = 0; i<List_Of_Time_Slots.size(); i++) {
 					for (int j = 0; j<this.getDataBase().get(date).get(roomNumber).size(); j++) {
 						if(List_Of_Time_Slots.get(i)[0].equals(this.getDataBase().get(date).get(roomNumber).get(j)[0])){
 							removedTS = removedTS + " [ " + List_Of_Time_Slots.get(i)[0] + " | " + List_Of_Time_Slots.get(i)[1] + " | " + List_Of_Time_Slots.get(i)[2] + " ] ";
 							this.getDataBase().get(date).get(roomNumber).remove(j);
 						}
 					}													
     			}
     		}
     		else {
     			replicaServerAnswer = "DELETE ROOM (FAILURE)";
     			this.replicaManagerLog(replicaServerAnswer);
     			return replicaServerAnswer;
     		}
     	}
     	else {
     		replicaServerAnswer = "DELETE ROOM (FAILURE)";
     		this.replicaManagerLog(replicaServerAnswer);
     		return replicaServerAnswer;
     	}
 		if(removedTS.equals("")){
 			replicaServerAnswer = "DELETE ROOM (FAILURE)";
 			this.replicaManagerLog(replicaServerAnswer);
 			return replicaServerAnswer;
 		} 	
 		replicaServerAnswer = "DELETE ROOM (SUCCESS)";
        this.replicaManagerLog(replicaServerAnswer);
        return replicaServerAnswer;
	}

	@Override
	public String bookRoomHere(String campusName, int roomNumber, String date, String timeslot, String id, String location) {
        String replicaServerAnswer = "";
        
    	String[] loca = location.split("!"); 
    	location = loca[0];
    	String sequenceint = loca[1];
    	
    	if(this.sequenceIntRS+1 != Integer.parseInt(sequenceint)) {
    		return "CREATE ROOM (FAILURE) SEQUENCER";
    	}
    	else {
    		sequenceIntRS++;
    	}
    
        // Implement here
        String bookingID = campusName + "|" + date+  "|" + roomNumber + "|"  + timeslot + "|" + id;
    	if (this.getDataBase().containsKey(date)){
			if(this.getDataBase().get(date).containsKey(roomNumber)){
				for (String[] element : this.getDataBase().get(date).get(roomNumber)) {
					if(element[0].equals(timeslot)) {
						if(!element[1].equals("Not Booked")) {
							this.replicaManagerLog("BOOK ROOM (FAILURE)");
							return replicaServerAnswer = "BOOK ROOM (FAILURE)";
						}
						else {
							element[1] = bookingID;
							element[2] = id;
							this.replicaManagerLog(bookingID);
							return replicaServerAnswer = bookingID;
						}
					}					
				}
				this.replicaManagerLog("BOOK ROOM (FAILURE)");
				return replicaServerAnswer = "BOOK ROOM (FAILURE)";
			}
			else {  
				this.replicaManagerLog("BOOK ROOM (FAILURE)");
				return replicaServerAnswer = "BOOK ROOM (FAILURE)";
			}
		}
		else { 
			this.replicaManagerLog("BOOK ROOM (FAILURE)");
			return replicaServerAnswer = "BOOK ROOM (FAILURE)";
		}    	
	}

	@Override
	public String getAvailableTimeSlotHere(String date, String id, String location) {
        String replicaServerAnswer = "";
        
        String[] loca = location.split("!"); 
    	location = loca[0];
    	String sequenceint = loca[1];
    	
    	if(this.sequenceIntRS+1 != Integer.parseInt(sequenceint)) {
    		return "CREATE ROOM (FAILURE) SEQUENCER";
    	}
    	else {
    		sequenceIntRS++;
    	}
        // Implement here
        int total = 0;
		if(this.getDataBase().get(date)!=null){
			Map<Integer, List<String[]>> temp = this.getDataBase().get(date);
			for (List<String[]> listElement : temp.values()) {
				for (String[] arrayElement : listElement){
					if(arrayElement[1].equals("Not Booked")){
						total += 1;
					}
				}
			}
			replicaServerAnswer = String.valueOf(total);
		}
		else{
			replicaServerAnswer = String.valueOf(0);
		}
        
        this.replicaManagerLog("Slots here: " + replicaServerAnswer);
        return replicaServerAnswer;
	}

	@Override
	public String cancelBookingHere(String bookingID, String id, String location) {
        String replicaServerAnswer = "";
        
        String[] loca = location.split("!"); 
    	location = loca[0];
    	String sequenceint = loca[1];
        
    	if(this.sequenceIntRS+1 != Integer.parseInt(sequenceint)) {
    		return "CREATE ROOM (FAILURE) SEQUENCER";
    	}
    	else {
    		sequenceIntRS++;
    	}
    	
        // Implement here
        String[] bookingIDArray = bookingID.split("\\|");
		String date = bookingIDArray[1];
		int roomNumber = Integer.parseInt(bookingIDArray[2]);
		String timeslot = bookingIDArray[3];
		String studentId = bookingIDArray[4];
		
		if (this.getDataBase().containsKey(date)){
			if(this.getDataBase().get(date).containsKey(roomNumber)){
				for (String[] element : this.getDataBase().get(date).get(roomNumber)) {
					if(element[0].equals(timeslot)) {
						if(!element[2].equals(studentId)) {
							replicaServerAnswer = "CANCEL BOOKING (FAILURE)";
						}
						else {
							element[1] = "Not Booked";
							element[2] = "";
							replicaServerAnswer = "CANCEL BOOKING (SUCCESS)";
							this.replicaManagerLog(replicaServerAnswer);
							return replicaServerAnswer;
						}
					}										
				}
				replicaServerAnswer = "CANCEL BOOKING (FAILURE)";
				this.replicaManagerLog(replicaServerAnswer);
				return replicaServerAnswer;
			}
			else {
				replicaServerAnswer = "CANCEL BOOKING (FAILURE)";
				this.replicaManagerLog(replicaServerAnswer);
				return replicaServerAnswer;
			}
		}
		else {
			replicaServerAnswer = "CANCEL BOOKING (FAILURE)";
			this.replicaManagerLog(replicaServerAnswer);
			return replicaServerAnswer;
		}
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}
}
