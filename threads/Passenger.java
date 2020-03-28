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
	private final IArraivalTerminalExitPassenger monitorAe;
	private final IArraivalTerminalTransferQPassenger monitorTTQ;
	private final IDepartureTerminalTransferQPassenger monitorDTTQ;
	private Baggage[] bags;
	private List<Baggage> bagsCollected;
	private int passengerID;
	private int nbags;

	// Monitors(interfaces) are passed as arguments
	public Passenger(int passengerID,Baggage[] bags,IArraivalLoungePassenger monitorAl,IBaggageCollectionPointPassenger monitorBc, IArraivalTerminalExitPassenger monitorAe, IArraivalTerminalTransferQPassenger monitorTTQ , IDepartureTerminalTransferQPassenger monitorDTTQ, boolean jorneyEnds ) {
		this.passengerID = passengerID;
		this.bags = bags;
		this.jorneyEnds = jorneyEnds;
		this.monitorBc = monitorBc;
		this.monitorAl = monitorAl;
		this.state = PassengerEnum.AT_THE_DISEMBARKING_ZONE;	
		this.nbags = 0;
		this.bagsCollected = new ArrayList<Baggage>();
		this.monitorAe = monitorAe;
		this.monitorTTQ = monitorTTQ;
		this.monitorDTTQ = monitorDTTQ;
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
					System.out.printf("Passenger:%d -> waiting AT_THE_DISEMBARKING_ZONE with : %d bags and jouneyEnds:%b \n",this.passengerID,bags.length,this.jorneyEnds);
					PassengerAction action = this.monitorAl.whatShouldIDO(this.bags, this.jorneyEnds);
					if(action == PassengerAction.goHome){
						state = PassengerEnum.EXITING_THE_ARRIVAL_TERMINAL;
					}	
					else if(action == PassengerAction.collecBag){
						state = PassengerEnum.AT_THE_LUGGAGE_COLLECTION_POINT;
					}	
					else{
						state = PassengerEnum.AT_THE_ARRIVAL_TRANSFER_TERMINAL;
					}
					// else if(this.monitorAl.whatShouldIDO(this.bags, this.jorneyEnds)== PassengerAction.takeABus)
						
					break;
				case AT_THE_LUGGAGE_COLLECTION_POINT:
					//Enquanto o passageiro tem malas entao vai busca las, no caso de as mesmas nao estarem no collectPoint
					// entao vai para o ReclaimOffice  
					System.out.printf("Passenger:%d -> LUGGAGE_COLLECTION_POINT" + "bags=%d \n",this.passengerID , this.bags.length);
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
						idx ++;	
					}
					break;
				case AT_THE_ARRIVAL_TRANSFER_TERMINAL:
					System.out.printf("Passenger:%d -> AT THE ARRIVAL TRANSFER TERMINAL WATING FOR BUS \n",this.passengerID);
					monitorTTQ.takeABus(this);
					monitorTTQ.enterTheBus();
					System.out.printf("Passenger:%d -> IN THE BUS \n",this.passengerID);
					state = PassengerEnum.TERMINAL_TRANSFER;
					break;
				case TERMINAL_TRANSFER:
					System.out.printf("Passenger:%d -> AT THE TERMINAL TRANSFER \n",this.passengerID);
					monitorDTTQ.waitRide();
					state = PassengerEnum.AT_THE_DEPARTURE_TRANSFER_TERMINAL;
					break;

				case AT_THE_DEPARTURE_TRANSFER_TERMINAL:
				System.out.printf("Passenger:%d -> LEVING THE BUS \n",this.passengerID);
					monitorDTTQ.leaveTheBus();
					state = PassengerEnum.ENTERING_THE_DEPARTURE_TERMINAL;
					break;
				case ENTERING_THE_DEPARTURE_TERMINAL:
					System.out.printf("Passenger:%d -> PREPARING NEXT FLIGHT \n",this.passengerID);
					monitorAe.prepareNextLeg();
					break loop;
				case EXITING_THE_ARRIVAL_TERMINAL:
					System.out.printf("Passenger:%d -> EXITING_THE_ARRIVAL_TERMINAL \n",this.passengerID);
					this.state = PassengerEnum.EXITING_THE_ARRIVAL_TERMINAL;
					monitorAe.goHome();
					break loop;
				default :
					//System.out.printf("ARRIVAL_TRANSFER_TERMINAL" + "passengerID=%d",this.passengerID);
					
            }
        	try {
                Thread.sleep(50);
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

