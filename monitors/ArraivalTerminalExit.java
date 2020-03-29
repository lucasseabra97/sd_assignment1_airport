package monitors;
import interfaces.IArraivalTerminalExitPassenger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

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
    * Arraival Terminal Exit boolean to check if all can go home
	*/
    private boolean goingHome;
    /**
	* Arraival Terminal Exit shared Mem.
	* 
	* @param nrPassengers
	*
	*/
    public ArraivalTerminalExit(int nrPassengers) {
        rl = new ReentrantLock(true);
        waitingEnd = rl.newCondition();
        this.nrPassengers = nrPassengers;
        this.goingHome = false;
    }

    /**
	 * Puts passenger in {EXITING_THE_ARRIVAL_TERMINAL} if go home end cycle. 
	 * @param npassengers
	 * @return lastone
	 */
    @Override
    public boolean goHome(int npassengers) {
        rl.lock();
        try {
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
            return passengers;
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
            passengers ++;
        }catch(Exception ex){}
        finally{
            rl.unlock();
        }
   
    }
   



}
