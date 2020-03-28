package monitors;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import interfaces.IDepartureTerminalEntrancePassenger;

public class DepartureTerminalEntrance implements IDepartureTerminalEntrancePassenger{
    private final ReentrantLock rl;
    private final Condition waitingEnd;
    private int passengers = 0;
    private int nrPassengers;



  
    public DepartureTerminalEntrance(int nrPassengers) {
        rl = new ReentrantLock(true);
        waitingEnd = rl.newCondition();
        this.nrPassengers = nrPassengers;
    }

    @Override
    public void prepareNextLeg() {
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
