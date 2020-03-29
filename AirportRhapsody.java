
import java.lang.management.MonitorInfo;
import java.util.Random;
import interfaces.*;
import threads.*;
import monitors.*;
import model.*;
public class AirportRhapsody {
    public static void main(String[] args){
        System.out.println("----------AirportRhapsody---------");
        final Random random = new Random();
        final int maxPassengers = 6;
        final int busSize = 3;
        Baggage [] bags = null;
        Passenger p[] = new Passenger[maxPassengers];
        ArraivalLounge arraivalLounge = new ArraivalLounge(maxPassengers);
        BaggageCollectionPoint baggageCollectionPoint = new BaggageCollectionPoint();
        ArraivalTerminalExit arraivalTerminalExit = new ArraivalTerminalExit(maxPassengers);
        ArraivalTerminalTransferQuay arraivalTerminalTransferQuay = new ArraivalTerminalTransferQuay(busSize);
        DepartureTerminalTransferQuay departureTerminalTransferQuay = new DepartureTerminalTransferQuay();
        DepartureTerminalEntrance departureTerminalEntrance = new DepartureTerminalEntrance(maxPassengers);
        Porter porter = new Porter(arraivalLounge, baggageCollectionPoint);
        porter.start();
        BusDriver busdriver = new BusDriver(arraivalTerminalTransferQuay , departureTerminalTransferQuay,busSize);
        busdriver.start();

        Time time = new Time(arraivalTerminalTransferQuay, arraivalLounge);
        time.start();

        for(int i=0;i<maxPassengers;i++){
                //random nbags gerados para cada passageiro 
            int nBags = random.nextInt(3);
            boolean jorneyEnds = random.nextBoolean();
            bags = new Baggage[nBags];
            //a ideia e para cada passageiro atualizar a info da mala associada ao mesmo pq 
            //se o passageiro id0 tiver 2 malas entao a mala 0 e 1 tem associadas a si o passageiro id0 
            //e o estado do mesmo para que o porter, após todos cheguarem possa decidir onde colocar as malas
            for(int b=0;b<nBags;b++){
                //i => id
                bags[b] = new Baggage(i,jorneyEnds);
                //System.out.println(bags[b]);
            }
            //array de bags é passado como argumento pq ajuda o porter a remove las
            p[i] = new Passenger(i, bags,(IArraivalLoungePassenger) arraivalLounge,
                    (IBaggageCollectionPointPassenger) baggageCollectionPoint,
                    (IArraivalTerminalExitPassenger) arraivalTerminalExit,
                    (IArraivalTerminalTransferQPassenger) arraivalTerminalTransferQuay, 
                    (IDepartureTerminalTransferQPassenger) departureTerminalTransferQuay, 
                    (IDepartureTerminalEntrancePassenger) departureTerminalEntrance,
                    jorneyEnds);
            p[i].start();
            //  System.out.println(String.format("Passageiro gerado com %d malas: %s", nBags, p[i]));
        }

        try {
            porter.join();
            busdriver.join();
            time.join();
            for(int i = 0; i < maxPassengers; i++) {
                p[i].join();
            }
        } catch (Exception e) {

        }
    }
}
   