
import java.lang.management.MonitorInfo;
import java.util.Random;

import interfaces.IArraivalLoungePassenger;
import interfaces.IBaggageCollectionPointPassenger;
import threads.*;
import monitors.*;
import model.*;
public class AirportRhapsody {
    public static void main(String[] args){
            System.out.println("----------AirportRhapsody---------");
            final Random random = new Random();
            final int maxPassengers = 6;
            Baggage [] bags = null;
            Passenger p[] = new Passenger[maxPassengers];
            ArraivalLounge arraivalLounge = new ArraivalLounge(maxPassengers);
            BaggageCollectionPoint baggageCollectionPoint = new BaggageCollectionPoint();
            Porter porter = new Porter(arraivalLounge, baggageCollectionPoint);
            porter.start();
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
                p[i] = new Passenger(i, bags, (IArraivalLoungePassenger)arraivalLounge,(IBaggageCollectionPointPassenger)baggageCollectionPoint,jorneyEnds);
                p[i].start();
                System.out.println(String.format("Passageiro gerado com %d malas: %s", nBags, p[i]));
            }

            try {
                porter.join();
                for(int i = 0; i < maxPassengers; i++) {
                    p[i].join();
                }
            } catch (Exception e) {
    
            }
    }
}
   