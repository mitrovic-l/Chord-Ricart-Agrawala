package app.mutex;

import app.AppConfig;
import app.ServentInfo;
import servent.message.mutex.RequestMessage;
import servent.message.util.MessageUtil;

import java.util.List;

public class RAMutex implements DistributedMutex {

    public RAMutex() {
    }

    @Override
    public void lock() {
        AppConfig.raState.sendRequestToAll(); // Pošalji zahtev svim čvorovima

        // Čekamo odgovore
        while (!AppConfig.raState.allRepliesReceived()) {
            try {
                Thread.sleep(100); // Provera svakih 100ms
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Kada primimo sve odgovore, možemo ući u kritičnu sekciju
        synchronized (RAState.lock) {
            AppConfig.raState.enterCriticalSection();
        }
    }

    @Override
    public void unlock() {
        synchronized (RAState.lock) {
            AppConfig.raState.exitCriticalSection();
            AppConfig.timestampedStandardPrint("Exiting critical section");
        }
    }
}