package threads;

import interfaces.*;
import model.*;

public class BusDriver extends Thread{
    private int numLimitPassenger;
    private BusDriverEnum state;
    private final IArraivalTerminalTransferQBusDriver terminalTQBusDriver;
    private final IDepartureTerminalTransferQBusDriver departureTerminalQBusDriver;

    public BusDriver(IArraivalTerminalTransferQBusDriver terminalTQBusDriver,IDepartureTerminalTransferQBusDriver departureTerminalQBusDriver){
        this.state = state.PARKING_AT_THE_ARRIVAL_TERMINAL;
        this.terminalTQBusDriver = terminalTQBusDriver;
        this.departureTerminalQBusDriver = departureTerminalQBusDriver;
    }


    @Override
    public void run(){
        while(true){
            switch(state){
                case PARKING_AT_THE_ARRIVAL_TERMINAL: 
                    System.out.println("BUSDRIVE waiting for passengers...");
                   // BusDriverAction busState = terminalTQBusDriver.hasDaysWorkEnded();  
            }
        }
    }
}








































// @Override
//     public void run(){

//         while(loop){
//             char choice = arrivalTermTransfQuay.hasDaysWorkEnded();

//             if(choice == 'W') {
//                 nPassengers = arrivalTermTransfQuay.annoucingBusBoarding();			
//                 goToDepartureTerminal();
//                 state = BusDriverState.PARKING_AT_THE_DEPARTURE_TERMINAL;
//                 departureTermTransfQuay.parkTheBusAndLetPassengerOff(nPassengers);
//                 goToArrivalTerminal();
//                 state = BusDriverState.PARKING_AT_THE_ARRIVAL_TERMINAL;
//                 arrivalTermTransfQuay.parkTheBus();
//             }else if(choice == 'E'){
//                 loop = false;
//             }
//         }
//     }

    
//     /** 
//      * @param state
//      */
//     public void setState(BusDriverState state) {
//         this.state = state;
//     }
    
//     /** 
//      * @return BusDriverState
//      */
//     public BusDriverState getBDriverState() {
//         return this.state;
//     }

//     void goToDepartureTerminal(){
//         try {
//             state = BusDriverState.DRIVING_FORWARD; 
//             Thread.sleep(50);
//         } catch (Exception e) {}

//     }
//     void goToArrivalTerminal(){
//         try {
//             state = BusDriverState.DRIVING_FORWARD; 
//             Thread.sleep(50);
//         } catch (Exception e) {}

//     }