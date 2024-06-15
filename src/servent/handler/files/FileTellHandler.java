package servent.handler.files;

import app.AppConfig;
import app.backup.MySharedFile;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.files.FileTellMessage;

public class FileTellHandler implements MessageHandler {

	private Message clientMessage;

	public FileTellHandler(Message clientMessage) {
		this.clientMessage = clientMessage;
	}

	@Override
	public void run() {
		if (clientMessage.getMessageType() != MessageType.FILE_TELL) {
			AppConfig.timestampedErrorPrint("Received message is not of type TELL_GET");
			return;
		}

		String keyString = clientMessage.getMessageText();
		if (keyString.isEmpty()) {
			AppConfig.timestampedErrorPrint("Received TELL_GET message with empty key");
			return;
		}

		try {
			int key = Integer.parseInt(keyString);
			FileTellMessage fileTellMessage = (FileTellMessage) clientMessage;
			MySharedFile file = fileTellMessage.getDistributedFile();

			if (file == null) {
				AppConfig.timestampedStandardPrint("----[*]---> From: " + clientMessage.getSenderPort() + " - No such key: " + key);
			} else {
				AppConfig.timestampedStandardPrint("----[*]---> File(" + key + "):\n" + file.getContent()); // Prikaz sadržaja fajla
			}
		} catch (NumberFormatException e) {
			AppConfig.timestampedErrorPrint("Invalid key in TELL_GET message: " + keyString);
		}
	}
}