package servent.handler.check;

import app.AppConfig;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.check.TackMessage;
import servent.message.util.MessageUtil;

public class TickHandler implements MessageHandler {
    private Message clientMessage;
    public TickHandler(Message clientMessage){
        this.clientMessage = clientMessage;
    }
    @Override
    public void run() {
        if(clientMessage.getMessageType() == MessageType.TICK){
            TackMessage tackMessage = new TackMessage(AppConfig.myServentInfo.getListenerPort(), clientMessage.getSenderPort());
            MessageUtil.sendMessage(tackMessage);
        }
    }
}
