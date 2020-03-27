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
                        System.out.println("Bus driver parked");
                        state = BusDriverEnum.PARKING_AT_THE_ARRIVAL_TERMINAL;    
                    }
                    else if (busState == BusDriverAction.goToDepartureTerminal){
                        System.out.println("Starting jouney to terminal Transfer");
                        terminalTQBusDriver.annoucingBusBoarding();
                        state = BusDriverEnum.DRIVING_FORWARD;
                        
                    }    
                   
                    else if (busState == BusDriverAction.dayEnded){
                        break loop;    
                    }
                    break;
                case DRIVING_FORWARD:
                    System.out.println("BUSDRIVER driving Forward");
                    state = BusDriverEnum.PARKING_AT_THE_DEPARTURE_TERMINAL;
                    break;
                case PARKING_AT_THE_DEPARTURE_TERMINAL:
                    System.out.println("BUS DRIVER AT THE DEPARTURE TERMINAL");
                    departureTerminalQBusDriver.parkTheBusAndLetPassOff();
                    System.out.println("PASSAGEIROS SAIRAM DO AUTOCARRO, A COMEÇAR VIAGEM DE VOLTA");
                    state = BusDriverEnum.DRIVING_BACKWARD;
                    break;
            
                case DRIVING_BACKWARD:
                    System.out.println("BUS DRIVER GOING BACKWARD");
                    state = BusDriverEnum.PARKING_AT_THE_ARRIVAL_TERMINAL;
                    break;


            }
        }
    }
}



