
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
        // final int nrPassengers = 6;
        // final int busSize = 3;
        // final int maxBags = 3;
        // final int fligths=5;
       /**
		 * List of every {@link Bag} of every flight occurring in this airport.
		 */
		List<List<Integer>> bags = generateRandBags(global.NR_PASSENGERS, global.MAX_BAGS, global.NR_FLIGHTS);
        /**
         * {@link entities.Passenger}
         */

        Passenger p[] = new Passenger[global.NR_PASSENGERS];
        // Initialize shared region ArraivalLounge
        /**
         * {@link shared_regions.ArraivalLounge}
         */
        ArraivalLounge arraivalLounge = new ArraivalLounge(global.NR_PASSENGERS);
        // Initialize shared region BaggageCollectionPoint
        /**
         * {@link shared_regions.BaggageCollectionPoint}
         */
        BaggageCollectionPoint baggageCollectionPoint = new BaggageCollectionPoint();
        // Initialize shared region ArraivalTerminalExit
        /**
         * {@link shared_regions.ArraivalTerminalExit}
         */
        ArraivalTerminalExit arraivalTerminalExit = new ArraivalTerminalExit(global.NR_PASSENGERS);
        // Initialize shared region ArraivalTerminalTransferQuay
        /**
         * {@link shared_regions.ArraivalTerminalTransferQuay}
         */
        ArraivalTerminalTransferQuay arraivalTerminalTransferQuay = new ArraivalTerminalTransferQuay(global.BUS_SIZE);
        // Initialize shared region DepartureTerminalTransferQuay
        /**
         * {@link shared_regions.DepartureTerminalTransferQuay}
         */
        DepartureTerminalTransferQuay departureTerminalTransferQuay = new DepartureTerminalTransferQuay();
        // Initialize shared region DepartureTerminalEntrance
        /**
         * {@link shared_regions.DepartureTerminalEntrance}
         */
        DepartureTerminalEntrance departureTerminalEntrance = new DepartureTerminalEntrance(global.NR_PASSENGERS);
        /**
         * {@link entities.Porter}
         */
        Porter porter = new Porter(arraivalLounge, baggageCollectionPoint);
        porter.start();
        /**
         * {@link entities.BusDriver}
         */
        BusDriver busdriver = new BusDriver(arraivalTerminalTransferQuay, departureTerminalTransferQuay,
                global.BUS_SIZE);
        busdriver.start();
        /**
         * {@link entities.Time}
         */
        Time time = new Time(arraivalTerminalTransferQuay, arraivalLounge);
        time.start();

        for (int i = 0; i < global.NR_PASSENGERS; i++) {

            // array de bags é passado como argumento pq ajuda o porter a remove las
            p[i] = new Passenger(i, bags.get(i), (IArraivalLoungePassenger) arraivalLounge,
                    (IBaggageCollectionPointPassenger) baggageCollectionPoint,
                    (IArraivalTerminalExitPassenger) arraivalTerminalExit,
                    (IArraivalTerminalTransferQPassenger) arraivalTerminalTransferQuay,
                    (IDepartureTerminalTransferQPassenger) departureTerminalTransferQuay,
                    (IDepartureTerminalEntrancePassenger) departureTerminalEntrance);
            p[i].start();
            // System.out.println(String.format("Passageiro gerado com %d malas: %s", nBags,
            // p[i]));
        }

        try {
            porter.join();
            busdriver.join();
            time.join();
            for (int i = 0; i < global.NR_PASSENGERS; i++) {
                p[i].join();
            }
        } catch (Exception e) {

        }

    }

    public static List<List<Integer>> generateRandBags(int nrPassengers, int maxBags, int nrFlights) {
            List<List<Integer>> bagsPerPassenger = new ArrayList<List<Integer>>(nrPassengers);
		    int[] bagsPerFlight = new int[nrFlights];
            List<Integer> bagsList;
           
            for(int p=0;p<nrPassengers;p++){
                bagsList = new ArrayList<Integer>();
                
                for(int v=0;v<nrFlights;v++){
                    Random random = new Random();
                    int nrBagsRand =random.nextInt(maxBags + 1);
                    bagsList.add(nrBagsRand);
                    bagsPerFlight[v] += nrBagsRand;
                }
                bagsPerPassenger.add(bagsList);
            }
            return bagsPerPassenger;
        }
    
}
   