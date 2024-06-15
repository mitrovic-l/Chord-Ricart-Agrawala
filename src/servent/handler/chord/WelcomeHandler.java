package servent.handler.chord;

import app.AppConfig;
import app.mutex.DistributedMutex;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.chord.UpdateMessage;
import servent.message.chord.WelcomeMessage;
import servent.message.util.MessageUtil;

public class WelcomeHandler implements MessageHandler {

	private Message clientMessage;
	private DistributedMutex mutex;
	
	public WelcomeHandler(Message clientMessage, DistributedMutex mutex) {
		this.clientMessage = clientMessage;
		this.mutex = mutex;
	}
	
	@Override
	public void run() {
		if (clientMessage.getMessageType() == MessageType.WELCOME) {

			WelcomeMessage welcomeMsg = (WelcomeMessage)clientMessage;
			AppConfig.timestampedStandardPrint("Dobio sam welcome message, pozivam init za chordstate");
			AppConfig.chordState.init(welcomeMsg);
			
			UpdateMessage um = new UpdateMessage(AppConfig.myServentInfo.getListenerPort(), AppConfig.chordState.getNextNodePort(), "");
			MessageUtil.sendMessage(um);
			
		} else {
			AppConfig.timestampedErrorPrint("Welcome handler got a message that is not WELCOME");
		}

	}

}
