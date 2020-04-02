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
    private Boolean noMoreBags = false;


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
            
                bags.add(bag);
                noMoreBags = false;
                waitingBag.signalAll();
            
        }catch(Exception ex){
        }finally{
            rl.unlock();
        }
    }
    @Override
    public Baggage goCollectABag(List<Baggage> bagp) {
        rl.lock();
        try {

            
            while(!noMoreBags){
                System.out.println("BOMDIAAAAAAAAAAAAAAA");
                for(int i = 0; i < bags.size(); i++) {
                    for (Baggage baggage : bags) {
                        if(bagp.contains(baggage)){
                            
                            bags.remove(baggage);
                            return baggage;
                        }
                    }
                }
                waitingBag.await();

            }
            return null;
    
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
            noMoreBags = true;
            waitingBag.signalAll();
        } catch(Exception ex) {} 
        finally {
            rl.unlock();
        }
    }
    
}
