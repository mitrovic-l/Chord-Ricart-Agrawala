package servent.message.failure;

import servent.message.BasicMessage;
import servent.message.MessageType;

public class HardFailConfirmMessage extends BasicMessage {
    public HardFailConfirmMessage(int senderPort, int receiverPort, int failedNodePort) {
        super(MessageType.HARD_FAIL_CONFIRM, senderPort, receiverPort, String.valueOf(failedNodePort));
    }
}
