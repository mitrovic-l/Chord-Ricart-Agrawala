package servent.handler.friends;

import app.AppConfig;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;

public class FriendRequestHandler implements MessageHandler {
    private Message clientMessage;
    public FriendRequestHandler(Message clientMessage){
        this.clientMessage = clientMessage;
    }
    @Override
    public void run() {
        if(clientMessage.getMessageType() == MessageType.FRIEND_REQUEST){
            AppConfig.chordState.addFriend(clientMessage.getSenderPort());
            AppConfig.timestampedStandardPrint("New friend: " + clientMessage.getSenderPort());
        }
    }
}
