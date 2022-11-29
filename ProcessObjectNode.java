import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
// import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class ProcessObjectNode extends java.rmi.server.UnicastRemoteObject implements ProcessObject {
    public static String[] processes = { "p1", "p2", "p3", "p4" };
    private boolean locked = true;
    private int clock = 0;
    private String processName;
    private ArrayList<Message> mqueue = new ArrayList<Message>();

    public ProcessObjectNode(String s) throws RemoteException {
        processName = s;
    }

    public void setClock(int value) {
        clock += value;
    }

    public synchronized int getClock() {
        return clock;
    }

    public synchronized void toggleProcessLock() {
        locked = locked ? false : true;
    }

    public synchronized void sendMessage(Message m) throws RemoteException {
        mqueue.add(m);
        System.out.println("Sending message");
        // m.printMessage();
    }

    private static int generateChoice() {
        final int MIN = 1;
        final int MAX = 5;
        return (int) ((Math.random() * (MAX - MIN)) + MIN) - 1;
    }

    public synchronized void receiveMessage() throws RemoteException {
        if (mqueue.size() > 0) {
            Message msg = mqueue.remove(0);
            // msg.printMessage();
            System.out.println("Sender clock: " + msg.clock);
            if (clock <= msg.clock) {
                // clock += msg.clock;
                setClock(msg.clock + 1);
            } else {
                setClock(1);
            }
            System.out.println(processName + " clock value:" + clock);
        }
    }

    public static void main(String args[])
            throws RemoteException, MalformedURLException, NotBoundException, InterruptedException {
        // if (System.getSecurityManager() == null) {
        // System.setSecurityManager(new SecurityManager());
        // }
        String processBaseUrl = "rmi://localhost:6000/";
        String processName = args[0];

        if (processName.equals("p1") || processName.equals("p2")
                || processName.equals("p3") || processName.equals("p4")) {
            String process = processBaseUrl + processName;
            ProcessObjectNode currentProcess = new ProcessObjectNode(process);
            Naming.rebind(process, currentProcess);

            System.out.println("Binded process to name " + process);
            System.out.println(process + " ready!");

            while (currentProcess.locked) {
                // Be idle
                System.out.println("Process idle due to lock...");
                Thread.sleep(2000);
            }

            System.out.println("Process started as lock has been released");
            while (!currentProcess.locked) {
                Thread.sleep(1000);
                int choice = generateChoice();
                System.out.println("chosen process " + choice);
                ProcessObject chosenProcess = (ProcessObject) Naming.lookup(processBaseUrl +
                        processes[choice]);
                Message msg = new Message(process, "message", currentProcess.clock);
                chosenProcess.sendMessage(msg);
                currentProcess.setClock(1);
                currentProcess.receiveMessage();
                Thread.sleep(1000);
            }
        } else {
            System.err.println("Use a valid process name (p1,p2,p3,p4)");
            System.exit(1);
        }
    }
}
