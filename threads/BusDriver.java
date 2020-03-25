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
        loop :while(true){
            switch(state){
                case PARKING_AT_THE_ARRIVAL_TERMINAL: 
                    System.out.println("BUSDRIVER waiting for passengers...");                   
                    BusDriverAction busState = terminalTQBusDriver.hasDaysWorkEnded();  
                    if(busState == BusDriverAction.stayParked)
                    {
                        if(terminalTQBusDriver.annoucingBusBoarding()){
                            System.out.println("Starting jouney to terminal Transfer");
                            state= BusDriverEnum.DRIVING_FORWARD;
                        }
                        else if (busState == BusDriverAction.dayEnded){
                            break loop;
                        }

                        break;      
                    }
                case DRIVING_FORWARD:
                    System.out.println("BUSDRIVER driving Forward");



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