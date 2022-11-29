import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ProcessObject extends Remote {
    void sendMessage(Message m) throws RemoteException;

    int getClock() throws RemoteException;

    void receiveMessage() throws RemoteException;

    void toggleProcessLock() throws RemoteException;
}