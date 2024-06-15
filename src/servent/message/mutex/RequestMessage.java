package servent.message.mutex;

import servent.message.BasicMessage;
import servent.message.MessageType;

public class RequestMessage extends BasicMessage {

    private static final long serialVersionUID = -8091942872037803682L;
    private final int requestNumber;
    private final long timestamp;
    private final int requesterPort;

    public RequestMessage(int senderPort, int receiverPort, int requestNumber, long timestamp, int requesterPort) {
        super(MessageType.RA_REQUEST, senderPort, receiverPort);
        this.requestNumber = requestNumber;
        this.timestamp = timestamp;
        this.requesterPort = requesterPort;
    }

    public int getRequestNumber() {
        return requestNumber;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getRequesterPort() {
        return requesterPort;
    }

    @Override
    public String toString() {
        return "RequestMessage{" +
                "requestNumber=" + requestNumber +
                ", timestamp=" + timestamp +
                ", requesterPort=" + requesterPort +
                ", senderPort=" + getSenderPort() +
                ", receiverPort=" + getReceiverPort() +
                '}';
    }
}
