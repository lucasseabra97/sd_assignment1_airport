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
    private boolean goingHome;
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
        this.goingHome = false;
        this.rep = rep;
    }

    /**
	*  Increments passengers at Departure Terminal Entrance
	*  
    */
    @Override 
    public void syncPassenger(){
        rl.lock();
        try{
            goingHome = false;
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
            int tmp = passengers; 
            return tmp;
        } catch (Exception e) {  return passengers;      }
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
            Passenger passenger = (Passenger) Thread.currentThread();
            rep.passPrepareNextLeg(passenger.getPassengerID());

            if(lastone){
                goingHome = true;
                waitingEnd.signalAll();
            }
            while(!goingHome){
                waitingEnd.await();
            }
            passengers --;
            return lastone;
            
        } catch(Exception ex) {return false;}
        finally {
            rl.unlock();
        }
    }
}
