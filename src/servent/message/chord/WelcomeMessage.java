package servent.message.chord;

import app.backup.MySharedFile;
import servent.message.BasicMessage;
import servent.message.MessageType;

import java.util.Map;

public class WelcomeMessage extends BasicMessage {

	private static final long serialVersionUID = -8981406250652693908L;

	private Map<Integer, MySharedFile> values;
	
	public WelcomeMessage(int senderPort, int receiverPort, Map<Integer, MySharedFile> values) {
		super(MessageType.WELCOME, senderPort, receiverPort);
		
		this.values = values;
	}
	
	public Map<Integer, MySharedFile> getValues() {
		return values;
	}
}
