package threads;

import model.*;
import interfaces.*;
public class Passenger extends Thread {
	private PassengerEnum state;
	private final IArraivalLoungePassenger monitorAl;
	private boolean jorneyEnds;
	private Baggage[] bags;
	private int passengerID;

	
	public Passenger(int passengerID,Baggage[] bags,IArraivalLoungePassenger monitorAl,boolean jorneyEnds) {
		this.passengerID = passengerID;
		this.bags = bags;
		this.jorneyEnds = jorneyEnds;
		this.monitorAl = monitorAl;
		this.state = PassengerEnum.AT_THE_DISEMBARKING_ZONE;	
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
        while (true){
            switch(this.monitorAl.whatShouldIDO(this.bags,this.jorneyEnds)){
				case goHome:
					//System.out.printf("EXITING_THE_ARRIVAL_TERMINAL" + "passengerID=%d",this.passengerID);
					this.state = PassengerEnum.EXITING_THE_ARRIVAL_TERMINAL;
				case collecBag:
					//System.out.printf("LUGGAGE_COLLECTION_POINT" + "passengerID=%d",this.passengerID);
					state = PassengerEnum.AT_THE_LUGGAGE_COLLECTION_POINT;
				default :
					//System.out.printf("ARRIVAL_TRANSFER_TERMINAL" + "passengerID=%d",this.passengerID);
					state = PassengerEnum.AT_THE_ARRIVAL_TRANSFER_TERMINAL;
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

