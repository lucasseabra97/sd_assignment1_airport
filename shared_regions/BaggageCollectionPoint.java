package shared_regions;

import java.util.List;
import java.util.ArrayList;

import commonInfra.*;
import entities.Passenger;

import java.util.concurrent.locks.ReentrantLock;

import interfaces.*;


import java.util.concurrent.locks.Condition;
/**
 * Baggage Collection Point shared memory region.
 * 
 * @author Lucas Seabra
 * @author Joao Monteiro
 */

public class BaggageCollectionPoint implements IBaggageCollectionPointPorter, IBaggageCollectionPointPassenger{
    /**
    * Baggage Collection Point for locking
    */
    private final ReentrantLock rl;
    /**
    * Condition variable for passengers until the porter places a bag in the belt or there are no more bags to collect
    */
    private final Condition waitingBag;
    /**
    * List of bags
    */
    private List<Baggage> bags;
    /**
    * Boolean variavle to check if there is bags to collect
    */
    private Boolean noMoreBags = false;
    /**
    * General Repository
    */
    private GeneralRepository rep;
   
    /**
    * Baggage Collection Point shared memory constructor
    * @param rep
    */
    public BaggageCollectionPoint(GeneralRepository rep){
        rl = new ReentrantLock(true);
        waitingBag = rl.newCondition();
        bags = new ArrayList<>();
        this.rep = rep;
      
    }

  
    /**
     * Porter carries a bag to the collection point. Wakes up passengers waiting for bags.
     */
    @Override
	public void carryItToAppropriateStore(Baggage bag) {
        rl.lock();
        try{
                rep.porterMoveBagToConveyorBelt();
                bags.add(bag);
                noMoreBags = false;
                waitingBag.signalAll();
            
        }catch(Exception ex){
        }finally{
            rl.unlock();
        }
    }
    /**
     * Passenger collects a bag from the baggage collection point.
     * Passengers wait until there are bags in the collection point. 
     * They area waken up by the porter, when he puts a bag in the collection point or when there are no more bags.
     * @param passengerBags The bags the passenger owns.
     */
    @Override
    public Baggage goCollectABag(ArrayList<Baggage> bagp) {
        rl.lock();
        try {
            Passenger passenger = (Passenger) Thread.currentThread();
            rep.passEnterLuggageCollectionPoint(passenger.getPassengerID());
            //System.out.println(bags +"  bomdia "+ bagp);

            //System.out.println(bags +"  bomdia "+ bagp);
            while(!noMoreBags){
                for(int i = 0; i < bags.size(); i++) {
                    Baggage tempbag =bags.get(i);
                    if(bagp.contains(tempbag)){
                            rep.passCollectBag(passenger.getPassengerID());
                            bags.remove(tempbag);
                        return tempbag;
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


    /**
     * Wakes passengers that are waiting for bags, when there are no more bags.
     */
    @Override
    public void noMoreBagsToCollect() {
        rl.lock();
        try {
            rep.porterNoMoreBags();
            noMoreBags = true;
            waitingBag.signalAll();
        } catch(Exception ex) {} 
        finally {
            rl.unlock();
        }
    }


    @Override
    public void resetState() {
        rl.lock();
        try {
            noMoreBags = false;
        } catch(Exception ex) {
        } finally {
            rl.unlock();
        }
    }
    
}
