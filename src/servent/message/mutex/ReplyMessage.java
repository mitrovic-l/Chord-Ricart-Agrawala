package servent.message.mutex;

import servent.message.BasicMessage;
import servent.message.MessageType;

public class ReplyMessage extends BasicMessage {

    private static final long serialVersionUID = -5680305731126731433L;

    public ReplyMessage(int senderPort, int receiverPort) {
        super(MessageType.RA_REPLY, senderPort, receiverPort);
    }

    @Override
    public String toString() {
        return "ReplyMessage{" +
                "senderPort=" + getSenderPort() +
                ", receiverPort=" + getReceiverPort() +
                '}';
    }
}
