package servent.handler.files;

import app.AppConfig;
import app.mutex.RAState;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.files.FileRemoveMessage;

public class FileRemoveHandler implements MessageHandler {

    private FileRemoveMessage clientMessage;

    public FileRemoveHandler(Message clientMessage) {
        this.clientMessage = (FileRemoveMessage) clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() != MessageType.FILE_REMOVE) {
            AppConfig.timestampedErrorPrint("Received message is not of type REMOVE_FILE");
            return;
        }

        String filePath = clientMessage.getMessageText();
        if (filePath.isEmpty()) {
            AppConfig.timestampedErrorPrint("Received REMOVE_FILE message with empty file path");
            return;
        }

        try {
            synchronized (RAState.lock) {
                // Uklanjanje fajla iz Chord
                AppConfig.chordState.deleteFile(filePath);
            }
        } catch (NumberFormatException e) {
            AppConfig.timestampedErrorPrint("Invalid file path in REMOVE_FILE message: " + filePath);
        }
    }
}