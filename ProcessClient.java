import java.net.MalformedURLException;
import java.rmi.*;

public class ProcessClient {

    public static int SIM_TIME = 20;

    public static void main(String args[])
            throws MalformedURLException, RemoteException, NotBoundException, InterruptedException {
        // if (System.getSecurityManager() == null) {
        // System.setSecurityManager(new SecurityManager());
        // }
        String p1Name = "rmi://localhost:6000/p1";
        ProcessObject p1 = (ProcessObject) Naming.lookup(p1Name);
        String p2Name = "rmi://localhost:6000/p2";
        ProcessObject p2 = (ProcessObject) Naming.lookup(p2Name);
        String p3Name = "rmi://localhost:6000/p3";
        ProcessObject p3 = (ProcessObject) Naming.lookup(p3Name);
        String p4Name = "rmi://localhost:6000/p4";
        ProcessObject p4 = (ProcessObject) Naming.lookup(p4Name);
        int timer = 0;

        // Release locks for processes
        p1.toggleProcessLock();
        p2.toggleProcessLock();
        p3.toggleProcessLock();
        p4.toggleProcessLock();

        // Run simulation
        while (timer <= ProcessClient.SIM_TIME) {
            System.out.println("p1: " + p1.getClock() + " p2: " + p2.getClock() +
                    " p3: " + p3.getClock() + " p4: " + p4.getClock());
            Thread.sleep(1000);
            timer++;
        }

        System.out.println("Ending simulation...");

        // Lock processes from sending any messages
        p1.toggleProcessLock();
        p2.toggleProcessLock();
        p3.toggleProcessLock();
        p4.toggleProcessLock();
    }
}
