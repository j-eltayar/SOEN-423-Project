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


public class RSServant4 extends RSPOA{

	// Name of the RS.
	private String replicaServerName;
	// Name of the RS log file .
	private String replicaServerLogName;
	
	// Constructor initializes RM with its name and sets up the file for all logs and sets up other RM names.
	public RSServant4(String name, String RMOneName, String RMTwoName) throws IOException {
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
	public String createRoomHere(int roomNumber, String date, String List_Of_Time_Slots, String id, String location) {
        String replicaServerAnswer = "";
        // Implement here
        this.replicaManagerLog(replicaServerAnswer);
        return replicaServerAnswer;
	}

	@Override
	public String deleteRoomHere(int roomNumber, String date, String List_Of_Time_Slots, String id, String location) {
        String replicaServerAnswer = "";
        // Implement here
        this.replicaManagerLog(replicaServerAnswer);
        return replicaServerAnswer;
	}

	@Override
	public String bookRoomHere(String campusName, int roomNumber, String date, String timeslot, String id, String location) {
        String replicaServerAnswer = "";
        // Implement here
        this.replicaManagerLog(replicaServerAnswer);
        return replicaServerAnswer;
	}

	@Override
	public String getAvailableTimeSlotHere(String date, String id, String location) {
        String replicaServerAnswer = "";
        // Implement here
        this.replicaManagerLog(replicaServerAnswer);
        return replicaServerAnswer;
	}

	@Override
	public String cancelBookingHere(String bookingID, String id, String location) {
        String replicaServerAnswer = "";
        // Implement here
        this.replicaManagerLog(replicaServerAnswer);
        return replicaServerAnswer;
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}
}
