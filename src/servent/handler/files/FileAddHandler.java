package servent.handler.files;

import app.AppConfig;
import app.backup.MySharedFile;
import app.mutex.RAState;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.files.FileAddMessage;

public class FileAddHandler implements MessageHandler {

	private Message clientMessage;

	public FileAddHandler(Message clientMessage) {
		this.clientMessage = clientMessage;
	}

	@Override
	public void run() {
		if (clientMessage.getMessageType() != MessageType.FILE_ADD) {
			return;
		}

		String keyString = clientMessage.getMessageText();
		if (keyString.isEmpty()) {
			return;
		}

		try {
			int key = Integer.parseInt(keyString);
			FileAddMessage fileAddMessage = (FileAddMessage) clientMessage;
			MySharedFile mySharedFile = fileAddMessage.getDistributedFile();

			// Sinhronizovani blok nad RAState.lock
			synchronized (RAState.lock) {
				AppConfig.chordState.putValue(key, mySharedFile);
			}
		} catch (NumberFormatException e) {
			AppConfig.timestampedErrorPrint("Bad key in add_file: " + keyString);
		}
	}
}
