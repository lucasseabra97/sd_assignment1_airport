package shared_regions;

import commonInfra.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.ArrayList;
import java.util.List;

import interfaces.*;

import main.global;

import java.util.Random;


public class ArraivalLounge implements IArraivalLoungePassenger , IArraivalLoungePorter{
	/**
    * Arraival Lounge Variable for locking 
	*/
	private final ReentrantLock rl;

	/**
    * Arraival Lounge Memory for all baggages
    */
	private ArrayList<Baggage> memBag;
	/**
    * Arraival Lounge Conditional variable for waiting for porter
    */
	private final Condition cPorter;

	/**
    * Arraival Lounge Conditional variable for waiting for plane
    */
	private final Condition waitForPlane;
	/**
    * Arraival Lounge variable to count Passengers
    */
	private int nPassengers=0;
	/**
    * Arraival Lounge determine maxPassengers for Bus
    */
	private int maxPassengers;
 
	/**
    * Arraival Lounge boolean for end of cycle
    */
	private boolean dayEnded=false;

	/**
	 * Arraival Lounge to collect bag
	 * 
	 */
	private boolean collect = false;
	/**
	 *  General Information Repository
	 */

	private boolean porterAvailable=false;

	 
	private List<List<Baggage>> bagsPerFlight;

	private Random rand;
	private GeneralRepository rep;
	/**
    * 
	*  	Arraival Lounge shared Mem.
    * 	@param maxPassengers
    */
	public ArraivalLounge( List<List<Baggage>> bagsPerFlight) {
		//this.maxPassengers = maxPassengers;
		this.memBag = new ArrayList<Baggage>();
		rl = new ReentrantLock(true);
		this.maxPassengers = global.NR_PASSENGERS;
		cPorter = rl.newCondition();
		waitForPlane = rl.newCondition();
		rand = new Random();
		this.bagsPerFlight = bagsPerFlight;
		
	}


	/**
	 * Returns passenger action in {@link commonInfra.PassengerAction} state. <p/>
	 * Disembarks passenger and notifies Porter
	 * @param bags
	 * @param jorneyEnds
	 * @return PassengerAction
	 */


	@Override
    public int whatShouldIDO(Boolean goHome) {
        rl.lock();
        try {

            // if(goHome) repository.addFinalDestinations();
            // else repository.addTransit();

            while(!porterAvailable)
                cPorter.await();

            nPassengers++;
           // if(nPassengers == 1) repository.startNextFlight(bagsPerFlight.get(0).size());

            //Passenger passenger = (Passenger) Thread.currentThread();
            //int bags = passenger.getFlightBags();
            //repository.passengerInit(PassengerEnum.AT_THE_DISEMBARKING_ZONE, bags, goHome ? "FDT" : "TRF", passenger.getPassengerId());

            if(nPassengers == maxPassengers) {
                collect = true;
                nPassengers = 0;
                waitForPlane.signal();
            }

            return nPassengers;
            
        } catch(Exception ex) {  
            return 0;   
        } finally {
            rl.unlock();
        }
    }

	/**
	 * Porter in {@link commonInfra.PorterEnum.WAITING_FOR_A_PLANE_TO_LAND} state
	 * @return dayEnded
	 * 
	 */	
	@Override
    public boolean takeARest() {
        rl.lock();
        try {

            porterAvailable = true;
            cPorter.signalAll();

            //repository.porterWaitingLanding();
            while(!collect && !dayEnded) {
				System.out.println("BOMDIA");
				waitForPlane.await();
				
            }

            memBag = new ArrayList<>();
            if(!dayEnded){
                List<Baggage> flightBags = bagsPerFlight.remove(0);
                for(int b = 0; b < flightBags.size(); b++) {
                    memBag.add(flightBags.get(b));
                    //repository.addBag();
                   
                }
            }
            
            porterAvailable = false;
            collect = false;
            return !dayEnded;

        } catch(Exception ex) {  
            return true;  
        } finally {
            rl.unlock();
        }
    }
	/**
	 * Porter in {@link  commonInfra.PorterEnum.AT_THE_PLANES_HOLDWAITING_FOR_PLANE_TO_LAND} state
	 * @return bag
	 * 
	 */	
	@Override
	public Baggage tryToCollectABag(){
		//rep.porterState(PorterEnum.AT_THE_PLANES_HOLD);
		rl.lock();
		try{
			if(memBag.size() > 0) {
				Baggage tempbagg = memBag.remove(0);
				//System.out.println(memBag.size());
				return tempbagg;
			}
			else 
				return null;
		}
		catch(Exception ex){
			return null;
		}
		finally{
		rl.unlock();
		}
        
	}

	@Override 
    public void endOfDay() {
        rl.lock();
        try {
            dayEnded = true;
            waitForPlane.signal();
        } catch(Exception ex) {}
        finally {
            rl.unlock();
        }
    }

}
	
