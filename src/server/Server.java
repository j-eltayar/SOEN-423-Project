package server;

import RMAPP.*;
import RSAPP.*;
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
	
	public void run() {
		
	};

	public static void main(String[] args) throws RemoteException {
		
		
		try {

			Properties props = new Properties();
			props.put("org.omg.CORBA.ORBInitialPort", "1050");
			props.put("org.omg.CORBA.ORBInitialHost", "localhost");
			ORB orb = ORB.init(args, props);
			
			// Create Replica Managers
			RMServant rmServantDVL = new RMServant("DVL", "KKL", "WST", "DVL1", "DVL2", "DVL3", "DVL4");
			RMServant rmServantKKL = new RMServant("KKL", "DVL", "WST", "KKL1", "KKL2", "KKL3", "KKL4");
			RMServant rmServantWST = new RMServant("WST", "DVL", "KKL", "WST1", "WST2", "WST3", "WST4");
			
			// Create DVL Replica Servers
			RSServant1 rsServantDVL1 = new RSServant1("DVL1");
			RSServant2 rsServantDVL2 = new RSServant2("DVL2");
			RSServant2 rsServantDVL3 = new RSServant2("DVL3");
			RSServant1 rsServantDVL4 = new RSServant1("DVL4");
			
			// Create KKL Replica Servers
			RSServant1 rsServantKKL1 = new RSServant1("KKL1");
			RSServant2 rsServantKKL2 = new RSServant2("KKL2");
			RSServant2 rsServantKKL3 = new RSServant2("KKL3");
			RSServant1 rsServantKKL4 = new RSServant1("KKL4");
			
			// Create WST Replica Servers
			RSServant1 rsServantWST1 = new RSServant1("WST1");
			RSServant2 rsServantWST2 = new RSServant2("WST2");
			RSServant2 rsServantWST3 = new RSServant2("WST3");
			RSServant1 rsServantWST4 = new RSServant1("WST4");
			
			// Set Orbs for Replica Managers
			rmServantDVL.setORB(orb);
			rmServantKKL.setORB(orb);
			rmServantWST.setORB(orb);
			
			// Set orbs for DVL Replica Servers
			rsServantDVL1.setORB(orb);
			rsServantDVL2.setORB(orb);
			rsServantDVL3.setORB(orb);
			rsServantDVL4.setORB(orb);
			
			// Set orbs for KKL Replica Servers
			rsServantKKL1.setORB(orb);
			rsServantKKL2.setORB(orb);
			rsServantKKL3.setORB(orb);
			rsServantKKL4.setORB(orb);
			
			// Set orbs for WST Replica Servers
			rsServantWST1.setORB(orb);
			rsServantWST2.setORB(orb);
			rsServantWST3.setORB(orb);
			rsServantWST4.setORB(orb);
	
			// Set root poa for the orb
			POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
			rootpoa.the_POAManager().activate();		
			
			// Add servant Server Manager objects to the rootpoa
			org.omg.CORBA.Object refDVL = rootpoa.servant_to_reference(rmServantDVL);
			org.omg.CORBA.Object refKKL = rootpoa.servant_to_reference(rmServantKKL);
			org.omg.CORBA.Object refWST = rootpoa.servant_to_reference(rmServantWST);
			
			// Add servant DVL Replica Server objects to the rootpoa
			org.omg.CORBA.Object refRSDVL1 = rootpoa.servant_to_reference(rsServantDVL1);
			org.omg.CORBA.Object refRSDVL2 = rootpoa.servant_to_reference(rsServantDVL2);
			org.omg.CORBA.Object refRSDVL3 = rootpoa.servant_to_reference(rsServantDVL3);
			org.omg.CORBA.Object refRSDVL4 = rootpoa.servant_to_reference(rsServantDVL4);
			
			// Add servant KKL Replica Server objects to the rootpoa
			org.omg.CORBA.Object refRSKKL1 = rootpoa.servant_to_reference(rsServantKKL1);
			org.omg.CORBA.Object refRSKKL2 = rootpoa.servant_to_reference(rsServantKKL2);
			org.omg.CORBA.Object refRSKKL3 = rootpoa.servant_to_reference(rsServantKKL3);
			org.omg.CORBA.Object refRSKKL4 = rootpoa.servant_to_reference(rsServantKKL4);
			
			// Add servant WST Replica Server objects to the rootpoa
			org.omg.CORBA.Object refRSWST1 = rootpoa.servant_to_reference(rsServantWST1);
			org.omg.CORBA.Object refRSWST2 = rootpoa.servant_to_reference(rsServantWST2);
			org.omg.CORBA.Object refRSWST3 = rootpoa.servant_to_reference(rsServantWST3);
			org.omg.CORBA.Object refRSWST4 = rootpoa.servant_to_reference(rsServantWST4);
			
			// Narrow Replica Managers to the reference
			RM hrefDVL = RMHelper.narrow(refDVL);
			RM hrefKKL = RMHelper.narrow(refKKL);
			RM hrefWST = RMHelper.narrow(refWST);
			
			// Narrow DVL Replica Servers to the reference
			RS hrefRSDVL1 = RSHelper.narrow(refRSDVL1);
			RS hrefRSDVL2 = RSHelper.narrow(refRSDVL2);
			RS hrefRSDVL3 = RSHelper.narrow(refRSDVL3);
			RS hrefRSDVL4 = RSHelper.narrow(refRSDVL4);
			
			// Narrow KKL Replica Servers to the reference
			RS hrefRSKKL1 = RSHelper.narrow(refRSKKL1);
			RS hrefRSKKL2 = RSHelper.narrow(refRSKKL2);
			RS hrefRSKKL3 = RSHelper.narrow(refRSKKL3);
			RS hrefRSKKL4 = RSHelper.narrow(refRSKKL4);
			
			// Narrow WST Replica Servers to the reference
			RS hrefRSWST1 = RSHelper.narrow(refRSWST1);
			RS hrefRSWST2 = RSHelper.narrow(refRSWST2);
			RS hrefRSWST3 = RSHelper.narrow(refRSWST3);
			RS hrefRSWST4 = RSHelper.narrow(refRSWST4);
			
			// Resolve initial references to the naming service
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
			
			// Name components for Replica Managers
			NameComponent pathDVL[] = ncRef.to_name("DVL");
			NameComponent pathKKL[] = ncRef.to_name("KKL");
			NameComponent pathWST[] = ncRef.to_name("WST");
			
			// Name components for DVL Replica Servers
			NameComponent pathRSDVL1[] = ncRef.to_name("DVL1");
			NameComponent pathRSDVL2[] = ncRef.to_name("DVL2");
			NameComponent pathRSDVL3[] = ncRef.to_name("DVL3");
			NameComponent pathRSDVL4[] = ncRef.to_name("DVL4");
			
			// Name components for KKL Replica Servers
			NameComponent pathRSKKL1[] = ncRef.to_name("KKL1");
			NameComponent pathRSKKL2[] = ncRef.to_name("KKL2");
			NameComponent pathRSKKL3[] = ncRef.to_name("KKL3");
			NameComponent pathRSKKL4[] = ncRef.to_name("KKL4");
			
			// Name components for WST Replica Servers
			NameComponent pathRSWST1[] = ncRef.to_name("WST1");
			NameComponent pathRSWST2[] = ncRef.to_name("WST2");
			NameComponent pathRSWST3[] = ncRef.to_name("WST3");
			NameComponent pathRSWST4[] = ncRef.to_name("WST4");
			
			// Rebind Replica Manager nvRefs to hrefs
			ncRef.rebind(pathDVL, hrefDVL);
			ncRef.rebind(pathKKL, hrefKKL);
			ncRef.rebind(pathWST, hrefWST);
			
			// Rebind DVL Replica Server nvRefs to hrefs
			ncRef.rebind(pathRSDVL1, hrefRSDVL1);
			ncRef.rebind(pathRSDVL2, hrefRSDVL2);
			ncRef.rebind(pathRSDVL3, hrefRSDVL3);
			ncRef.rebind(pathRSDVL4, hrefRSDVL4);
			
			// Rebind KKL Replica Server nvRefs to hrefs
			ncRef.rebind(pathRSKKL1, hrefRSKKL1);
			ncRef.rebind(pathRSKKL2, hrefRSKKL2);
			ncRef.rebind(pathRSKKL3, hrefRSKKL3);
			ncRef.rebind(pathRSKKL4, hrefRSKKL4);
			
			// Rebind WST Replica Server nvRefs to hrefs
			ncRef.rebind(pathRSWST1, hrefRSWST1);
			ncRef.rebind(pathRSWST2, hrefRSWST2);
			ncRef.rebind(pathRSWST3, hrefRSWST3);
			ncRef.rebind(pathRSWST4, hrefRSWST4);
						
			System.out.println("Servers ready and waiting ...");
			new Thread (() -> {
				orb.run();
			}).start();
			
			// Set Inter Replica Manager Connections
			rmServantDVL.setRMServers();
			rmServantKKL.setRMServers();
			rmServantWST.setRMServers();
			
			// Set Replica Server connections between RMDVL and the 4 RSDVL instances
			rmServantDVL.setRSServers();
			// Set Replica Server connections between RMKKL and the 4 RSKKL instances
			rmServantKKL.setRSServers();
			// Set Replica Server connections between RMWST and the 4 RSWST instances
			rmServantWST.setRSServers();
			
			
		
		} catch( Exception e ) {
			System.err.println("ERROR: " + e);
			e.printStackTrace();
			System.out.println("Servers have failed and will close now.");
		}
		
	}
}
