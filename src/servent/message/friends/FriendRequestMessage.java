package servent.message.friends;

import servent.message.BasicMessage;
import servent.message.MessageType;

public class FriendRequestMessage extends BasicMessage {
    public FriendRequestMessage(int senderPort, int receiverPort) {
        super(MessageType.FRIEND_REQUEST, senderPort, receiverPort);
    }
}
