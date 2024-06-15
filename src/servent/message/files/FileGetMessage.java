package servent.message.files;

import servent.message.BasicMessage;
import servent.message.MessageType;

public class FileGetMessage extends BasicMessage {
    public FileGetMessage(int senderPort, int receiverPort) {
        super(MessageType.FILE_GET, senderPort, receiverPort);
    }
}
