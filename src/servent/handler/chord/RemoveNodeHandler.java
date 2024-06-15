package servent.handler.chord;

import app.AppConfig;
import app.mutex.RAState;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.chord.RemoveNodeMessage;
import servent.message.util.MessageUtil;

public class RemoveNodeHandler implements MessageHandler {
    private Message clientMessage;

    public RemoveNodeHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() == MessageType.REMOVE_NODE) {
            if (clientMessage.getSenderPort() == AppConfig.myServentInfo.getListenerPort()) {
                AppConfig.timestampedStandardPrint("YAY. Everyone has removed failed node.");
                return;
            }
            int failedNodePort = Integer.parseInt(clientMessage.getMessageText());

            synchronized (RAState.lock) {
                AppConfig.chordState.handleNodeFailure(failedNodePort);
                AppConfig.timestampedStandardPrint("NOTICED FAILIURE: Node " + failedNodePort + " failed and removed! Telling other nodes to remove it.");

                RemoveNodeMessage removeNodeMessage = new RemoveNodeMessage(clientMessage.getSenderPort(), AppConfig.chordState.getNextNodePort(), failedNodePort);
                MessageUtil.sendMessage(removeNodeMessage);
            }
        }
    }
}
