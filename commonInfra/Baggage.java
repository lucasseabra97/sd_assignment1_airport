package commonInfra;


public class Baggage
{
	private int passengerID;
	private boolean journeyEnds;

	public Baggage(int passengerID, boolean journeyEnds){
		this.passengerID = passengerID;
		this.journeyEnds = journeyEnds;
	}

	public int getPassengerID() {
		return this.passengerID;
	}

	public void setPassengerID(int passengerID) {
		this.passengerID = passengerID;
	}

	public boolean isJourneyEnds() {
		return this.journeyEnds;
	}

	public boolean getJourneyEnds() {
		return this.journeyEnds;
	}

	public void setJourneyEnds(boolean journeyEnds) {
		this.journeyEnds = journeyEnds;
	}
	

	@Override
	public String toString() {
		return "{" +
			" passengerID='" + getPassengerID() + "'" +
			", journeyEnds='" + isJourneyEnds() + "'" +
			"}";
	}

}