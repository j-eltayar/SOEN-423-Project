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

	@Override
	public String sayHello() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createRoomHere(int roomNumber, String date, String List_Of_Time_Slots, String id, String location) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String deleteRoomHere(int roomNumber, String date, String List_Of_Time_Slots, String id, String location) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String bookRoomHere(String campusName, int roomNumber, String date, String timeslot, String id,
			String location) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAvailableTimeSlotHere(String date, String id, String location) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String cancelBookingHere(String bookingID, String id, String location) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}

}
