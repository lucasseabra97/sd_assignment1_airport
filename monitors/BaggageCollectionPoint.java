package monitors;

import java.util.List;
import java.util.ArrayList;

import model.Baggage;

import java.util.concurrent.locks.ReentrantLock;

import interfaces.*;

import java.util.concurrent.locks.Condition;
public class BaggageCollectionPoint implements IBaggageCollectionPointPorter {
    private final ReentrantLock rl;
    private final Condition waitingBag;
    private List<Baggage> bags;

    public BaggageCollectionPoint(){
        rl = new ReentrantLock(true);
        waitingBag = rl.newCondition();
        bags = new ArrayList<>();
    }
    @Override
	public void carryItToAppropriateStore(Baggage bag) {
        rl.lock();
        try{
            if(bag == null || bag.getJourneyEnds()){
                bags.add(bag);
                waitingBag.signalAll();
            }
        }catch(Exception ex){
        }finally{
            rl.unlock();
        }
    }
    
    @Override
    public void noMoreBagsToCollect() {
        rl.lock();
        try {
            waitingBag.signalAll();
        } catch(Exception ex) {} 
        finally {
            rl.unlock();
        }
    }
}
