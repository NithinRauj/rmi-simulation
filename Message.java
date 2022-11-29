import java.io.Serializable;

public class Message implements Serializable {
    String sender;
    String message;
    int clock = 0;

    public Message(String s, String m, int c) {
        sender = s;
        message = m;
        clock = c;
    }

    void printMessage() {
        System.out.println("Message: " + message + " from " + sender + " with clock value" + clock);
    }
}
