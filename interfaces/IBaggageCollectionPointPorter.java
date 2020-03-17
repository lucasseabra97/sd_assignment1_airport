package interfaces;
import model.Baggage;


public interface IBaggageCollectionPointPorter{
    void carryItToAppropriateStore(Baggage bag);
    void noMoreBagsToCollect();
}