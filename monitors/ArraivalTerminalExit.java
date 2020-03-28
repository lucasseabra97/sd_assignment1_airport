package monitors;
import interfaces.IArraivalTerminalExitPassenger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ArraivalTerminalExit implements IArraivalTerminalExitPassenger{

    private final ReentrantLock rl;
    private final Condition waitingEnd;
    
    private int passengers = 0;
    private int nrPassengers;

  
    public ArraivalTerminalExit(int nrPassengers) {
        rl = new ReentrantLock(true);
        waitingEnd = rl.newCondition();
        this.nrPassengers = nrPassengers;
    }
    @Override
    public void goHome() {
        rl.lock();
        try {
            passengers++;
            if(passengers == nrPassengers) {
                waitingEnd.signalAll();
            } else {
                waitingEnd.await();
            }
        } catch(Exception ex) {}
        finally {
            rl.unlock();
        }
    }

   



}
