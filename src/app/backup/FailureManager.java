package app.backup;

import app.AppConfig;
import app.ServentInfo;
import servent.message.check.TickMessage;
import servent.message.chord.RemoveNodeMessage;
import servent.message.failure.HardFailMessage;
import servent.message.util.MessageUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class FailureManager {

    /**
     * Sends a message to the bootstrap node to remove a specified node.
     *
     * @param port the port of the node to be removed
     */
    public void removeNodeSendBootstrap(int port) {
        int bsPort = AppConfig.BOOTSTRAP_PORT;
        try (Socket bsSocket = new Socket("localhost", bsPort);
             PrintWriter bsWriter = new PrintWriter(bsSocket.getOutputStream())) {
            bsWriter.write("Remove " + port);
            bsWriter.flush();
            AppConfig.timestampedStandardPrint("Sending bootstrap message to remove node...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Broadcasts a message to all nodes to remove a failed node.
     *
     * @param failedNodePort the port of the failed node to be removed
     */
    public void broadcastRemoveNodeMessage(int failedNodePort) {
        for (ServentInfo node : AppConfig.chordState.getAllNodeInfo()) {
            RemoveNodeMessage removeNodeMessage = new RemoveNodeMessage(AppConfig.myServentInfo.getListenerPort(), node.getListenerPort(), failedNodePort);
            MessageUtil.sendMessage(removeNodeMessage);
        }
    }

    /**
     * Broadcasts a message to all nodes that a node has failed.
     *
     * @param failedNodePort the port of the failed node
     */
    public void broadcastFailedNodeMessage(int failedNodePort) {
        for (ServentInfo node : AppConfig.chordState.getAllNodeInfo()) {
            HardFailMessage hardFailMessage = new HardFailMessage(AppConfig.myServentInfo.getListenerPort(), node.getListenerPort(), failedNodePort);
            MessageUtil.sendMessage(hardFailMessage);
        }
    }

    /**
     * Sends a Tick message to a specified node to check its status.
     *
     * @param heart the node to which the Tick message is sent
     * @param respondedFlag the flag indicating if the node has responded
     */
    public void sendTick(ServentInfo heart, boolean respondedFlag) {
        if (heart != AppConfig.myServentInfo) {
            TickMessage tickMessage = new TickMessage(AppConfig.myServentInfo.getListenerPort(), heart.getListenerPort());
            MessageUtil.sendMessage(tickMessage);
        } else {
            AppConfig.timestampedStandardPrint("Don't send Tick message to myself");
            respondedFlag = true;
        }
    }

    /**
     * Pauses the execution for a specified amount of time.
     *
     * @param ms the amount of time to pause in milliseconds
     */
    public void wait(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
