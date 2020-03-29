package entities;

import commonInfra.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import interfaces.*;
public class Passenger extends Thread {
	/**
	* State for Passenger
	*/
	private PassengerEnum state;
	/**
	* Next action of Passenger
	*/
	private PassengerAction action;
	/**
	* verifying if passenger has another flight
	*/
	private boolean jorneyEnds;
	/**
    * Interface Passenger Arraival Lounge
    */
	private final IArraivalLoungePassenger monitorAl;
	/**
    * Interface Passenger Baggage Collection Point 
    */
	private final IBaggageCollectionPointPassenger monitorBc;
	/**
    * Interface Passenger Terminal Exit  
    */
	private final IArraivalTerminalExitPassenger monitorAe;
	/**
    * Interface Passenger Arraival Terminal Transfer Quay  
    */
	private final IArraivalTerminalTransferQPassenger monitorTTQ;
	/**
    * Interface Passenger Departure Terminal Transfer  
    */
	private final IDepartureTerminalTransferQPassenger monitorDTTQ;
	/**
    * Interface Passenger Departure Terminal Entrance  
    */
	private final IDepartureTerminalEntrancePassenger monitorDEP;
	/**
    * Array of Passenger bags  
    */
	private Baggage[] bags;
	/**
    * List of bags collected by Passenger 
    */
	private List<Baggage> bagsCollected;
	/**
    * Passenger ID  
    */
	private int passengerID;
	/**
    * Number of Passenger bags  
    */
	private int nbags;
	 /**
    * Terminate Passenger cicle if yes
    */
	private boolean end;
	/**
    * Number of Passengers in Arrival terminal Exit
    */
	private int npassengersAe;
	/**
    * Number of Passengers in Departure Terminal Entrance
    */
	private int npassengersDEP;

	/**
    * 
    *  Passenger entity 
    * 
    * @author João Monteiro 
    * @author Lucas Seabra
    */
	public Passenger(int passengerID,Baggage[] bags,IArraivalLoungePassenger monitorAl,IBaggageCollectionPointPassenger monitorBc, IArraivalTerminalExitPassenger monitorAe, IArraivalTerminalTransferQPassenger monitorTTQ , IDepartureTerminalTransferQPassenger monitorDTTQ, IDepartureTerminalEntrancePassenger monitorDEP, boolean jorneyEnds ) {
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
		this.monitorDEP = monitorDEP;
		this.end = true;
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
        while (end){
            switch(state){
				case AT_THE_DISEMBARKING_ZONE:
					System.out.printf("Passenger:%d -> waiting AT_THE_DISEMBARKING_ZONE with : %d bags and jouneyEnds:%b \n",this.passengerID,bags.length,this.jorneyEnds);
					action = this.monitorAl.whatShouldIDO(this.passengerID,this.bags, this.jorneyEnds);
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


				case AT_THE_BAGGAGE_RECLAIM_OFFICE:
					
					
					break;
				case AT_THE_ARRIVAL_TRANSFER_TERMINAL:
					System.out.printf("Passenger:%d -> AT THE ARRIVAL TRANSFER TERMINAL WATING FOR BUS \n",this.passengerID);
					monitorTTQ.takeABus(this.passengerID);
					monitorTTQ.enterTheBus(this.passengerID);
					System.out.printf("Passenger:%d -> IN THE BUS \n",this.passengerID);
					state = PassengerEnum.TERMINAL_TRANSFER;
					break;
				case TERMINAL_TRANSFER:
					System.out.printf("Passenger:%d -> AT THE TERMINAL TRANSFER \n",this.passengerID);
					monitorDTTQ.waitRide();
					state = PassengerEnum.AT_THE_DEPARTURE_TRANSFER_TERMINAL;
					break;

				case AT_THE_DEPARTURE_TRANSFER_TERMINAL:
					System.out.printf("Passenger:%d -> LEAVING THE BUS \n",this.passengerID);
					monitorDTTQ.leaveTheBus();
					state = PassengerEnum.ENTERING_THE_DEPARTURE_TERMINAL;
					break;
				case ENTERING_THE_DEPARTURE_TERMINAL:

					System.out.printf("Passenger:%d -> PREPARING NEXT FLIGHT \n",this.passengerID);
					npassengersDEP = monitorDEP.nPassengersDepartureTEntrance();
					monitorDEP.syncPassenger();
					if(monitorDEP.prepareNextLeg(npassengersDEP)){
						monitorAe.awakePassengers();
						monitorDEP.awakePassengers();
						
					}
					
					end = false;
					break ;
				case EXITING_THE_ARRIVAL_TERMINAL:
					System.out.printf("Passenger:%d -> EXITING_THE_ARRIVAL_TERMINAL \n",this.passengerID);
					npassengersAe = monitorAe.nPassengersDepartureAT();
					monitorAe.syncPassenger();
					if(monitorAe.goHome(npassengersAe)){
						monitorAe.awakePassengers();
						monitorDEP.awakePassengers();
						
					}
					end = false;
					break;
				default :
					//System.out.printf("ARRIVAL_TRANSFER_TERMINAL" + "passengerID=%d",this.passengerID);
					
			}
			
        	try {
                Thread.sleep(50);
			} catch (Exception e) {}
			
        }
		System.out.println("Passenger Ended : "+this.toString());
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

