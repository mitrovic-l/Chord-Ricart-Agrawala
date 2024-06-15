package servent.handler.failure;

import app.AppConfig;
import app.backup.Heartbeat;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;

public class HardFailConfirmHandler implements MessageHandler {
    private Message clientMessage;

    public HardFailConfirmHandler(Message clientMessage){
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if(clientMessage.getMessageType() == MessageType.HARD_FAIL_CONFIRM){
            int failedNodePort = Integer.parseInt(clientMessage.getMessageText());
            Heartbeat.getInstance().addFailedNodeConfirm(clientMessage.getSenderPort(), failedNodePort);
            synchronized (AppConfig.chordState) {
                AppConfig.chordState.handleNodeFailure(failedNodePort);
                AppConfig.timestampedStandardPrint("Node " + failedNodePort + " removed from the network.");
            }
        }
    }
}
