package monitors;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import interfaces.IDepartureTerminalEntrancePassenger;

public class DepartureTerminalEntrance implements IDepartureTerminalEntrancePassenger{
    private final ReentrantLock rl;
    private final Condition waitingEnd;
    private int passengers = 0;
    private int nrPassengers;
    private boolean goingHome;


  
    public DepartureTerminalEntrance(int nrPassengers) {
        rl = new ReentrantLock(true);
        waitingEnd = rl.newCondition();
        this.nrPassengers = nrPassengers;
        this.goingHome = false;
    }


    @Override 
    public void syncPassenger(){
        rl.lock();
        try{
            
            passengers ++;

        }catch(Exception ex){}
        finally{
            rl.unlock();
        }
       
        
    }

    @Override
    public void awakePassengers(){
        rl.lock();
        try {
            goingHome = true;
            waitingEnd.signalAll();
        } catch (Exception e) {}
        finally{
            rl.unlock();
        }
    }

    @Override
    public int nPassengersDepartureTEntrance(){
        rl.lock();
        try {
            return passengers;
        } catch (Exception e) {  return passengers;      }
        finally{
            rl.unlock();
        }
        
    }

    @Override
    public boolean prepareNextLeg(int npassengers) {
        rl.lock();
        try {
            boolean lastone = npassengers + passengers == nrPassengers -1;
            if(lastone){
                goingHome = true;
                waitingEnd.signalAll();
            }
            while(!goingHome){
                waitingEnd.await();
            }
            npassengers --;
            return lastone;
            
        } catch(Exception ex) {return false;}
        finally {
            rl.unlock();
        }
    }
}
