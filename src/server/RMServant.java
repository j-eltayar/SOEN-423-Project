package server;
import RMAPP.*;
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
import java.util.Optional;

/*
 *	Replica Manager Class: Layer of abstraction before communication with the replica servers that store the actual infromation and preform the actual
 *	Calculations and manipulation of Data. Acts as the server to all Front-end calls and some RM calls (calls originating from other RMs)
 *	Acts as a client to all all replica interations and some RM interactions (calling other RMs) .
 */
public class RMServant extends RMPOA {
	// Name of the RM.
	private String replicaManagerName;
	// Name of the RM log file .
	private String replicaManagerLogName;
	// Other two RM that this RM will communicate with. Their Name and the instance.
	private String RMOneName;
	private String RMTwoName;
	private RM RMOne;
	private RM RMTwo;
	// Four replica servers managed by this RM
	private String RSOneName;
	private String RSTwoName;
	private String RSThreeName;
	private String RSFourName;
	private RS RSOne;
	private RS RSTwo;
	private RS RSThree;
	private RS RSFour;
	// An orb
	private ORB orb;
	int sequenceNUM;
	private String methodCalls;
	private long worstTime = 5000;
	private int[] replicaErrorCount = {0,0,0,0};

	// Setter for the orb of this servant
	public void setORB(ORB orb_val) {
		orb = orb_val;
	}
	
	// Getter for the orb of this servant
	public ORB getORB() {
		return this.orb;
	}
	public void addMethodCall(String s) {
		methodCalls += s+"!";
	}
	public static String removeLastCharOptional(String s) {
	    return Optional.ofNullable(s)
	      .filter(str -> str.length() != 0)
	      .map(str -> str.substring(0, str.length() - 1))
	      .orElse(s);
	}
	 
	public void handleError(RS rs) {
		String s = "f";
		rs.createRoomHere(-1, methodCalls,s,s,s);
	}
	
	public String setRMServers() {
		try {
			// get the root naming context
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			// Use NamingContextExt instead of NamingContext. This is part of the Interoperable naming Service.
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
			// resolve the Object Reference in Naming
			RMOne = RMHelper.narrow(ncRef.resolve_str(RMOneName));
			RMTwo = RMHelper.narrow(ncRef.resolve_str(RMTwoName));
		} 		
		catch (Exception e) {		
				
			e.printStackTrace(System.out);
			System.out.println("ERROR : " + e);
			return "Error FLAMES";
		}
		
		return "GREAT SUCCESS!!";
	}
	
	public String setRSServers() {
		try {
			// get the root naming context
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			// Use NamingContextExt instead of NamingContext. This is part of the Interoperable naming Service.
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
			// resolve the Object Reference in Naming
			RSOne = RSHelper.narrow(ncRef.resolve_str(RSOneName));
			RSTwo = RSHelper.narrow(ncRef.resolve_str(RSTwoName));
			RSThree = RSHelper.narrow(ncRef.resolve_str(RSThreeName));
			RSFour = RSHelper.narrow(ncRef.resolve_str(RSFourName));
		} 		
		catch (Exception e) {		
				
			e.printStackTrace(System.out);
			System.out.println("ERROR : " + e);
			return "Error FLAMES";
		}
		
		return "GREAT SUCCESS!!";
	}
	
	// Constructor initializes RM with its name and sets up the file for all logs and sets up other RM names.
	// Also initalizes the replica names for the four replica servers
	public RMServant(String name, String RMOneName, String RMTwoName, String RSOneName, String RSTwoName, String RSThreeName, String RSFourName) throws IOException {
        this.replicaManagerName = name;
        this.RMOneName = RMOneName;
        this.RMTwoName = RMTwoName;
        this.RSOneName = RSOneName;
        this.RSTwoName = RSTwoName;
        this.RSThreeName = RSThreeName;
        this.RSFourName = RSFourName;
        this.replicaManagerLogName = "ReplicaManagerLogs\\" + this.replicaManagerName + ".txt";
        final File yourFile = new File(this.replicaManagerLogName);
        yourFile.createNewFile();
    }
	
