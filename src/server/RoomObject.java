package server;
import java.util.concurrent.*;

public class RoomObject {

	int roomNumber;
	ConcurrentHashMap<String,ConcurrentHashMap<String,String>> Dates = new ConcurrentHashMap<String,ConcurrentHashMap<String,String>>();
	
	/*
	 * populating
	 * innerMap.put("InnerKey", "InnerValue");
	 *	outerMap.put("OuterKey", innerMap);
	 *
	 *retrieving
	 *  String value = ((HashMap<String, String>)outerMap.get("OuterKey")).get("InnerKey").toString();
	 *  System.out.println("Retrieved value is : " + value);
	 *
	 */
	
	public RoomObject(int roomNumber){
		this.roomNumber = roomNumber;

	}
	
}
