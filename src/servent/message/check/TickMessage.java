package servent.message.check;

import servent.message.BasicMessage;
import servent.message.MessageType;

public class TickMessage extends BasicMessage {
    public TickMessage(int senderPort, int receiverPort) {
        super(MessageType.TICK, senderPort, receiverPort);
    }
}
