package threads;

import model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import interfaces.*;
public class Passenger extends Thread {
	private PassengerEnum state;
	private final IArraivalLoungePassenger monitorAl;
	private final IBaggageCollectionPointPassenger monitorBc;
	private boolean jorneyEnds;
	private Baggage[] bags;
	private List<Baggage> bagsCollected;
	private int passengerID;
	private int nbags;

	
	public Passenger(int passengerID,Baggage[] bags,IArraivalLoungePassenger monitorAl,IBaggageCollectionPointPassenger monitorBc,boolean jorneyEnds) {
		this.passengerID = passengerID;
		this.bags = bags;
		this.jorneyEnds = jorneyEnds;
		this.monitorBc = monitorBc;
		this.monitorAl = monitorAl;
		this.state = PassengerEnum.AT_THE_DISEMBARKING_ZONE;	
		this.nbags = 0;
		this.bagsCollected = new ArrayList<Baggage>();
	}

	public boolean isJorneyEnds() {
		return this.jorneyEnds;
	}

	// public boolean getJorneyEnds() {
	// 	return this.jorneyEnds;
	// }

	public void setJorneyEnds(boolean jorneyEnds) {
		this.jorneyEnds = jorneyEnds;
	}

	public Baggage[] getBags() {
		return this.bags;
	}

	public void setBags(Baggage[] bags) {
		this.bags = bags;
	}

	public int getPassengerID() {
		return this.passengerID;
	}

	public void setPassengerID(int passengerID) {
		this.passengerID = passengerID;
	}
		
	@Override
    public void run() {   
        loop :while (true){
            switch(state){
				case AT_THE_DISEMBARKING_ZONE:
					System.out.printf("Passenger waiting AT_THE_DISEMBARKING_ZONE with : %d",bags.length);
					if(this.monitorAl.whatShouldIDO(this.bags, this.jorneyEnds)== PassengerAction.goHome)
						state = PassengerEnum.EXITING_THE_ARRIVAL_TERMINAL;
					else if(this.monitorAl.whatShouldIDO(this.bags, this.jorneyEnds)== PassengerAction.collecBag)
						state = PassengerEnum.AT_THE_LUGGAGE_COLLECTION_POINT;
					else if(this.monitorAl.whatShouldIDO(this.bags, this.jorneyEnds)== PassengerAction.takeABus)
						state = PassengerEnum.AT_THE_ARRIVAL_TRANSFER_TERMINAL;
					break;
				case AT_THE_LUGGAGE_COLLECTION_POINT:
					//Enquanto o passageiro tem malas entao vai busca las, no caso de as mesmas nao estarem no collectPoint
					// entao vai para o ReclaimOffice  
					System.out.printf("Passenger at LUGGAGE_COLLECTION_POINT" + "passengerID=%d bags=%d",this.passengerID , this.bags);
					int idx =0;
					// passa de array para arraylist. implementaçao mais facil...
					bagsCollected = Arrays.asList(bags); 
					while(nbags < bags.length){
						// ir buscar mala random ? 
						Baggage baggtoCollect = monitorBc.goCollectABag(idx);
						if(baggtoCollect == null)
						{
							if(nbags<bags.length)
								state = PassengerEnum.AT_THE_BAGGAGE_RECLAIM_OFFICE;
							else
								state = PassengerEnum.AT_THE_ARRIVAL_TRANSFER_TERMINAL;

						}
						if(bagsCollected.contains(baggtoCollect))
						{
							nbags++;
							if(nbags == bags.length)
								state = PassengerEnum.EXITING_THE_ARRIVAL_TERMINAL;
						
						}
						else{
							// bag nao tem o idx do passenger , tem de devolver ? // criar função BaggageCollectionPoint para devolver a mala caso não seja dele
						}		
						idx ++;
						
					}
					state = PassengerEnum.AT_THE_LUGGAGE_COLLECTION_POINT;
					break;
				case AT_THE_ARRIVAL_TRANSFER_TERMINAL:

					
					break;
				case ENTERING_THE_DEPARTURE_TERMINAL:
					
					break loop;
				case EXITING_THE_ARRIVAL_TERMINAL:
					System.out.printf("EXITING_THE_ARRIVAL_TERMINAL" + "passengerID=%d",this.passengerID);
					this.state = PassengerEnum.EXITING_THE_ARRIVAL_TERMINAL;
				
					break loop;
				default :
					//System.out.printf("ARRIVAL_TRANSFER_TERMINAL" + "passengerID=%d",this.passengerID);
					
            }
        	try {
                Thread.sleep(1000);
			} catch (Exception e) {}
			
        }
  
	}

	@Override
	public String toString() {
		return "{" +
			" state='" + state + "'" +
			", jorneyEnds='" + isJorneyEnds() + "'" +
			", passengerID='" + getPassengerID() + "'" +
			"}";
	}
	
}

