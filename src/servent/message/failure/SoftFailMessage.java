package servent.message.failure;

import servent.message.BasicMessage;
import servent.message.MessageType;

public class SoftFailMessage extends BasicMessage {
    public SoftFailMessage(int senderPort, int receiverPort) {
        super(MessageType.SOFT_FAIL, senderPort, receiverPort);
    }
}
