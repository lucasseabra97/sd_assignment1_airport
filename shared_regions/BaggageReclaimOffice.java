package shared_regions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import commonInfra.*;
import entities.Passenger;
import interfaces.IBaggageReclaimOfficePassenger;
public class BaggageReclaimOffice implements IBaggageReclaimOfficePassenger{

    private final ReentrantLock rl;
    private List<Baggage> bagsList;
    private GeneralRepository rep;

    public BaggageReclaimOffice(GeneralRepository rep){
        this.rep = rep;
        rl = new ReentrantLock(true);
        bagsList = new ArrayList<>();
       
    }

    /**
     *  report baggages missing
     * @param i
     * @param passengerID
     */
    public void complain(ArrayList<Baggage> bags) {
        //rep.passengerState(passengerID, PassengerEnum.AT_THE_BAGGAGE_RECLAIM_OFFICE);
        //rep.missingBags(i, passengerID);
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
