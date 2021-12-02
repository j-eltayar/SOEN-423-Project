package server;
import RSAPP.*;
import killian.core.*;
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
import java.util.List;


public class RSServant3 extends RSPOA{

	CampusServerImpl server;

	// Name of the RS.
	private String replicaServerName;
	// Name of the RS log file .
	private String replicaServerLogName;
	//An orb
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
	public RSServant3(String name) throws IOException {
        this.replicaServerName = name;
        this.replicaServerLogName = "ReplicaServerLogs\\" + this.replicaServerName + ".txt";
        final File yourFile = new File(this.replicaServerLogName);
        yourFile.createNewFile();
		String campusName = name.replaceAll("\\d", "");

		server = new CampusServerImpl(Campus.valueOf(campusName));
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
        String replicaServerAnswer;
		Date roomDate = getDate(date);
		String[] timeSlotsString = List_Of_Time_Slots.split("\\.");
		TimeSlot[] timeSlots = new TimeSlot[timeSlotsString.length];
		for (int i = 0; i < timeSlotsString.length; i++) {
			timeSlots[i] = getTimeSlot(timeSlotsString[i]);
		}
		replicaServerAnswer = server.createRoom(roomNumber, roomDate, timeSlots, id);

        this.replicaManagerLog(replicaServerAnswer);
        return replicaServerAnswer;
	}

	private Date getDate(String date) {
		int year = Integer.parseInt(date.split("-")[2]);
		int month = Integer.parseInt(date.split("-")[1]);
		int day = Integer.parseInt(date.split("-")[0]);
		return DateFactory.create(year, month, day);
	}

	private TimeSlot getTimeSlot(String timeslot) {
		String startTime = timeslot.split(",")[0];
		int startTimeHour = Integer.parseInt(startTime.split(":")[0]);
		int startTimeMin = Integer.parseInt(startTime.split(":")[1]);
		int endTimeHour = startTimeHour + 1;

		return TimeSlotFactory.create(startTimeHour, startTimeMin, endTimeHour, startTimeMin);
	}

	@Override
	public String deleteRoomHere(int roomNumber, String date, String List_Of_Time_Slots, String id, String location) {
		String replicaServerAnswer;
		Date roomDate = getDate(date);
		String[] timeSlotsString = List_Of_Time_Slots.split("\\.");
		TimeSlot[] timeSlots = new TimeSlot[timeSlotsString.length];
		for (int i = 0; i < timeSlotsString.length; i++) {
			timeSlots[i] = getTimeSlot(timeSlotsString[i]);
		}
		replicaServerAnswer = server.deleteRoom(roomNumber, roomDate, timeSlots, id);

		this.replicaManagerLog(replicaServerAnswer);
        return replicaServerAnswer;
	}

	@Override
	public String bookRoomHere(String campusName, int roomNumber, String date, String timeslot, String id, String location) {
		String replicaServerAnswer;
		Date roomDate = getDate(date);
		TimeSlot timeSlot = getTimeSlot(timeslot);
		replicaServerAnswer = server.bookRoom(Campus.valueOf(campusName), roomNumber, roomDate, timeSlot, id);

        this.replicaManagerLog(replicaServerAnswer);
        return replicaServerAnswer;
	}

	@Override
	public String getAvailableTimeSlotHere(String date, String id, String location) {
		String replicaServerAnswer;
		Date roomDate = getDate(date);
		replicaServerAnswer = server.getAvailableTimeSlot(roomDate, id);

		this.replicaManagerLog(replicaServerAnswer);
        return replicaServerAnswer;
	}

	@Override
	public String cancelBookingHere(String bookingID, String id, String location) {
		String replicaServerAnswer;

		replicaServerAnswer = server.cancelBooking(bookingID, id);

		this.replicaManagerLog(replicaServerAnswer);
        return replicaServerAnswer;
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}
}
