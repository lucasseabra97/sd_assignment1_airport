package interfaces;
import commonInfra.*;


public interface IBaggageCollectionPointPorter{
    void carryItToAppropriateStore(Baggage bag);
    void noMoreBagsToCollect();
}