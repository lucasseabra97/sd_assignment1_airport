package shared_regions;
import commonInfra.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.ArrayList;
import interfaces.*;
import java.util.Random;


public class ArraivalLounge implements IArraivalLoungePassenger , IArraivalLoungePorter,IOpArrivalLounge{
	/**
    * Arraival Lounge Variable for locking 
	*/
	private final ReentrantLock rl;
	/**
    * Arraival Lounge Memory for all baggages
    */
	private ArrayList<Baggage> memBag;
	/**
    * Arraival Lounge Conditional variable for waking up porter
    */
	private final Condition cPorter;
	/**
    * Arraival Lounge variable to count Passengers
    */
	private int nPassengers=0;
	/**
    * Arraival Lounge determine maxPassengers for Bus
    */
	private int maxPassengers;
	/**
    * Arraival Lounge boolean for Porter collect bags 
    */
	private boolean collectBaggs; 
	/**
    * Arraival Lounge boolean for end of cycle
    */
	private boolean dayEnded;
	/**
	 *  General Information Repository
	 */

	private Random rand;
	private GeneralRepository rep;
	/**
    * 
	*  	Arraival Lounge shared Mem.
    * 	@param maxPassengers
    */
	public ArraivalLounge(int maxPassengers) {
		this.maxPassengers = maxPassengers;
		this.memBag = new ArrayList<Baggage>();
		rl = new ReentrantLock(true);
		collectBaggs = false;
		dayEnded = false;
		cPorter = rl.newCondition();
		rand = new Random();
	}


	/**
	 * Returns passenger action in {@link commonInfra.PassengerAction} state. <p/>
	 * Disembarks passenger and notifies Porter
	 * @param bags
	 * @param jorneyEnds
	 * @return PassengerAction
	 */
	@Override
	public PassengerAction whatShouldIDO(int passengerID,Baggage[] bags,boolean jorneyEnds) {
		rl.lock();
        try {
			//rep.passengerState(passengerID, PassengerEnum.AT_THE_DISEMBARKING_ZONE, jorneyEnds, bags.length);
			
			//Thread.sleep(100);
			for(int i=0;i<bags.length;i++){
				boolean val = rand.nextInt(75)==0;
				if(!val)
					this.memBag.add(bags[i]);
			}			
			nPassengers++;
			if(nPassengers == maxPassengers){
				//System.out.printf("total bags = %d \n",this.memBag.size());
				//System.out.println(nPassengers);
				collectBaggs=true;
				cPorter.signal();	
				nPassengers =0;	
			}
        } catch (Exception ex) {}
        finally {
            rl.unlock();
		}
		if(jorneyEnds)
			return bags.length ==0 ? PassengerAction.goHome : PassengerAction.collecBag;
		return PassengerAction.takeABus;
	}
	/**
	 * Porter in {@link commonInfra.PorterEnum.WAITING_FOR_A_PLANE_TO_LAND} state
	 * @return dayEnded
	 * 
	 */	
	@Override
	public  boolean takeARest(){
		rl.lock();
		try{

			//rep.porterState(PorterEnum.WAITING_FOR_A_PLANE_TO_LAND);
			if(dayEnded)
                return false;
            cPorter.await();
            return !dayEnded;
		
		}catch(Exception ex){
			return true;
		}
		finally{
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
            cPorter.signal();
        } catch(Exception ex) {}
        finally {
            rl.unlock();
        }
    }

}
	
