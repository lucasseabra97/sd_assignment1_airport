package interfaces;
public interface IDepartureTerminalEntrancePassenger {
   
    boolean prepareNextLeg(int npassengers);
    void syncPassenger();
    int nPassengersDepartureTEntrance();
    void awakePassengers();
}