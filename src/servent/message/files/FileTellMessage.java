package servent.message.files;

import app.backup.MySharedFile;
import servent.message.BasicMessage;
import servent.message.MessageType;

public class FileTellMessage extends BasicMessage {

	private static final long serialVersionUID = -6213394344524749872L;
	private MySharedFile mySharedFile;

	public FileTellMessage(int senderPort, int receiverPort, int key, MySharedFile value) {
		super(MessageType.FILE_TELL, senderPort, receiverPort, String.valueOf(key));
		mySharedFile = value;
	}

	public MySharedFile getDistributedFile() {
		return mySharedFile;
	}
}
