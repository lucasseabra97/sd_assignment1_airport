package monitors;
import model.*;
public class BaggageReclaimOffice {

    
    private GeneralRepository rep;

    public BaggageReclaimOffice(GeneralRepository rep){
        this.rep = rep;
    }

    /**
     *  report baggages missing
     * @param i
     * @param passengerID
     */
    public void reportMissingBags(int i, int passengerID) {
        rep.passengerState(passengerID, PassengerEnum.AT_THE_BAGGAGE_RECLAIM_OFFICE);
        rep.missingBags(i, passengerID);
    }

    
}
