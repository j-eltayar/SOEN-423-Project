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
		new Thread(() -> {
			try{
				ORB orb = ORB.init(args, null);
				POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
				rootpoa.the_POAManager().activate();		
				
				RMServant rmServant = new RMServant("DVL", "KKL", "WST");		
				rmServant.setORB(orb);
				
				org.omg.CORBA.Object ref = rootpoa.servant_to_reference(rmServant);
				RM href = RMHelper.narrow(ref);
				
				org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
				NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
				
				
				String name = "DVL";
				NameComponent path[] = ncRef.to_name( name );
				ncRef.rebind(path, href);
				
				System.out.println("Server DVL ready and waiting ...");
				
				orb.run();
			}
			catch (Exception e) {
				System.err.println("ERROR: " + e);
				e.printStackTrace(System.out);
				}
				System.out.println("Server DVL Exiting ...");
        }).start();
		
	}
}
