package ai.megaworks.ema.listener;

public interface Publisher {
    void attach(Subscriber subscriber);

    void notifyAll(Long id);
}
