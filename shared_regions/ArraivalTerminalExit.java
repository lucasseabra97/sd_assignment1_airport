package shared_regions;

import interfaces.IArraivalTerminalExitPassenger;
import main.global;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import entities.Passenger;

/**
 * Arraival Terminal shared memory region.
 * 
 * @author Lucas Seabra
 * @author Joao Monteiro
 */

public class ArraivalTerminalExit implements IArraivalTerminalExitPassenger{
    /**
    * Arraival Terminal Exit Variable for locking 
	*/
    private final ReentrantLock rl;
    /**
    * Arraival Terminal Exit Conditional variable for waiting all passengers to exit
	*/
    private final Condition waitingEnd;
    /**
    * Arraival Terminal Exit variable to count passengers
	*/
    private int passengers = 0;
    /**
    * Arraival Terminal Exit Conditional variable for nº off all passengers
	*/
    private int nrPassengers;
    /**
     * General Repository
     */
    private GeneralRepository rep;
    /**
    * Arraival Terminal Exit boolean to check if all can go home
    */
    private boolean goingHome=false;
    /**
	* Arraival Terminal Exit shared Memory constructor
	* 
	* @param nrPassengers
	* @param rep
	*/
    public ArraivalTerminalExit(int nrPassengers , GeneralRepository rep) {
        rl = new ReentrantLock(true);
        waitingEnd = rl.newCondition();
        this.rep=rep;
      
        this.nrPassengers = global.NR_PASSENGERS;
        this.goingHome = false;
    }

    /**
	 * Puts passenger in {@link  commonInfra.PassengerEXITING_THE_ARRIVAL_TERMINAL} if go home end cycle. 
	 * @param npassengers
	 * @return lastone
	 */
    @Override
    public boolean goHome(int npassengers) {
        rl.lock();
        try {

            Passenger passenger = (Passenger) Thread.currentThread();
            rep.passGoHome(passenger.getPassengerID());

            boolean lastone = npassengers+passengers == nrPassengers;
            if(lastone ){
                goingHome = true;
                waitingEnd.signalAll();
            }
            while(!goingHome){
                waitingEnd.await();
            
            }
            passengers--;

            return lastone;
        } catch(Exception ex) {return false;}
        finally {
            rl.unlock();
        }
    }
    /**
	* Wakes upp all passengers and end Cycle Arraival Terminal Exit
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
	*  Returns all passengers in Arraival Terminal Exit
	*  @return passengers
	*/
    @Override
    public int nPassengersDepartureAT(){
        rl.lock();
        try{
            int temp = passengers;
            return temp;
        }catch(Exception ex){ return passengers;}
        finally{
            rl.unlock();
        }
    /**
	*  Increments passengers at Arraival Terminal Exit
	*  
    */
    }
    @Override
    public void syncPassenger(){
        rl.lock();    
        try{
            goingHome=false;
            passengers++;
            
        }catch(Exception ex){}
        finally{
            rl.unlock();
        }
   
    }
   



}
