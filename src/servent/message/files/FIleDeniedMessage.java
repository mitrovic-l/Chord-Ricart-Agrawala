package servent.message.files;

import servent.message.BasicMessage;
import servent.message.MessageType;

public class FIleDeniedMessage extends BasicMessage {
    public FIleDeniedMessage(int senderPort, int receiverPort, int key) {
        super(MessageType.FILE_DENIED, senderPort, receiverPort, String.valueOf(key));
    }
}
