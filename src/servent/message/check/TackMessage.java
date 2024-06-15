package servent.message.check;

import servent.message.BasicMessage;
import servent.message.MessageType;

public class TackMessage extends BasicMessage {
    public TackMessage(int senderPort, int receiverPort) {
        super(MessageType.TACK, senderPort, receiverPort);
    }
}
