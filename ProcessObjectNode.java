import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class ProcessObjectNode extends java.rmi.server.UnicastRemoteObject implements ProcessObject {
    public static String[] processes = { "p1", "p2", "p3", "p4" };
    private static double BYZANTINE_FAULT_PROBABILITY = 0.3;

    private boolean locked = true;
    private int clock = 0;
    private String processName;
    private ArrayList<Message> mqueue = new ArrayList<Message>();

    public ProcessObjectNode(String s) throws RemoteException {
        processName = s;
    }

    public void setClock(int value) {
        clock = value;
    }

    public synchronized int getClock() {
        return clock;
    }

    public synchronized void toggleProcessLock() {
        locked = locked ? false : true;
    }

    public synchronized void sendMessage(Message m) throws RemoteException {
        mqueue.add(m);
    }

    private static int generateChoice() {
        Random rand = new Random();
        return rand.nextInt(4);
    }

    private static boolean isFailure() {
        double event = Math.random();
        return event < BYZANTINE_FAULT_PROBABILITY;
    }

    public synchronized void receiveMessage() throws RemoteException {
        if (mqueue.size() > 0) {
            Message msg = mqueue.remove(0);
            System.out.println("Sender clock: " + msg.clock);
            if (isFailure()) {
                System.out.println("Byzantine failure occured...");
                return;
            }
            if (clock <= msg.clock) {
                setClock(msg.clock + 1);
            } else {
                setClock(clock + 1);
            }
            System.out.println(processName + " clock value:" + clock);
        }
    }

    private static void runProcess(String baseUrl, String processUrl, ProcessObjectNode currentProcess)
            throws InterruptedException, NotBoundException, MalformedURLException, RemoteException {
        Thread.sleep(1000);
        int choice = generateChoice();

        if (processes[choice].equals(currentProcess.processName)) {
            System.out.println("Internal event in process");
        } else {
            ProcessObject chosenProcess = (ProcessObject) Naming.lookup(baseUrl + processes[choice]);
            Message msg = new Message(processUrl, "message", currentProcess.clock);
            chosenProcess.sendMessage(msg);
        }
        currentProcess.setClock(currentProcess.clock + 1);

        currentProcess.receiveMessage();
        Thread.sleep(1000);
    }

    public static void main(String args[])
            throws RemoteException, MalformedURLException, InterruptedException, NotBoundException {
        // if (System.getSecurityManager() == null) {
        // System.setSecurityManager(new SecurityManager());
        // }
        String baseUrl = args[0];
        String processName = args[1];

        if (Arrays.asList(processes).contains(processName)) {
            String processUrl = baseUrl + processName;
            ProcessObjectNode currentProcess = new ProcessObjectNode(processName);
            Naming.rebind(processUrl, currentProcess);

            System.out.println("Binded process to name " + processUrl);
            System.out.println(processUrl + " ready!");

            while (currentProcess.locked) {
                // Be idle
                System.out.println("Process idle due to lock...");
                Thread.sleep(2000);
            }

            System.out.println("Process started as lock has been released");
            while (!currentProcess.locked) {
                runProcess(baseUrl, processUrl, currentProcess);
            }
        } else {
            System.err.println("Use a valid process name (p1,p2,p3,p4)");
            System.exit(1);
        }
    }
}
