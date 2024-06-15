package servent.handler.backup;

import app.AppConfig;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.backup.GiveBackupMessage;

public class BackupGetHandler implements MessageHandler {
    private GiveBackupMessage clientMessage;

    public BackupGetHandler(Message clientMessage){
        this.clientMessage = (GiveBackupMessage) clientMessage;
    }
    @Override
    public void run() {
        if(clientMessage.getMessageType() == MessageType.GIVE_BACKUP){
            AppConfig.chordState.addBackup(clientMessage.getBackup());
        }
    }
}
