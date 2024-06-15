package servent.handler.mutex;

import app.AppConfig;
import app.mutex.RAState;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.mutex.ReplyMessage;

public class ReplyMessageHandler implements MessageHandler {

    private ReplyMessage clientMessage;

    public ReplyMessageHandler(Message clientMessage) {
        this.clientMessage = (ReplyMessage) clientMessage;
    }

    @Override
    public void run() {
        int senderPort = clientMessage.getSenderPort();
        synchronized (RAState.lock) {
            AppConfig.raState.removePendingReply(senderPort);
            AppConfig.timestampedStandardPrint("Received reply from: " + senderPort);
        }
    }
}