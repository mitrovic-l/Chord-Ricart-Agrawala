package servent.handler.failure;

import app.AppConfig;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.failure.HardFailConfirmMessage;
import servent.message.failure.HardFailMessage;
import servent.message.util.MessageUtil;

public class HardFailHandler implements MessageHandler {

    private HardFailMessage clintMessage;
    public HardFailHandler(Message clientMessage){
        this.clintMessage = (HardFailMessage) clientMessage;
    }
    @Override
    public void run() {
        if(clintMessage.getMessageType() == MessageType.HARD_FAIL){
            int failedNodePort = Integer.parseInt(clintMessage.getMessageText());
            AppConfig.chordState.addFailedNode(failedNodePort);
            HardFailConfirmMessage message = new HardFailConfirmMessage(AppConfig.myServentInfo.getListenerPort(), clintMessage.getSenderPort(), failedNodePort);
            MessageUtil.sendMessage(message);
        }
    }
}
