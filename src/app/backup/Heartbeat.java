package app.backup;

import app.AppConfig;
import app.ServentInfo;
import app.mutex.DistributedMutex;
import servent.message.failure.SoftFailMessage;
import servent.message.util.MessageUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Heartbeat implements Runnable {

    private static final int HEARTBEAT_INTERVAL = 1000;
    private static final int SOFT_FAIL = AppConfig.SOFT_FAIL_INTERVAL;
    private static final int HARD_FAIL = AppConfig.HARD_FAIL_INTERVAL;

    private volatile boolean predecessorHeartResponded = false;
    private volatile boolean successorHeartResponded = false;
    private volatile boolean working = true;

    private Map<Integer, List<Integer>> failedNodeConfirmMap = new ConcurrentHashMap<>();
    private DistributedMutex mutex;

    private static Heartbeat instance;
    private FailureManager failureManager;

    static {
        instance = new Heartbeat();
    }

    private Heartbeat() {
        failureManager = new FailureManager();
    }

    public static Heartbeat getInstance() {
        return instance;
    }

    @Override
    public void run() {
        while (working) {
            failureManager.wait(HEARTBEAT_INTERVAL);
//            AppConfig.timestampedStandardPrint("Vrtim heartbeat while");
            resetRespondFlags();

            ServentInfo predecessorHeart = AppConfig.chordState.getPredecessor();
            ServentInfo successorHeart = AppConfig.chordState.getSuccessor();

            if (predecessorHeart == null || successorHeart == null) {
//                if (predecessorHeart == null)
//                    AppConfig.timestampedStandardPrint("Prethodnik je null.");
//                else if (successorHeart == null)
//                    AppConfig.timestampedStandardPrint("Sledbenik je null.");
                continue;
            }

            logHeartbeat(predecessorHeart, successorHeart);
            sendTickMessages(predecessorHeart, successorHeart);

            failureManager.wait(SOFT_FAIL);
            if (heartsResponded()) continue;

            handleSoftFail(predecessorHeart, predecessorHeartResponded, "predecessorHeart");
            handleSoftFail(successorHeart, successorHeartResponded, "successorHeart");

            failureManager.wait(HARD_FAIL);
            if (heartsResponded()) continue;

            if (!working) return;

            handleHardFail(predecessorHeart, predecessorHeartResponded, AppConfig.chordState.getPredecessor(), true);
            handleHardFail(successorHeart, successorHeartResponded, AppConfig.chordState.getSuccessor(), false);
        }
    }

    private void resetRespondFlags() {
        predecessorHeartResponded = false;
        successorHeartResponded = false;
    }

    private void logHeartbeat(ServentInfo predecessorHeart, ServentInfo successorHeart) {
        AppConfig.timestampedStandardPrint("My nodes to check: " + predecessorHeart + ", " + successorHeart);
    }

    private void sendTickMessages(ServentInfo predecessorHeart, ServentInfo successorHeart) {
        failureManager.sendTick(predecessorHeart, predecessorHeartResponded);
        failureManager.sendTick(successorHeart, successorHeartResponded);
    }

    private boolean heartsResponded() {
        return predecessorHeartResponded && successorHeartResponded;
    }

    private void handleSoftFail(ServentInfo heart, boolean heartResponded, String heartName) {
        if (!heartResponded) {
            logSoftFail(heart);
            if (heart != getExpectedHeart(heartName)) {
                setHeartRespondedFlag(heartName);
            } else {
                sendSoftFailMessage(heart);
            }
        }
    }

    private void handleHardFail(ServentInfo heart, boolean heartResponded, ServentInfo expectedHeart, boolean handleFail) {
        if (!heartResponded && heart.getListenerPort() == expectedHeart.getListenerPort()) {
            if (handleFail) processHardFail(heart);
            failureManager.broadcastFailedNodeMessage(heart.getListenerPort());
        }
    }

    private void logSoftFail(ServentInfo heart) {
        AppConfig.timestampedStandardPrint(heart.getChordId() + " with port " + heart.getListenerPort() + " SOFT FAILED");
    }

    private ServentInfo getExpectedHeart(String heartName) {
        return heartName.equals("predecessorHeart") ? AppConfig.chordState.getPredecessor() : AppConfig.chordState.getSuccessor();
    }

    private void setHeartRespondedFlag(String heartName) {
        if (heartName.equals("predecessorHeart")) predecessorHeartResponded = true;
        if (heartName.equals("successorHeart")) successorHeartResponded = true;
    }

    private void sendSoftFailMessage(ServentInfo heart) {
        SoftFailMessage softFailMessage = new SoftFailMessage(AppConfig.myServentInfo.getListenerPort(), heart.getListenerPort());
        MessageUtil.sendMessage(softFailMessage);
    }

    private void processHardFail(ServentInfo heart) {
        AppConfig.timestampedStandardPrint("Uso sam u proccess hard fail. Cekam lock");
        mutex.lock(); // Zaključavamo mutex za pristup kritičnoj sekciji
        try {
            AppConfig.timestampedStandardPrint(heart.getChordId() + " with port " + heart.getListenerPort() + " HARD FAILED");
            AppConfig.timestampedStandardPrint("Handling hard failure");
            AppConfig.chordState.handleNodeFailure(heart.getListenerPort());
            failureManager.removeNodeSendBootstrap(heart.getListenerPort());
            failureManager.broadcastRemoveNodeMessage(heart.getListenerPort());
            waitForEveryoneToRemoveNode(heart.getListenerPort());
            AppConfig.timestampedStandardPrint("Other nodes handled node failure!");
        } finally {
            mutex.unlock(); // Otključavamo mutex nakon što smo završili sa kritičnom sekcijom
        }
    }

    private void waitForEveryoneToRemoveNode(int failedNodePort) {
        AppConfig.timestampedStandardPrint("I am waiting for everyone to remove failed node");
        while (working) {
            if (allNodesConfirmed(failedNodePort)) return;
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private boolean allNodesConfirmed(int failedNodePort) {
        Set<Integer> failedNodes = AppConfig.chordState.getFailedNodes();
        List<Integer> allNodes = new ArrayList<>();
        for (ServentInfo node : AppConfig.chordState.getAllNodeInfo()) {
            allNodes.add(node.getListenerPort());
        }
        allNodes.removeAll(failedNodeConfirmMap.getOrDefault(failedNodePort, new ArrayList<>()));
        allNodes.removeAll(failedNodes);
        return allNodes.isEmpty();
    }

    public void heartResponded(int heartPort) {
        AppConfig.timestampedStandardPrint("Heart " + heartPort + " responded");
        if (heartPort == AppConfig.chordState.getPredecessor().getListenerPort()) {
            predecessorHeartResponded = true;
        }
        if (heartPort == AppConfig.chordState.getSuccessor().getListenerPort()) {
            successorHeartResponded = true;
        }
    }

    public void stop() {
        working = false;
    }

    public void setMutex(DistributedMutex mutex) {
        this.mutex = mutex;
    }

    public void addFailedNodeConfirm(int senderPort, int failedNodePort) {
        AppConfig.timestampedStandardPrint("Node " + senderPort + " confirmed failed node: " + failedNodePort);
        failedNodeConfirmMap.compute(failedNodePort, (key, existingList) -> {
            if (existingList == null) {
                existingList = new ArrayList<>();
            }
            existingList.add(senderPort);
            return existingList;
        });
    }

    public boolean isWorking() {
        return working;
    }
}
