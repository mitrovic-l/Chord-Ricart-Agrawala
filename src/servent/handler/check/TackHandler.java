package servent.handler.check;

import app.backup.Heartbeat;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;

public class TackHandler implements MessageHandler {
    private Message clientMessage;
    public TackHandler(Message clientMessage){
        this.clientMessage = clientMessage;
    }
    @Override
    public void run() {
        if(clientMessage.getMessageType() == MessageType.TACK){
            Heartbeat.getInstance().heartResponded(clientMessage.getSenderPort());
        }
    }
}
