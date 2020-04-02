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
    * Interface Porter Baggage Collection Point 
    */
    private final IBaggageCollectionPointPorter monitorBCP;
     /**
    * Interface Porter Temporary Storage Area 
    */
    private final ITemporaryStorageAreaPorter monitorTSA;

    
    /**
    * 
    *  Porter entity 
    * 
    * @author João Monteiro 
    * @author Lucas Seabra
    */
    public Porter(IArraivalLoungePorter monitorAl,IBaggageCollectionPointPorter monitorBCP , ITemporaryStorageAreaPorter monitorTSA){
        this.monitorAl = monitorAl;
        this.monitorBCP = monitorBCP;
        this.monitorTSA = monitorTSA;
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
                    }
                    break;
                case AT_THE_PLANES_HOLD:    
                    System.out.println("Porter AT_THE_PLANES_HOLD");
                    bag = monitorAl.tryToCollectABag();
                    if(bag == null ){
                        System.out.println("NO MORE BAGS");
                        monitorBCP.noMoreBagsToCollect();
                        state =PorterEnum.WAITING_FOR_A_PLANE_TO_LAND;
                       
                    }
                    else{
                        if(bag.getJourneyEnds())
                            state =  PorterEnum.AT_THE_LUGGAGE_BELT_CONVEYOR;
                        else 
                            state = PorterEnum.AT_THE_STOREROOM;

                    }   
                    break;
                case AT_THE_LUGGAGE_BELT_CONVEYOR:
                    System.out.println("PORTEIRO A CARREGAR MALA PARA A MESA: " + bag);
                    monitorBCP.carryItToAppropriateStore(bag);
                    state = PorterEnum.AT_THE_PLANES_HOLD;
                    break;
                case AT_THE_STOREROOM:
                    System.out.println("PORTEIRO A CARREGAR MALA PARA ESPAÇO TEMPORARIO: " + bag);
                    monitorTSA.carryItToAppropriateStore(bag);
                    state = PorterEnum.AT_THE_PLANES_HOLD;
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