package threads;
import model.*;
import interfaces.*;


public class Porter extends Thread{
    private PorterEnum state;
    private Baggage bag;
    private final IArraivalLoungePorter monitorAl;
    private final IBaggageCollectionPointPorter monitorBCP;

    public Porter(IArraivalLoungePorter monitorAl,IBaggageCollectionPointPorter monitorBCP){
        this.monitorAl = monitorAl;
        this.monitorBCP = monitorBCP;
        this.state = PorterEnum.WAITING_FOR_A_PLANE_TO_LAND; //initial state
    }

    @Override
    public void run() { 
        while(true){
            switch(state){
                case WAITING_FOR_A_PLANE_TO_LAND:
                    System.out.println("Porter waiting for a plain to land...");
                    if (monitorAl.takeARest()) state = PorterEnum.AT_THE_PLANES_HOLD;
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
                    if(bag!=null ){
                        System.out.println("PORTER LOADING BAGGAGE"+ bag);
                        monitorBCP.carryItToAppropriateStore(bag);
                    }
            }
            try {
                Thread.sleep(1000);
            } catch (Exception e) {}
        }
    }

    
    @Override
    public String toString() {
        return "{" +
            "Porter's state='" + state + "'" +
            "}";
    }

}