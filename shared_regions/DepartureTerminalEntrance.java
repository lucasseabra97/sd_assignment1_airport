package shared_regions;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import interfaces.IDepartureTerminalEntrancePassenger;
import main.global;

public class DepartureTerminalEntrance implements IDepartureTerminalEntrancePassenger{
    /**
    * Departure Terminal Entrance Variable for locking 
	*/
    private final ReentrantLock rl;
    /**
    * Departure Terminal Entrance Conditional variable for waiting all passengers to exit
	*/
    private final Condition waitingEnd;
    /**
    *  Departure Terminal Entrance variable to count passengers
	*/
    private int passengers = 0;
     /**
    * Departure Terminal Entrance Conditional variable for nº off all passengers
	*/
    private int nrPassengers;
    /**
    * Departure Terminal Entrance boolean to check if all can go home
	*/
    private boolean goingHome;

    /**
	* Departure Terminal Entrance shared Mem.
	* 
	* @param nrPassengers
	*
	*/
  
    public DepartureTerminalEntrance(int nrPassengers) {
        rl = new ReentrantLock(true);
        waitingEnd = rl.newCondition();
        this.nrPassengers = global.NR_PASSENGERS;
        this.goingHome = false;
    }

    /**
	*  Increments passengers at Departure Terminal Entrance
	*  
    */
    @Override 
    public void syncPassenger(){
        rl.lock();
        try{
            goingHome = true;
            passengers ++;

        }catch(Exception ex){}
        finally{
            rl.unlock();
        }
       
        
    }
     /**
	* Wakes upp all passengers and end Cycle  Departure Terminal Entrance
	*
	*/
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

    /**
	*  Returns all passengers in Departure Terminal Entrance
	*  @return passengers
	*/
    @Override
    public int nPassengersDepartureTEntrance(){
        rl.lock();
        try {
            return this.passengers;
        } catch (Exception e) {  return passengers;}
        finally{
            rl.unlock();
        }
        
    }
    /**
	 * Puts passenger in {ENTERING_THE_DEPARTURE_TERMINAL} if prepare Next Flight end cycle. 
	 * @param npassengers
	 * @return lastone
	 */        
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
