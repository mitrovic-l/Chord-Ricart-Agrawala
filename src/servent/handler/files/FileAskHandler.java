package servent.handler.files;

import java.util.Map;

import app.AppConfig;
import app.ServentInfo;
import app.backup.MySharedFile;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.files.FileAskMessage;
import servent.message.files.FileTellMessage;
import servent.message.files.FIleDeniedMessage;
import servent.message.util.MessageUtil;

public class FileAskHandler implements MessageHandler {

	private FileAskMessage clientMessage;

	public FileAskHandler(Message clientMessage) {
		this.clientMessage = (FileAskMessage) clientMessage;
	}

	@Override
	public void run() {
		if (clientMessage.getMessageType() != MessageType.FILE_ASK) {
			AppConfig.timestampedErrorPrint("Received message is not of type ASK_GET");
			return;
		}

		try {
			int key = Integer.parseInt(clientMessage.getMessageText());
			if (AppConfig.chordState.isKeyMine(key)) {
				handleFileRequest(key);
			} else {
				// Prosleđivanje zahteva sledećem čvoru
				ServentInfo nextNode = AppConfig.chordState.getNextNodeForKey(key);
				FileAskMessage fileAskMessage = new FileAskMessage(
						clientMessage.getSenderPort(),
						nextNode.getListenerPort(),
						clientMessage.getMessageText(),
						clientMessage.getMyFriends()
				);
				MessageUtil.sendMessage(fileAskMessage);
			}
		} catch (NumberFormatException e) {
			AppConfig.timestampedErrorPrint("Invalid key in ASK_GET message: " + clientMessage.getMessageText());
		}
	}

	// Obrada zahteva za lokalni fajl
	private void handleFileRequest(int key) {
		Map<Integer, MySharedFile> valueMap = AppConfig.chordState.getValueMap();
		MySharedFile file = valueMap.get(key);
		if (file == null) {
			AppConfig.timestampedStandardPrint("File not found for key: " + key);
			return;
		}

		if (!file.isPublic() && !clientMessage.getMyFriends().contains(file.getOwnerPort())) {
			// Slanje poruke o nedozvoljenom pristupu
			FIleDeniedMessage FIleDeniedMessage = new FIleDeniedMessage(
					AppConfig.myServentInfo.getListenerPort(),
					clientMessage.getSenderPort(),
					key
			);
			MessageUtil.sendMessage(FIleDeniedMessage);
			return;
		}

		sendFile(key, file);
	}

	// Slanje fajla podnosiocu zahteva
	private void sendFile(int key, MySharedFile file) {
		FileTellMessage fileTellMessage = new FileTellMessage(
				AppConfig.myServentInfo.getListenerPort(),
				clientMessage.getSenderPort(),
				key,
				file
		);
		MessageUtil.sendMessage(fileTellMessage);
	}
}