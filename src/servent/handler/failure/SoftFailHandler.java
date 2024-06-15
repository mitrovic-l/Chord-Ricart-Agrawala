package servent.handler.failure;

import app.AppConfig;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.check.TackMessage;
import servent.message.util.MessageUtil;

public class SoftFailHandler implements MessageHandler {
    private Message clientMessage;

    public SoftFailHandler(Message clientMessage){
        this.clientMessage = clientMessage;
    }
    @Override
    public void run() {
        if(clientMessage.getMessageType() == MessageType.SOFT_FAIL){
            TackMessage tackMessage = new TackMessage(AppConfig.myServentInfo.getListenerPort(), clientMessage.getSenderPort());
            MessageUtil.sendMessage(tackMessage);
        }
    }
}
