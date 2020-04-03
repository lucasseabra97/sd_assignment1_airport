package interfaces;

import java.util.List;

import commonInfra.*;


public interface IBaggageCollectionPointPassenger{
    Baggage goCollectABag(List<Baggage> ibagp);
    void resetState();

}