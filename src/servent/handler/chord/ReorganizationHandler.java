package servent.handler.chord;

import app.AppConfig;
import app.mutex.RAState;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;

public class ReorganizationHandler implements MessageHandler {
    private Message clientMessage;
    public ReorganizationHandler(Message clientMessage){
        this.clientMessage = clientMessage;
    }
    @Override
    public void run() {
        if(clientMessage.getMessageType() == MessageType.REORGANIZATION){
            RAState.isReorganizationDone.set(true);
            AppConfig.timestampedStandardPrint("|-|-|-|-|-|===> Reorganization completed: " + clientMessage.getSenderPort());
        }
    }
}
