package entities;
import commonInfra.*;
import interfaces.*;


public class Porter extends Thread{
    /**
	* State for Porter
    */
    private PorterEnum state;
	/**
	* Next action of Porter
	*/
    private boolean end;
    /**
	* Next bag for Porter collect
	*/
    private Baggage bag;
    /**
    * Interface Porter Arraival Lounge  
    */
    private final IArraivalLoungePorter monitorAl;
    /**
    * Interface Passenger Baggage Collection Point 
    */
    private final IBaggageCollectionPointPorter monitorBCP;
    

    /**
    * 
    *  Porter entity 
    * 
    * @author João Monteiro 
    * @author Lucas Seabra
    */
    public Porter(IArraivalLoungePorter monitorAl,IBaggageCollectionPointPorter monitorBCP){
        this.monitorAl = monitorAl;
        this.monitorBCP = monitorBCP;
        this.state = PorterEnum.WAITING_FOR_A_PLANE_TO_LAND; //initial state
        this.end = true;
    }

    @Override
    public void run() { 
        while(end){
            switch(state){
                case WAITING_FOR_A_PLANE_TO_LAND:
                    System.out.println("Porter waiting for a plain to land...");
                    
                    if (monitorAl.takeARest())
                        state = PorterEnum.AT_THE_PLANES_HOLD;
                    else {
                        System.out.println("End of day for porter");
                        end = false;
                        break;
                    }
                    break;
                case AT_THE_PLANES_HOLD:    
                    System.out.println("Porter AT_THE_PLANES_HOLD");
                    bag = monitorAl.tryToCollectABag();
                    if(bag == null || bag.getJourneyEnds())
                        state =PorterEnum.AT_THE_LUGGAGE_BELT_CONVEYOR ;
                    else
                        state =  PorterEnum.AT_THE_PLANES_HOLD;
                    break;
                case AT_THE_LUGGAGE_BELT_CONVEYOR:
                    System.out.println("AT_THE_LUGGAGE_BELT_CONVEYOR");
                    if(bag == null) {
                        System.out.println("No more bags to collect");
                        monitorBCP.noMoreBagsToCollect();
                    } else {
                        System.out.println("PORTEIRO A CARREGAR MALA PARA A MESA: " + bag);
                        monitorBCP.carryItToAppropriateStore(bag);
                    }
                    state = bag == null ? PorterEnum.WAITING_FOR_A_PLANE_TO_LAND : PorterEnum.AT_THE_PLANES_HOLD;
                    break;
            }
            try {
                Thread.sleep(50);
            } catch (Exception e) {}
        }
        System.out.println("Porter Ended");
    }
 

    
    @Override
    public String toString() {
        return "{" +
            "Porter's state='" + state + "'" +
            "}";
    }

}