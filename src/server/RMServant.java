package server;
import RMAPP.*;
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

/*
 *	Replica Manager Class: Layer of abstraction before communication with the replica servers that store the actual infromation and preform the actual
 *	Calculations and manipulation of Data. Acts as the server to all Front-end calls and some RM calls (calls originating from other RMs)
 *	Acts as a client to all all replica interations and some RM interactions (calling other RMs) .
 */
public class RMServant extends RMPOA {
	// Name of the RM.
	private String replicaName;
	// Name of the RM log file .
	private String replicaLogName;
	// Other two RM that this RM will communicate with. Their Name and the instance.
	private String RMOneName;
	private String RMTwoName;
	private RMServant RMOne;
	private RMServant RMTwo;
	private ORB orb;
	
	// Setter for the orb of this servant
	public void setORB(ORB orb_val) {
		orb = orb_val;
	}
	
	// Getter for the orb of this servant
	public ORB getORB() {
		return this.orb;
	}
	
	// Constructor initializes RM with its name and sets up the file for all logs and sets up other RM names.
	public RMServant(String name, String RMOneName, String RMTwoName) throws IOException {
        this.replicaName = name;
        this.RMOneName = RMOneName;
        this.RMTwoName = RMTwoName;
        this.replicaLogName = "ReplicaLogs\\" + this.replicaName + ".txt";
        final File yourFile = new File(this.replicaLogName);
        yourFile.createNewFile();
    }
	
	// Basic say hello method to be used for testing.
	public String sayHello() {
		// TODO Auto-generated method stub
		return "\nHello World From: " + this.replicaName + "\n";
	}
	
	// Log method that takes a string and then inputs it into the RM's log file.
	public void replicaLog(String input) {
        try {
            BufferedWriter outStream = new BufferedWriter(new FileWriter(this.replicaLogName, true));
            outStream.newLine();
            outStream.write("[" + this.getDateTime() + "]" + this.replicaName + " Log: " + input);
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
    
	// Create Room method. Create a room in all replicas. 
    @Override
	public String createRoom(int roomNumber, String date, String List_Of_Time_Slots, String id, String location)
    {
        String replicaAnswer = "";
        // Implement here
        this.replicaLog(replicaAnswer);
        return replicaAnswer;
    }
    
    // Delete Room method. Delete a room in all replicas. 
    @Override
    public String deleteRoom(int room_Number, String date, String list_Of_Time_Slots, String id, String location) {
        String replicaAnswer = "";
        // Implement here
        this.replicaLog(replicaAnswer);
        return replicaAnswer;
    }
    
   /* 
    * Book Room method. Book a room at the appropriate location. If the locaiton is this RM then 
    * Book a room in all of this RM's replicas. If the booking is in another RM then transfer the call
    * to that RM.
    */
    @Override
    public String bookRoom(String campusName, int roomNumber, String date, String timeslot, String id, String location) {
        String replicaAnswer = "";
        // Implement here
        this.replicaLog(replicaAnswer);
        return replicaAnswer;
    }
    
    /* 
     * Get all vailable time slot method. Get all available time slots in this RM's replicas and 
     * call all other RMs to do the same with theirs.
     */
    @Override
    public String getAvailableTimeSlot(String date, String id, String location) {
        String replicaAnswer = "";
        // Implement here
        this.replicaLog(replicaAnswer);
        return replicaAnswer;
    }

    /* 
     * Cancel Room method. Cancel a room at the appropriate location. If the locaiton is this RM then 
     * Cancel a room in all of this RM's replicas. If the replication is in another RM then transfer the call
     * to that RM.
     */
    @Override
    public String cancelBooking(String bookingID, String id, String location) {
        String replicaAnswer = "";
        // Implement here
        this.replicaLog(replicaAnswer);
        return replicaAnswer;
    }
    
    /* 
     * Change Reservation method. Cancel a room at the appropriate location. If the locaiton is this RM then 
     * Cancel a room in all of this RM's replicas. If the replication is in another RM then transfer the call
     * to that RM. Then Book a room at the appropriate location. If the locaiton is this RM then Book a room in 
     * all of this RM's replicas. If the booking is in another RM then transfer the call
	 * to that RM.
     */
    @Override
    public String changeReservation(String bookingID, String selectedCampus, int selectedRoom, String selectedDate, String selectedTimeslot, String id, String location) {
        String replicaAnswer = "";
        // Implement here
        this.replicaLog(replicaAnswer);
        return replicaAnswer;
    }

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}
	

}