package shared_regions;

import java.util.List;
import java.util.ArrayList;

import commonInfra.*;

import java.util.concurrent.locks.ReentrantLock;

import interfaces.*;

import java.util.concurrent.locks.Condition;
public class BaggageCollectionPoint implements IBaggageCollectionPointPorter, IBaggageCollectionPointPassenger{
    private final ReentrantLock rl;
    private final Condition waitingBag;
    private List<Baggage> bags;

    public BaggageCollectionPoint(){
        rl = new ReentrantLock(true);
        waitingBag = rl.newCondition();
        bags = new ArrayList<>();
    }

    //para sinalizar os passageiros que ja nao ha malas, ou para irem ver 
    //se a mala que ele acabou de por e deles
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
    public Baggage goCollectABag(int idx) {
        rl.lock();
        try {
            waitingBag.await();
            if(idx >= this.bags.size()) {
                return null;
            }

            return this.bags.get(idx);
        } catch(Exception ex) {
            return null;
        } finally {
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
