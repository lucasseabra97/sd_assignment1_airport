package interfaces;

import java.util.ArrayList;


import commonInfra.*;


public interface IBaggageCollectionPointPassenger{
    Baggage goCollectABag(ArrayList<Baggage> ibagp);
    void resetState();

}