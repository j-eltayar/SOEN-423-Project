package server;

import RMAPP.*;
import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.*;
import org.omg.CORBA.*;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Properties;

public class Server {

	public static void main(String[] args) throws RemoteException {
		
		try {
			ORB orb = ORB.init(args, null);
			RMServant rmServantDVL = new RMServant("DVL", "KKL", "WST");
			RMServant rmServantKKL = new RMServant("KKL", "DVL", "WST");
			RMServant rmServantWST = new RMServant("WST", "DVL", "KKL");
			
			rmServantDVL.setORB(orb);
			rmServantKKL.setORB(orb);
			rmServantWST.setORB(orb);
	
			POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
			rootpoa.the_POAManager().activate();		
			
			org.omg.CORBA.Object refDVL = rootpoa.servant_to_reference(rmServantDVL);
			org.omg.CORBA.Object refKKL = rootpoa.servant_to_reference(rmServantKKL);
			org.omg.CORBA.Object refWST = rootpoa.servant_to_reference(rmServantWST);
			
			RM hrefDVL = RMHelper.narrow(refDVL);
			RM hrefKKL = RMHelper.narrow(refKKL);
			RM hrefWST = RMHelper.narrow(refWST);
			
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
			
			NameComponent pathDVL[] = ncRef.to_name("DVL");
			NameComponent pathKKL[] = ncRef.to_name("KKL");
			NameComponent pathWST[] = ncRef.to_name("WST");
			
			ncRef.rebind(pathDVL, hrefDVL);
			ncRef.rebind(pathKKL, hrefKKL);
			ncRef.rebind(pathWST, hrefWST);
			
			System.out.println("Servers ready and waiting ...");
			new Thread (() -> {
				orb.run();
			}).start();
			
			rmServantDVL.setRMServers();
			rmServantKKL.setRMServers();
			rmServantWST.setRMServers();
		
		} catch( Exception e ) {
			System.err.println("ERROR: " + e);
			e.printStackTrace();
			System.out.println("Servers have failed and will close now.");
		}
		
	}
}
