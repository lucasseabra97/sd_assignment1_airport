

import java.io.File;
import java.io.IOException;
import java.util.Random;

import commonInfra.Baggage;
import entities.*;
import shared_regions.*;
import interfaces.*;

public class AirportRhapsody {

    /**
	 * @param args
	 * @throws InterruptedException
	 */
    public static void main(String[] args)throws IOException{


        // creates new logger
		File logger = new File("logger.txt");
		if(logger.createNewFile()){
			//System.out.println("Logger created: " + logger.getName());
        }
        else{
			logger.delete();
			logger.createNewFile();
			// System.out.println("File already exists.");
			
		}
		GeneralRepository genInfoRepo = new GeneralRepository(logger);



        System.out.println("----------AirportRhapsody---------");
        final Random random = new Random();
        final int maxPassengers = 6;
        final int busSize = 3;
        final int planeLandings=5;
        Baggage [] bags = null;
         /**
		*{@link entities.Passenger}
		*/
        Passenger p[] = new Passenger[maxPassengers];
        // Initialize shared region ArraivalLounge
		/**
		* {@link shared_regions.ArraivalLounge}
		*/
        ArraivalLounge arraivalLounge = new ArraivalLounge(maxPassengers);
        // Initialize shared region BaggageCollectionPoint
		/**
		* {@link shared_regions.BaggageCollectionPoint}
		*/
        BaggageCollectionPoint baggageCollectionPoint = new BaggageCollectionPoint();
        // Initialize shared region ArraivalTerminalExit
		/**
		* {@link shared_regions.ArraivalTerminalExit}
		*/
        ArraivalTerminalExit arraivalTerminalExit = new ArraivalTerminalExit(maxPassengers);
        // Initialize shared region ArraivalTerminalTransferQuay
		/**
		* {@link shared_regions.ArraivalTerminalTransferQuay}
		*/
        ArraivalTerminalTransferQuay arraivalTerminalTransferQuay = new ArraivalTerminalTransferQuay(busSize);
        // Initialize shared region BaggageReclaimOffice
		/**
		* {@link shared_regions.BaggageReclaimOffice}
        */
        
        BaggageReclaimOffice baggageReclaimOfficePassenger = new BaggageReclaimOffice(genInfoRepo); 
        
        // Initialize shared region DepartureTerminalTransferQuay
		/**
		* {@link shared_regions.DepartureTerminalTransferQuay}
        */
        DepartureTerminalTransferQuay departureTerminalTransferQuay = new DepartureTerminalTransferQuay();
        // Initialize shared region DepartureTerminalEntrance
		/**
		* {@link shared_regions.DepartureTerminalEntrance}
		*/
        DepartureTerminalEntrance departureTerminalEntrance = new DepartureTerminalEntrance(maxPassengers);
        
        /**
		* {@link shared_regions.DepartureTerminalEntrance}
        */
        TemporaryStorageArea temporaryStorageArea = new TemporaryStorageArea();
        
        
        /**
		 *{@link entities.Porter}
		 */
        Porter porter = new Porter((IArraivalLoungePorter) arraivalLounge,(IBaggageCollectionPointPorter) baggageCollectionPoint, (ITemporaryStorageAreaPorter) temporaryStorageArea);
        porter.start();
        /**
		*{@link entities.BusDriver}
		*/
        BusDriver busdriver = new BusDriver(arraivalTerminalTransferQuay , departureTerminalTransferQuay,busSize);
        busdriver.start();
         /**
		*{@link entities.Time}
		*/
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
                    (IDepartureTerminalEntrancePassenger) departureTerminalEntrance, (IBaggageReclaimOfficePassenger) baggageReclaimOfficePassenger,
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
   