package servent.message.backup;

import app.backup.MySharedFile;
import servent.message.BasicMessage;
import servent.message.MessageType;

import java.util.HashMap;
import java.util.Map;

public class GiveBackupMessage extends BasicMessage {
    private Map<Integer, MySharedFile> backup;
    public GiveBackupMessage(int senderPort, int receiverPort, Map<Integer, MySharedFile> backup) {
        super(MessageType.GIVE_BACKUP, senderPort, receiverPort);
        this.backup = new HashMap<>(backup);
    }

    public Map<Integer, MySharedFile> getBackup() {
        return backup;
    }
}
