package servent.message.failure;

import servent.message.BasicMessage;
import servent.message.MessageType;

public class HardFailMessage extends BasicMessage {
    public HardFailMessage(int senderPort, int receiverPort, int failedNodePort) {
        super(MessageType.HARD_FAIL, senderPort, receiverPort, String.valueOf(failedNodePort));
    }
}
