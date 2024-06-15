package app.backup;

import app.AppConfig;
import app.ServentInfo;
import servent.message.backup.GiveBackupMessage;
import servent.message.util.MessageUtil;

import java.util.Map;

public class Backupper implements Runnable{
    private static final int BACKUP_INTERVAL_MS = AppConfig.BACKUP_INTEVRAL;
    private volatile boolean isRunning = true;

    @Override
    public void run() {
        while(isRunning){
            performBackup();
        }
    }

    // Metoda za izvršenje bekapa
    private void performBackup() {
        try {
            Thread.sleep(BACKUP_INTERVAL_MS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        ServentInfo successor = AppConfig.chordState.getSuccessor();
        if(successor == null) return;

        int successorPort = successor.getListenerPort();
        Map<Integer, MySharedFile> backupData = AppConfig.chordState.getValueMap();

        GiveBackupMessage backupMessage = new GiveBackupMessage(AppConfig.myServentInfo.getListenerPort(), successorPort, backupData);
        MessageUtil.sendMessage(backupMessage);
    }

    public void stop() {
        isRunning = false;
    }
}