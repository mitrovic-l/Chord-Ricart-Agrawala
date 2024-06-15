package servent.message.chord;

import servent.message.BasicMessage;
import servent.message.MessageType;

public class ReorganizationMessage extends BasicMessage {
    public ReorganizationMessage(int senderPort, int receiverPort) {
        super(MessageType.REORGANIZATION, senderPort, receiverPort);
    }
}
