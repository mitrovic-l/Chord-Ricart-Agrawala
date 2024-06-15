package servent.handler.files;

import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;

public class FileDeniedHandler implements MessageHandler {
    private Message clientMessage;
    public FileDeniedHandler(Message clientMessage){
        this.clientMessage = clientMessage;
    }
    @Override
    public void run() {
        if(clientMessage.getMessageType().equals(MessageType.FILE_DENIED)){
            System.out.println("- - -> Can't view file (file is private and you are not their friend): " + clientMessage.getMessageText());
        }
    }
}
