package interfaces;

import commonInfra.*;
public interface IArraivalLoungePassenger {
	
	PassengerAction whatShouldIDO(int passengerID,Baggage[] bags,boolean jorneyEnds);
}