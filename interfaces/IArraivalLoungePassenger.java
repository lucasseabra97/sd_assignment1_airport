package interfaces;

import model.*;
public interface IArraivalLoungePassenger {
	
	PassengerAction whatShouldIDO(Baggage[] bags,boolean jorneyEnds);
}