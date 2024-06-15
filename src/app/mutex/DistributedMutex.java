package app.mutex;

public interface DistributedMutex {
    void lock();
    void unlock();
}
