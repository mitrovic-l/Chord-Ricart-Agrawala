package servent.message.files;

import app.backup.MySharedFile;
import servent.message.BasicMessage;
import servent.message.MessageType;

public class FileAddMessage extends BasicMessage {

	private static final long serialVersionUID = 5163039209888734276L;
	private MySharedFile mySharedFile;

	public FileAddMessage(int senderPort, int receiverPort, int key, MySharedFile value) {
		super(MessageType.FILE_ADD, senderPort, receiverPort, String.valueOf(key));
		mySharedFile = value;
	}

	public MySharedFile getDistributedFile() {
		return mySharedFile;
	}
}
