package shared_regions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import commonInfra.*;
import entities.Passenger;
import interfaces.IBaggageReclaimOfficePassenger;

/**
 * Baggage Reclaim Office shared memory region.
 * 
 * @author Lucas Seabra
 * @author Joao Monteiro
 */
public class BaggageReclaimOffice implements IBaggageReclaimOfficePassenger{
    /**
     * Baggage Reclaim Office variable for locking
     */
    private final ReentrantLock rl;
    /**
     * List of bags that where missed
     */
    private List<Baggage> bagsList;
    /**
     * General Repository
     */
    private GeneralRepository rep;
    /**
     * Baggage Reclaim Office constructor
     * @param rep
     */
    public BaggageReclaimOffice(GeneralRepository rep){
        this.rep = rep;
        rl = new ReentrantLock(true);
        bagsList = new ArrayList<>();
       
    }

    /**
     * Makes a complaint.
     * @param bags Set of bags lost.
     */
    public void complain(ArrayList<Baggage> bags) {
      
        rl.lock();
        try {
            Passenger passenger = (Passenger) Thread.currentThread();
            rep.passComplain(passenger.getPassengerID());

            for(Baggage b : bags) {
                this.bagsList.add(b);
            }
        } catch(Exception ex) {
        } finally {
            rl.unlock();
        }

    }

    
}
