package shared_regions;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import entities.Passenger;
import interfaces.IDepartureTerminalEntrancePassenger;
import main.global;


/**
 * Departure Terminal Entrance shared memory region.
 * 
 * @author Lucas Seabra
 * @author Joao Monteiro
 */

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
    private boolean goingHome =true;
    /**
     * General Repository
     */
    private GeneralRepository rep;
    /**
	* Departure Terminal Entrance shared Memory constructor
	* 
	* @param nrPassengers
	* @param rep
	*/
  
    public DepartureTerminalEntrance(int nrPassengers , GeneralRepository rep) {
        rl = new ReentrantLock(true);
        waitingEnd = rl.newCondition();
        this.nrPassengers = global.NR_PASSENGERS;
        this.rep = rep;
    }

    /**
	*  Increments passengers at Departure Terminal Entrance
	*  
    */
    @Override 
    public void syncPassenger(){
        rl.lock();
        goingHome = true;
        passengers++;
        rl.unlock(); 
        
    }
     /**
	* Wakes upp all passengers and end Cycle  Departure Terminal Entrance
	*
	*/
    @Override
    public void awakePassengers(){
        rl.lock();
        goingHome = false;
        waitingEnd.signalAll();
        rl.unlock();
    }

    /**
	*  Returns all passengers in Departure Terminal Entrance
	*  @return passengers
	*/
    @Override
    public int nPassengersDepartureTEntrance(){
        rl.lock();
        int passengers = this.passengers;
        rl.unlock();
        return passengers;
        
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


            boolean lastPassenger = npassengers + passengers == nrPassengers;

            Passenger passenger = (Passenger) Thread.currentThread();
            rep.passPrepareNextLeg(passenger.getPassengerID());
            System.out.println("-> "+ lastPassenger);
            if(lastPassenger) {
                goingHome = false;
                waitingEnd.signalAll();
            }

            while(goingHome) {
                waitingEnd.await();
            }

            passengers--;

            return lastPassenger;

        } catch(Exception ex) {
            return false;
        }
        finally {
            rl.unlock();
        }
    }
}
