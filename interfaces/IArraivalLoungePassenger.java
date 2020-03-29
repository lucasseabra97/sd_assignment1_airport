package interfaces;

import model.*;
public interface IArraivalLoungePassenger {
	
	PassengerAction whatShouldIDO(int passengerID,Baggage[] bags,boolean jorneyEnds);
}