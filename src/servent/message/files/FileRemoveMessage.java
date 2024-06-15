package servent.message.files;

import servent.message.BasicMessage;
import servent.message.MessageType;

public class FileRemoveMessage extends BasicMessage {
    public FileRemoveMessage(int senderPort, int receiverPort, String fileName) {
        super(MessageType.FILE_REMOVE, senderPort, receiverPort, fileName);
    }
}
