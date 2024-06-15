package servent.handler.chord;

import java.util.ArrayList;
import java.util.List;

import app.AppConfig;
import app.ServentInfo;
import app.mutex.RAState;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.chord.UpdateMessage;
import servent.message.chord.ReorganizationMessage;
import servent.message.util.MessageUtil;

public class UpdateHandler implements MessageHandler {

	private Message clientMessage;

	public UpdateHandler(Message clientMessage) {
		this.clientMessage = clientMessage;
	}

	@Override
	public void run() {
		if (clientMessage.getMessageType() == MessageType.UPDATE) {
			if (clientMessage.getSenderPort() != AppConfig.myServentInfo.getListenerPort()) {
				ServentInfo newNodInfo = new ServentInfo("localhost", clientMessage.getSenderPort());
				List<ServentInfo> newNodes = new ArrayList<>();
				newNodes.add(newNodInfo);

				// Sinhronizacija nad RAState.lock
				synchronized (RAState.lock) {
					AppConfig.chordState.addNodes(newNodes);
				}

				String newMessageText = "";
				if (clientMessage.getMessageText().equals("")) {
					newMessageText = String.valueOf(AppConfig.myServentInfo.getListenerPort());
				} else {
					newMessageText = clientMessage.getMessageText() + "," + AppConfig.myServentInfo.getListenerPort();
				}
				Message nextUpdate = new UpdateMessage(clientMessage.getSenderPort(), AppConfig.chordState.getNextNodePort(),
						newMessageText);
				MessageUtil.sendMessage(nextUpdate);
			} else {
				String messageText = clientMessage.getMessageText();
				String[] ports = messageText.split(",");

				List<ServentInfo> allNodes = new ArrayList<>();
				for (String port : ports) {
					allNodes.add(new ServentInfo("localhost", Integer.parseInt(port)));
				}

				// Sinhronizacija nad RAState.lock
				synchronized (RAState.lock) {
					AppConfig.chordState.addNodes(allNodes);
				}

				ReorganizationMessage message = new ReorganizationMessage(AppConfig.myServentInfo.getListenerPort(), AppConfig.chordState.getNextNodePort());
				MessageUtil.sendMessage(message);
			}
		} else {
			AppConfig.timestampedErrorPrint("Update message handler got message that is not UPDATE");
		}
	}

}
