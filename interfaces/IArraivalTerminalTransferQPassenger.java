
package interfaces;
import entities.*;

public interface IArraivalTerminalTransferQPassenger{
    void takeABus(int passengerID);
    void enterTheBus(int passengerID);
    Boolean endOfDay();
}