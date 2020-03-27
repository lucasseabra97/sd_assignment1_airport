package monitors;
import model.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.ArrayList;
import interfaces.*;
import java.util.Random;


public class ArraivalLounge implements IArraivalLoungePassenger , IArraivalLoungePorter{
	private final ReentrantLock rl;
	private ArrayList<Baggage> memBag;
	private final Condition cPorter;
	private int nPassengers=0;
	private int maxPassengers;
	private boolean collectBaggs; 
	//private final Random random = new Random();

	public ArraivalLounge(int maxPassengers) {
		this.maxPassengers = maxPassengers;
		this.memBag = new ArrayList<Baggage>();
		rl = new ReentrantLock(true);
		collectBaggs = false;
		cPorter = rl.newCondition();
	}
	@Override
	public PassengerAction whatShouldIDO(Baggage[] bags,boolean jorneyEnds) {
		
        try {
			rl.lock();
			//Thread.sleep(100);
			for(int i=0;i<bags.length;i++){
				//System.out.println(bags[i]);
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

	@Override
	public  boolean takeARest(){
		try{
			rl.lock();
			while(collectBaggs==false)
				cPorter.await();
				collectBaggs=false;
				return true;
		
			}catch(Exception ex){}
			finally{
				rl.unlock();
			}
		return false;
	}	
	@Override
	public Baggage tryToCollectABag(){
		if(memBag.size() > 0) {
			//System.out.println(memBag.size());
            return memBag.remove(0);
        }
        return null;
	}


}
	
