package app.mutex;

import app.AppConfig;
import app.ServentInfo;
import servent.message.mutex.ReplyMessage;
import servent.message.mutex.RequestMessage;
import servent.message.util.MessageUtil;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicBoolean;

public class RAState {
    private int myRequestNumber = 0; // Lokalni broj zahteva
    private long myTimestamp = System.currentTimeMillis(); // Timestamp za lokalni zahtev
    private Map<Integer, Integer> requestNumbers = new ConcurrentHashMap<>(); // Brojevi zahteva za svaki čvor
    private Map<Integer, Long> timestamps = new ConcurrentHashMap<>(); // Timestamp-ovi zahteva za svaki čvor
    private Set<Integer> pendingReplies = new ConcurrentSkipListSet<>(); // Čvorovi od kojih čekamo odgovore
    public static final Object lock = new Object(); // Synchronization object
    public static volatile AtomicBoolean isReorganizationDone = new AtomicBoolean(false); // Flag for reorganization status
    private boolean inCriticalSection = false;

    /**
     * Povećava lokalni broj zahteva i vraća novi broj.
     */
    public void incrementRequestNumber() {
        myRequestNumber++;
        myTimestamp = System.currentTimeMillis(); // Ažuriranje timestamp-a pri svakom povećanju broja zahteva
    }

    /**
     * Vraća trenutni lokalni broj zahteva.
     */
    public int getMyRequestNumber() {
        return myRequestNumber;
    }

    /**
     * Vraća trenutni timestamp za lokalni zahtev.
     */
    public long getMyTimestamp() {
        return myTimestamp;
    }

    /**
     * Postavlja broj zahteva za dati čvor.
     */
    public void setRequestNumber(int nodePort, int requestNumber) {
        requestNumbers.put(nodePort, requestNumber);
    }

    /**
     * Vraća broj zahteva za dati čvor.
     */
    public int getRequestNumber(int nodePort) {
        return requestNumbers.getOrDefault(nodePort, 0);
    }

    /**
     * Postavlja timestamp za dati čvor.
     */
    public void setTimestamp(int nodePort, long timestamp) {
        timestamps.put(nodePort, timestamp);
    }

    /**
     * Vraća timestamp za dati čvor.
     */
    public long getTimestamp(int nodePort) {
        return timestamps.getOrDefault(nodePort, 0L);
    }

    /**
     * Dodaje čvor u listu očekivanih odgovora.
     */
    public void addPendingReply(int nodePort) {
        pendingReplies.add(nodePort);
    }

    /**
     * Uklanja čvor iz liste očekivanih odgovora.
     */
    public void removePendingReply(int nodePort) {
        pendingReplies.remove(nodePort);
    }

    /**
     * Proverava da li su svi odgovori primljeni.
     */
    public boolean allRepliesReceived() {
        return pendingReplies.isEmpty();
    }

    /**
     * Prosleđuje zahtev sledećem čvoru u mreži.
     */
    public void forwardRequest(int senderPort, int requestNumber, long timestamp, int requesterPort) {
        ServentInfo nextNode = AppConfig.chordState.getNextNodeForKey(requesterPort);
        if (nextNode != null && nextNode.getListenerPort() != AppConfig.myServentInfo.getListenerPort()) {
            RequestMessage requestMessage = new RequestMessage(
                    AppConfig.myServentInfo.getListenerPort(),
                    nextNode.getListenerPort(),
                    requestNumber,
                    timestamp,
                    requesterPort
            );
            MessageUtil.sendMessage(requestMessage);
        }
    }

    /**
     * Šalje odgovor čvoru koji je zahtevao pristup.
     */
    public void sendReply(int requesterPort) {
        ReplyMessage replyMessage = new ReplyMessage(AppConfig.myServentInfo.getListenerPort(), requesterPort);
        MessageUtil.sendMessage(replyMessage);
        AppConfig.timestampedStandardPrint("Sent reply to: " + requesterPort);
    }

    /**
     * Oznaka ulaska u kritičnu sekciju.
     */
    public synchronized void enterCriticalSection() {
        AppConfig.timestampedStandardPrint("Entering critical section");
        inCriticalSection = true;
    }

    /**
     * Oznaka izlaska iz kritične sekcije.
     */
    public synchronized void exitCriticalSection() {
        AppConfig.timestampedStandardPrint("Exiting critical section");
        inCriticalSection = false;
    }

    /**
     * Proverava da li je čvor trenutno u kritičnoj sekciji.
     */
    public synchronized boolean isInCriticalSection() {
        return inCriticalSection;
    }

    /**
     * Šalje zahtev za kritičnu sekciju svim čvorovima u mreži.
     */
    public void sendRequestToAll() {
        int myPort = AppConfig.myServentInfo.getListenerPort();
        incrementRequestNumber();
        int requestNumber = getMyRequestNumber();
        long timestamp = getMyTimestamp();
        AppConfig.timestampedStandardPrint("Sending request for critical section with number: " + requestNumber + " and timestamp: " + timestamp);

        for (ServentInfo serventInfo : AppConfig.chordState.getAllNodeInfo()) {
            if (serventInfo.getListenerPort() == myPort) continue; // Skip ourselves

            RequestMessage requestMessage = new RequestMessage(myPort, serventInfo.getListenerPort(), requestNumber, timestamp, myPort);
            MessageUtil.sendMessage(requestMessage);
            addPendingReply(serventInfo.getListenerPort());
        }
    }
}
