package servent.message.files;

import servent.message.BasicMessage;
import servent.message.MessageType;

import java.util.ArrayList;
import java.util.List;

public class FileAskMessage extends BasicMessage {

	private static final long serialVersionUID = -8558031124520315033L;
	private List<Integer> myFriends;
	public FileAskMessage(int senderPort, int receiverPort, String text, List<Integer> myFriends) {
		super(MessageType.FILE_ASK, senderPort, receiverPort, text);
		this.myFriends = new ArrayList<>(myFriends);
	}

	public List<Integer> getMyFriends() {
		return myFriends;
	}
}
