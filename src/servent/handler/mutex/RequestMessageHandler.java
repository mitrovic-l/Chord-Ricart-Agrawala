package servent.handler.mutex;

import app.AppConfig;
import app.ServentInfo;
import app.mutex.RAState;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.mutex.ReplyMessage;
import servent.message.mutex.RequestMessage;
import servent.message.util.MessageUtil;

public class RequestMessageHandler implements MessageHandler {

    private RequestMessage clientMessage;

    public RequestMessageHandler(Message clientMessage) {
        this.clientMessage = (RequestMessage) clientMessage;
    }

    @Override
    public void run() {
        int senderPort = clientMessage.getSenderPort();
        int requestNumber = clientMessage.getRequestNumber();
        long timestamp = clientMessage.getTimestamp();
        int requesterPort = clientMessage.getRequesterPort();

        synchronized (RAState.lock) {
            // Update the request number and timestamp for the sender
            int myRequestNumber = AppConfig.raState.getRequestNumber(requesterPort);
            long myTimestamp = AppConfig.raState.getTimestamp(requesterPort);
            AppConfig.raState.setRequestNumber(requesterPort, Math.max(requestNumber, myRequestNumber));
            AppConfig.raState.setTimestamp(requesterPort, Math.max(timestamp, myTimestamp));

            // Send reply if not in critical section or request has lower timestamp
            if (!AppConfig.raState.isInCriticalSection() ||
                    (timestamp < AppConfig.raState.getMyTimestamp() ||
                            (timestamp == AppConfig.raState.getMyTimestamp() && requesterPort < AppConfig.myServentInfo.getListenerPort()))) {

                AppConfig.raState.sendReply(requesterPort);
            } else {
                // Queue the request if in critical section and request timestamp is higher
                AppConfig.raState.addPendingReply(requesterPort);
            }
        }
    }
}