	// Basic say hello method to be used for testing.
	@Override
	public String sayHello() {
		
		if (this.replicaManagerName == "DVL")
		return RMOne.sayHello() + RMTwo.sayHello() + "\nHello World From: " + this.replicaManagerName + "\n Servants: "+RSOne.sayHello()+" "+RSTwo.sayHello()+" "+RSThree.sayHello()+" "+RSFour.sayHello();
		else {
			return "\nHello World From: " + this.replicaManagerName + "\n Servants: "+RSOne.sayHello()+" "+RSTwo.sayHello()+" "+RSThree.sayHello()+" "+RSFour.sayHello();
		}
	}
	
	// Log method that takes a string and then inputs it into the RM's log file.
	public void replicaManagerLog(String input) {
        try {
            BufferedWriter outStream = new BufferedWriter(new FileWriter(this.replicaManagerLogName, true));
            outStream.newLine();
            outStream.write("[" + this.getDateTime() + "]" + this.replicaManagerName + " Log: " + input);
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
        String replicaManagerAnswer = "";
        sequenceNUM++;
        addMethodCall("createRoom;"+roomNumber+";"+date+";"+List_Of_Time_Slots+";"+id+";"+location);
        long start = System.currentTimeMillis();
    	long end = start + worstTime*2;
    	String RSOneRes = null, RSTwoRes= null , RSThreeRes= null, RSFourRes = null;
    	
    	while (System.currentTimeMillis() < end) {
    		if(RSOneRes==null){
    			RSOneRes = RSOne.createRoomHere(roomNumber, date, List_Of_Time_Slots, id, location+"!"+sequenceNUM);
    		}
    		if(RSTwoRes == null){
    			RSTwoRes = RSTwo.createRoomHere(roomNumber, date, List_Of_Time_Slots, id, location+"!"+sequenceNUM);
    		}
    		if(RSThreeRes == null) {
    			RSThreeRes = RSThree.createRoomHere(roomNumber, date, List_Of_Time_Slots, id, location+"!"+sequenceNUM);
    		}
    		if(RSFourRes == null){
    			RSFourRes =  RSFour.createRoomHere(roomNumber, date, List_Of_Time_Slots, id, location+"!"+sequenceNUM);
    		}
            if(RSOneRes!=null && RSTwoRes !=null && RSThreeRes!=null && RSFourRes != null) {
            	if(this.worstTime<(System.currentTimeMillis() - start)) {
            		this.worstTime = System.currentTimeMillis() - start;
            	}
            	break;
            }
    	}
    	String[] answerArray = {RSOneRes, RSTwoRes, RSThreeRes, RSFourRes};
    	for(int i = 0; i<answerArray.length;i++) {
    		if(answerArray[i]!=null) {
    			continue;
    		}
    		else {
    			replicaErrorCount[i]++;
    		}
    	}

        replicaManagerAnswer = validateResponseString(RSOneRes, RSTwoRes, RSThreeRes, RSFourRes);
        this.replicaManagerLog(replicaManagerAnswer);
        return replicaManagerAnswer;
    }
    
    // Delete Room method. Delete a room in all replicas. 
    @Override
    public String deleteRoom(int room_Number, String date, String list_Of_Time_Slots, String id, String location) {
        String replicaManagerAnswer = "";
        
        sequenceNUM++;
        addMethodCall("deleteRoom"+";"+room_Number+";"+date+";"+list_Of_Time_Slots+";"+id+";"+location);
        long start = System.currentTimeMillis();
    	long end = start + worstTime*2;
    	String RSOneRes = null, RSTwoRes= null , RSThreeRes= null, RSFourRes = null;
    	
    	while (System.currentTimeMillis() < end) {
    		if(RSOneRes==null){
    			RSOneRes = RSOne.deleteRoomHere(room_Number, date, list_Of_Time_Slots, id, location+"!"+sequenceNUM);        		}
    		if(RSTwoRes == null){
                RSTwoRes = RSTwo.deleteRoomHere(room_Number, date, list_Of_Time_Slots, id, location+"!"+sequenceNUM);
    		}
    		if(RSThreeRes == null) {
                RSThreeRes = RSThree.deleteRoomHere(room_Number, date, list_Of_Time_Slots, id, location+"!"+sequenceNUM);
    		}
    		if(RSFourRes == null){
                RSFourRes = RSFour.deleteRoomHere(room_Number, date, list_Of_Time_Slots, id, location+"!"+sequenceNUM);
    		}
            if(RSOneRes!=null && RSTwoRes !=null && RSThreeRes!=null && RSFourRes != null) {
            	if(this.worstTime<(System.currentTimeMillis() - start)) {
            		this.worstTime = System.currentTimeMillis() - start;
            	}
            	break;
            }
    	}
    	String[] answerArray = {RSOneRes, RSTwoRes, RSThreeRes, RSFourRes};
    	for(int i = 0; i<answerArray.length;i++) {
    		if(answerArray[i]!=null) {
    			continue;
    		}
    		else {
    			replicaErrorCount[i]++;
    		}
    	}
    	
        replicaManagerAnswer = validateResponseString(RSOneRes, RSTwoRes, RSThreeRes, RSFourRes);
        this.replicaManagerLog(replicaManagerAnswer);
        return replicaManagerAnswer;
    }
    
   /* 
    * Book Room method. Book a room at the appropriate location. If the locaiton is this RM then 
    * Book a room in all of this RM's replicas. If the booking is in another RM then transfer the call
    * to that RM.
    */
    @Override
    public String bookRoom(String campusName, int roomNumber, String date, String timeslot, String id, String location) {
        String replicaManagerAnswer = "";
        if(location.contentEquals(replicaManagerName)) {
        	
        	sequenceNUM++;
        	addMethodCall("bookRoom"+";"+campusName+";"+roomNumber+";"+date+";"+timeslot+";"+id+";"+location);
        	long start = System.currentTimeMillis();
        	long end = start + worstTime*2;
        	String RSOneRes = null, RSTwoRes= null , RSThreeRes= null, RSFourRes = null;
        	
        	while (System.currentTimeMillis() < end) {
                if(RSOneRes==null){
            		RSOneRes = RSOne.bookRoomHere(campusName, roomNumber, date, timeslot, id, location+"!"+sequenceNUM);
                }
        		if(RSTwoRes == null){
                    RSTwoRes = RSTwo.bookRoomHere(campusName, roomNumber, date, timeslot, id, location+"!"+sequenceNUM);
        		}
        		if(RSThreeRes == null) {
                    RSThreeRes = RSThree.bookRoomHere(campusName, roomNumber, date, timeslot, id, location+"!"+sequenceNUM);
        		}
        		if(RSFourRes == null){
                    RSFourRes = RSFour.bookRoomHere(campusName, roomNumber, date, timeslot, id, location+"!"+sequenceNUM);
        		}
                if(RSOneRes!=null && RSTwoRes !=null && RSThreeRes!=null && RSFourRes != null) {
                	if(this.worstTime<(System.currentTimeMillis() - start)) {
                		this.worstTime = System.currentTimeMillis() - start;
                	}
                	break;
                }
        	}
        	String[] answerArray = {RSOneRes, RSTwoRes, RSThreeRes, RSFourRes};
        	for(int i = 0; i<answerArray.length;i++) {
        		if(answerArray[i]!=null) {
        			continue;
        		}
        		else {
        			replicaErrorCount[i]++;
        		}
        	}

        	replicaManagerAnswer = validateResponseString(RSOneRes, RSTwoRes, RSThreeRes, RSFourRes);
        }
        else if(location.contentEquals(RMOneName)) {
        	replicaManagerAnswer = RMOne.bookRoom(campusName, roomNumber, date, timeslot, id, location);
        }
        else {
        	replicaManagerAnswer = RMTwo.bookRoom(campusName, roomNumber, date, timeslot, id, location);
        }
        this.replicaManagerLog(replicaManagerAnswer);
        return replicaManagerAnswer;
    }
    
    /* 
     * Get all vailable time slot method. Get all available time slots in this RM's replicas and 
     * call all other RMs to do the same with theirs.
     */
    @Override
    public String getAvailableTimeSlot(String date, String id, String location) {
        String replicaManagerAnswer = "";
        String answerHere = "";
        
        sequenceNUM++;
        addMethodCall("getAvailableTimeSlot"+";"+date+";"+location);
        long start = System.currentTimeMillis();
    	long end = start + worstTime*2;
    	String RSOneRes = null, RSTwoRes= null , RSThreeRes= null, RSFourRes = null;
    	
    	while (System.currentTimeMillis() < end) {
	            
		    if(RSOneRes==null){
	    		RSOneRes = RSOne.getAvailableTimeSlotHere(date, id, location+"!"+sequenceNUM);
		    }
	 		if(RSTwoRes == null){
			    RSTwoRes = RSTwo.getAvailableTimeSlotHere(date, id, location+"!"+sequenceNUM);
	 		}
	 		if(RSThreeRes == null) {
			    RSThreeRes = RSThree.getAvailableTimeSlotHere(date, id, location+"!"+sequenceNUM);
	 		}
	 		if(RSFourRes == null){
			    RSFourRes = RSFour.getAvailableTimeSlotHere(date, id, location+"!"+sequenceNUM);
	 		}
	        if(RSOneRes!=null && RSTwoRes !=null && RSThreeRes!=null && RSFourRes != null) {
	        	if(this.worstTime<(System.currentTimeMillis() - start)) {
	             		this.worstTime = System.currentTimeMillis() - start;
	            }
	            break;
	        }
    	}
    	String[] answerArray = {RSOneRes, RSTwoRes, RSThreeRes, RSFourRes};
    	for(int i = 0; i<answerArray.length;i++) {
    		if(answerArray[i]!=null) {
    			continue;
    		}
    		else {
    			replicaErrorCount[i]++;
    		}
    	}
        
        answerHere = validateResponseString(RSOneRes, RSTwoRes, RSThreeRes, RSFourRes);
        
        if(location.contentEquals(replicaManagerName)) {
        	replicaManagerAnswer = replicaManagerName + ":" + answerHere + "|" + RMOne.getAvailableTimeSlot(date, id, location) + ":" + answerHere + "|" + RMTwo.getAvailableTimeSlot(date, id, location);
        }
        else{
        	replicaManagerAnswer = answerHere;
        }
        this.replicaManagerLog(replicaManagerAnswer);
        return replicaManagerAnswer;
    }

    /* 
     * Cancel Room method. Cancel a room at the appropriate location. If the locaiton is this RM then 
     * Cancel a room in all of this RM's replicas. If the replication is in another RM then transfer the call
     * to that RM.
     */
    @Override
    public String cancelBooking(String bookingID, String id, String location) {
        String replicaManagerAnswer = "";
        
        if(location.contentEquals(replicaManagerName)) {
        	
        	sequenceNUM++;
            addMethodCall("cancelBooking;"+bookingID+";"+id+";"+location);
            long start = System.currentTimeMillis();
        	long end = start + worstTime*2;
        	String RSOneRes = null, RSTwoRes= null , RSThreeRes= null, RSFourRes = null;
        	
        	while (System.currentTimeMillis() < end) {
    	            
                if(RSOneRes==null){
            		RSOneRes = RSOne.cancelBookingHere(bookingID, id, location+"!"+sequenceNUM);         
    		    }
    	 		if(RSTwoRes == null){
                    RSTwoRes = RSTwo.cancelBookingHere(bookingID, id, location+"!"+sequenceNUM);          
    	 		}
    	 		if(RSThreeRes == null) {
                    RSThreeRes = RSThree.cancelBookingHere(bookingID, id, location+"!"+sequenceNUM);
    	 		}
    	 		if(RSFourRes == null){
                    RSFourRes = RSFour.cancelBookingHere(bookingID, id, location+"!"+sequenceNUM);
    	 		}
    	        if(RSOneRes!=null && RSTwoRes !=null && RSThreeRes!=null && RSFourRes != null) {
    	        	if(this.worstTime<(System.currentTimeMillis() - start)) {
    	             		this.worstTime = System.currentTimeMillis() - start;
    	            }
    	            break;
    	        }
        	}
        	String[] answerArray = {RSOneRes, RSTwoRes, RSThreeRes, RSFourRes};
        	for(int i = 0; i<answerArray.length;i++) {
        		if(answerArray[i]!=null) {
        			continue;
        		}
        		else {
        			replicaErrorCount[i]++;
        		}
        	}
            
            
            
        	replicaManagerAnswer = validateResponseString(RSOneRes, RSTwoRes, RSThreeRes, RSFourRes);
        }
        else if(location.contentEquals(RMOneName)) {
        	replicaManagerAnswer = RMOne.cancelBooking(bookingID, id, location);
        }
        else {
        	replicaManagerAnswer = RMTwo.cancelBooking(bookingID, id, location);
        }
        this.replicaManagerLog(replicaManagerAnswer);
        return replicaManagerAnswer;
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
       
    	
    	
    	String replicaManagerAnswer = "";

        String serverAnswer1 = this.cancelBooking(bookingID, id, location);
        String serverAnswer2 = this.bookRoom(selectedCampus, selectedRoom, selectedDate, selectedTimeslot, id, location);;
        replicaManagerAnswer = serverAnswer1 + serverAnswer2;
        
        this.replicaManagerLog(replicaManagerAnswer);
        return replicaManagerAnswer;
    }

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}
	
