
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import main.*;
import commonInfra.Baggage;

import entities.*;
import shared_regions.*;
import interfaces.*;

public class AirportRhapsody {

    /**
	 * @param args
     * @return 
	 * @throws InterruptedException
	 */
    public static void main(String[] args)throws IOException{


        System.out.println("----------AirportRhapsody---------");
        final Random random = new Random();
       

        List<List<Baggage>> bagsPerFlight = new ArrayList<>(global.NR_FLIGHTS);
        Boolean[][] passengersDestination = new Boolean [global.NR_PASSENGERS][global.NR_FLIGHTS];
        List<List<List<Baggage>>> passengersBags = new ArrayList<>(global.NR_PASSENGERS);



        

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
        /**
         * {@link entities.Passenger}
         */

        Passenger passengers[] = new Passenger[global.NR_PASSENGERS];
        // Initialize shared region ArraivalLounge
        /**
         * {@link shared_regions.ArraivalLounge}
         */
        ArraivalLounge arraivalLounge = new ArraivalLounge(bagsPerFlight,genInfoRepo);
        // Initialize shared region BaggageCollectionPoint
        /**
         * {@link shared_regions.BaggageCollectionPoint}
         */
        BaggageCollectionPoint baggageCollectionPoint = new BaggageCollectionPoint(genInfoRepo);
        // Initialize shared region ArraivalTerminalExit
        /**
         * {@link shared_regions.ArraivalTerminalExit}
         */
        ArraivalTerminalExit arraivalTerminalExit = new ArraivalTerminalExit(global.NR_PASSENGERS,genInfoRepo);
        // Initialize shared region ArraivalTerminalTransferQuay
        /**
         * {@link shared_regions.ArraivalTerminalTransferQuay}
         */
        ArraivalTerminalTransferQuay arraivalTerminalTransferQuay = new ArraivalTerminalTransferQuay(genInfoRepo);
        // Initialize shared region DepartureTerminalTransferQuay
        /**
         * {@link shared_regions.DepartureTerminalTransferQuay}
         */
        DepartureTerminalTransferQuay departureTerminalTransferQuay = new DepartureTerminalTransferQuay(genInfoRepo);
        // Initialize shared region DepartureTerminalEntrance
        /**
         * {@link shared_regions.DepartureTerminalEntrance}
         */
        DepartureTerminalEntrance departureTerminalEntrance = new DepartureTerminalEntrance(global.NR_PASSENGERS,genInfoRepo);
        /**
         * {@link entities.Porter}
         */
        /**
		* {@link shared_regions.BaggageReclaimOffice}
        */
        BaggageReclaimOffice baggageReclaimOfficePassenger = new BaggageReclaimOffice(genInfoRepo); 

        TemporaryStorageArea temporaryStorageArea = new TemporaryStorageArea(genInfoRepo);
        
        
        Porter porter = new Porter((IArraivalLoungePorter) arraivalLounge,(IBaggageCollectionPointPorter) baggageCollectionPoint, (ITemporaryStorageAreaPorter) temporaryStorageArea); 
        porter.start();
        /**
         * {@link entities.BusDriver}
         */
        BusDriver busdriver = new BusDriver(arraivalTerminalTransferQuay, departureTerminalTransferQuay,global.BUS_SIZE);
        busdriver.start();
        /**
         * {@link entities.Time}
         */
        // Time time = new Time(arraivalTerminalTransferQuay, arraivalLounge);
        // time.start();

        


        for(int p = 0;p<global.NR_PASSENGERS;p++){
            passengersBags.add(new ArrayList<>());
            for(int v = 0; v < global.NR_FLIGHTS; v++) {
                passengersBags.get(p).add(new ArrayList<>());
                if(bagsPerFlight.size() <= v)
                    bagsPerFlight.add(new ArrayList<>()); 

                Boolean goHome = random.nextBoolean();
                passengersDestination[p][v] =  goHome ? true : false;

                int nrRandomBags = random.nextInt(global.MAX_BAGS + 1);
                int bagsLost = random.nextInt(100);
                bagsLost = bagsLost < 5 ? 2 : (bagsLost < 25 ? 1 : 0);

                for(int b = 0; b < nrRandomBags; b++) {
                    Baggage bag = new Baggage(p, passengersDestination[p][v]);
                    passengersBags.get(p).get(v).add(bag);
                }
                int bagsToAdd = bagsLost > nrRandomBags ? nrRandomBags : nrRandomBags - bagsLost;
                for(int b = 0; b < bagsToAdd; b++) {
                    bagsPerFlight.get(v).add(passengersBags.get(p).get(v).get(b));
                }
            }
        }

      
       // genInfoRepo.writeHeader();

       for(int i = 0; i < global.NR_PASSENGERS; i++) {
            passengers[i] = new Passenger(i,passengersDestination[i], 
                            passengersBags.get(i), 
                            (IArraivalLoungePassenger) arraivalLounge, 
                            (IBaggageCollectionPointPassenger) baggageCollectionPoint, 
                            (IArraivalTerminalExitPassenger) arraivalTerminalExit, 
                            (IArraivalTerminalTransferQPassenger) arraivalTerminalTransferQuay, 
                            (IDepartureTerminalTransferQPassenger) departureTerminalTransferQuay,
                            (IDepartureTerminalEntrancePassenger) departureTerminalEntrance, 
                            (IBaggageReclaimOfficePassenger) baggageReclaimOfficePassenger);
            passengers[i].start();
            
        } 


        try {
            porter.join();
            busdriver.join();
            //time.join();
            for (int i = 0; i < global.NR_PASSENGERS; i++) {
                passengers[i].join();
            }
        } catch (Exception e) {
            
        }finally{
            genInfoRepo.close();
        }

    }

   
    
}
   