	// Return String that occurs the most often
	public String validateResponseString(String resOne, String resTwo, String resThree, String resFour){
		String replicaManagerAnswer = "";
		String[] resArray = { resOne,  resTwo, resThree, resFour};
	    boolean[] seen = new boolean[resArray.length];
	    String result = null;
	    int result_count = 0;
	    for (int i = 0; i < resArray.length; i++) {
	        if (!seen[i]) {
	            seen[i] = true;
	            int count = 1;
	            for (int j = i + 1; j < resArray.length; j++) {
	                if (!seen[j]) {
	                	if(resArray[j].contains("SEQUENCER")){
	                		replicaErrorCount[j]++;
	                	}
	                	else if (resArray[i].equals(resArray[j])) {
	                        seen[j] = true;
	                        count++;
	                    }
	                }
	            }
	            if (count > result_count) {
	                result_count = count;
	                result = resArray[i];
	            }
	        }
	        
	    }
	    for(int i = 0; i<resArray.length;i++) {
    		if(resArray[i].equals(result)) {
    			continue;
    		}
    		else {
    			replicaErrorCount[i]++;
    		}
    	}
	    replicaManagerAnswer = result;
	    return replicaManagerAnswer;
	}
	
	// Return int that occurs the most often
	public int validateResponseInt(int resOne, int resTwo, int resThree, int resFour){
		int replicaManagerAnswer = 0;
		int[] resArray = {resOne, resTwo, resThree, resFour};
		int count = 1, tempCount;
		  int popular = resArray[0];
		  int temp = 0;
		  for (int i = 0; i < (resArray.length - 1); i++)
		  {
		    temp = resArray[i];
		    tempCount = 0;
		    for (int j = 1; j < resArray.length; j++)
		    {
		      if (temp == resArray[j])
		        tempCount++;
		    }
		    if (tempCount > count)
		    {
		      popular = temp;
		      count = tempCount;
		    }
		  }
		 replicaManagerAnswer = popular;
		 return replicaManagerAnswer;
	}
	

}
