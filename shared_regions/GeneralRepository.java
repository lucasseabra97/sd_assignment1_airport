package shared_regions;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

import commonInfra.*;
import entities.*;
import main.global;

/**
 * General Repository region.
 * 
 * @author Lucas Seabra
 * @author Joao Monteiro
 */

public class GeneralRepository{
    /**
     * File pass as argument 
    */
    private File logger;
    /**
     * flight luggage
     */
    private int flightLuggage;
   
    private String[] passengerStates = new String[global.NR_PASSENGERS]; 
    /**
     * Counter for number of passengers with final destination
     */
    private int finalDestinations = 0;

    /**
     * Counter for number of passengers in transit
     */
    private int transit =0 ;
    /**
     * pointer for queue
     */
    private int queueIdx;

    /**
     * pointer for seats queue
     */
    private int seatsIdx;
    /**
     * Variable for amount of bag
     */
    private int bags;
    /**
     * variable for lots bags
     */
    private int lostBags;
    /**
     * luggage at the conveyor belt
     */
    private int conveyorLuggage;
    /**
     * luggage at the store room
     */
    private int storeroomLuggage;

    /**
     * add the luggage to a passenger
     */
    private String[] passengerLuggage;
    /**
     * array to collect all passengers situations
     */
    private String[] passengerSituation;
    /**
     * queue to handle bus entrance
     */
    private String[] queue;
    /**
     * queue to handle bus seats
     */
    private String[] seats;
    /**
     * flights count
     */
    private int flight;
    /**
     * max number of passengers
     */
    private int maxPassengers;
    /**
     * add luggage that have been collected by the passenger
     */
    private String[] passengerCollectedLuggage;
    /**
     *  reentrant lock
     */
    private ReentrantLock rl;
    
    /**
     * Porter state
     */
    private String porterStates;
    /**
     * BusDriverState
     */
    private String bDriverStates;



    public GeneralRepository(File logger) {
        this.logger = logger;
        this.maxPassengers = global.NR_PASSENGERS;
        String header = "               AIRPORT RHAPSODY - Description of the internal state of the problem";
        String s1 = "PLANE    PORTER                  DRIVER";
        String s2 = "FN BN  Stat CB SR   Stat  Q1 Q2 Q3 Q4 Q5 Q6  S1 S2 S3";
        String s3 = "                                                         PASSENGERS";
        String s4 = "St1 Si1 NR1 NA1 St2 Si2 NR2 NA2 St3 Si3 NR3 NA3 St4 Si4 NR4 NA4 St5 Si5 NR5 NA5 St6 Si6 NR6 NA6";
        this.rl = new ReentrantLock(true);
        this.conveyorLuggage =0;
        initializeLogger(header);
        initializeLogger("");
        initializeLogger(s1);
        initializeLogger(s2);
        initializeLogger(s3);
        initializeLogger(s4);
        this.flightLuggage=0;
        this.storeroomLuggage=0;
        this.lostBags = 0;
        this.bags=0;
        this.porterStates = "WPTL";
        this.bDriverStates ="PKAT";
        this.queue = new String[global.NR_PASSENGERS];
        for(int i=0;i<global.NR_PASSENGERS;i++) this.queue[i] = "-";
        this.queueIdx = 0;
        this.seats = new String[global.BUS_SIZE];
        for(int i=0;i<global.BUS_SIZE;i++) this.seats[i] = "-";
        this.seatsIdx = 0;
        this.passengerStates = new String[global.NR_PASSENGERS];
        for(int i=0;i<global.NR_PASSENGERS;i++) this.passengerStates[i] = "---";
        this.passengerSituation = new String[global.NR_PASSENGERS];
        for(int i=0;i<global.NR_PASSENGERS;i++) this.passengerSituation[i] = "---";
        this.passengerLuggage = new String[global.NR_PASSENGERS];
        for(int i=0;i<global.NR_PASSENGERS;i++) this.passengerLuggage[i] = "-";
        this.passengerCollectedLuggage = new String[global.NR_PASSENGERS];
        for(int i=0;i<global.NR_PASSENGERS;i++) this.passengerCollectedLuggage[i] = "-";
        this.flight =0;
    
    }
    



     /**
     * Increases the current flight number.
     * Does not write the log file on it's own. The update has to be written by another change.
     */
    private void addFlight() {
        this.flight++;
    }

    /**
     * Gets the current flight number to the one given.
     * @param flight The flight number to be set.
     */
    public int getFlight() {
        return flight - 1;
    }

    /**
     * Sets the flight luggage to the one given.
     * @param flightLuggage The amount of luggage at the plane's hold.
     */
    private void setFlightLuggage(int flightLuggage) {
        this.flightLuggage = flightLuggage;
    }

     /**
     * State of the passenger
     */
    public synchronized void passengerState(int passengerID, PassengerEnum passengerState){
        switch(passengerState){
            case AT_THE_DISEMBARKING_ZONE:
                this.passengerStates[passengerID] = "WSD";
                break;
                
            case AT_THE_ARRIVAL_TRANSFER_TERMINAL:
                this.passengerStates[passengerID] = "ATT";
                break;

            case TERMINAL_TRANSFER:
                this.passengerStates[passengerID] = "TRT";
                break;

            case AT_THE_DEPARTURE_TRANSFER_TERMINAL:
                this.passengerStates[passengerID] = "DTT";
                break;

            case ENTERING_THE_DEPARTURE_TERMINAL:
                this.passengerStates[passengerID] = "EDT";
                break;
                
            case AT_THE_LUGGAGE_COLLECTION_POINT:
                this.passengerStates[passengerID] = "LCP";
                break;

            case AT_THE_BAGGAGE_RECLAIM_OFFICE:
                this.passengerStates[passengerID] = "BRO";
                break;

            case EXITING_THE_ARRIVAL_TERMINAL:
                this.passengerStates[passengerID] = "EAT";
                break;

        }
      }

     /**
     * State of the porter
     */
    public synchronized void porterState(PorterEnum porterState){
        switch(porterState){
            case WAITING_FOR_A_PLANE_TO_LAND:
                this.porterStates = "WPTL";
                break;
                
            case AT_THE_PLANES_HOLD:
                this.porterStates = "APLH";
                break;

            case AT_THE_LUGGAGE_BELT_CONVEYOR:
                this.porterStates = "ALCB";
                break;

            case AT_THE_STOREROOM:
                this.porterStates = "ASTR";
                break;

            default:
        }
    }


    /**
     * State of Bus Driver 
     * 
     */
    private void busdriverState(BusDriverEnum busState) {
        switch(busState){
            case PARKING_AT_THE_ARRIVAL_TERMINAL:
                this.bDriverStates = "PKAT";
                break;
                
            case DRIVING_FORWARD:
                this.bDriverStates = "DRFW";
                break;

            case DRIVING_BACKWARD:
                this.bDriverStates = "DRBW";
                break;

            case PARKING_AT_THE_DEPARTURE_TERMINAL:
                this.bDriverStates = "PKDT";
                break;

            default:
            }
        }


          /**
     * Sets the driver state to Parking at the Arrival Terminal (PKAT), only if it isn't already in that state.
     */
    public void driverParkingArrivalTerminal() {
        rl.lock();
        try{
            if(!bDriverStates.equals("PKAT")){
                busdriverState(BusDriverEnum.PARKING_AT_THE_ARRIVAL_TERMINAL);
                write();
            }
        }catch(Exception e){}
        finally{
            rl.unlock();
        }
    }

    /**
     * Sets the driver state to Parking at the Departure Terminal (PKDT), only if it isn't already in that state.
     */
    public void driverParkingDepartureTerminal() {
  
        rl.lock();
        try{
            if(!bDriverStates.equals("PKDT")){
                busdriverState(BusDriverEnum.PARKING_AT_THE_DEPARTURE_TERMINAL);
                write();
            }
        }catch(Exception e){}
        finally{
            rl.unlock();
        }
    }

    /**
     * Sets the driver state to Driving Forward (DRFW), only if it isn't already in that state.
     */
    public void driverDrivingForward() {
        rl.lock();
        try{
            if(!bDriverStates.equals("DRFW")){
                busdriverState(BusDriverEnum.DRIVING_FORWARD);
                write();
            }
        }catch(Exception e){}
        finally{
            rl.unlock();
        }
    }

    /**
     * Sets the driver state to Driving Backward (DRBW), only if it isn't already in that state.
     */
    public void driverDrivingBackward() {
        rl.lock();
        try{
            if(!bDriverStates.equals("DRBW")){
                busdriverState(BusDriverEnum.DRIVING_BACKWARD);
                write();
            }
        }catch(Exception e){}
        finally{
            rl.unlock();
        }
    }



     /**
     * sets the porter state to Waiting Plane to Land (WPTL)
     */
    public void porterWaitingLanding() {
        rl.lock();
        try{
            if(!porterStates.equals("WPTL")){
                porterState(PorterEnum.WAITING_FOR_A_PLANE_TO_LAND);
                write();
            }
        }catch(Exception e){}
        finally{
            rl.unlock();
        }
    }

    /**
     *  sets the porter state to At the Plane's Hold (APLH)
     */
    public void porterNoMoreBags() {
        rl.lock();
        try{
            if(!porterStates.equals("APLH")){
                porterState(PorterEnum.AT_THE_PLANES_HOLD);
                write();
            }
        }catch(Exception e){}
        finally{
            rl.unlock();
        }
    }

    /**
     * Sets the porter state to At the Plane's Hold (APLH) and removes a bag from the flight luggage.
     */
    public void porterCollectBag() {
        rl.lock();
        try{
            porterState(PorterEnum.AT_THE_PLANES_HOLD);
            this.flightLuggage--;
            write();
        }catch(Exception e){}
        finally{
            rl.unlock();
        }
    }

    /**
     * Sets the porter state to At the Luggage Belt Conveyor (ALBC) and adds a bag to the conveyor belt luggage.
     */
    public void porterMoveBagToConveyorBelt() {
        rl.lock();
        try{
            porterState(PorterEnum.AT_THE_LUGGAGE_BELT_CONVEYOR);
            this.conveyorLuggage++;
            write();
        }catch(Exception e){}
        finally{
            rl.unlock();
        }
    }

    /**
     * Sets the porter state to At the Storeroom (ASTR) and removes a bag from the flight luggage.
     */
    public void porterMoveBagToStoreroom() {
        rl.lock();
        try{
            porterState(PorterEnum.AT_THE_STOREROOM);
            this.storeroomLuggage++;
            write();
        }catch(Exception e){}
        finally{
            rl.unlock();
        }
    }
        /**
     * Initializes a passenger in the logging file.
     * Changes it's state to the given one , sets it's amount of luggage and situation.
     * @param state A state from {@link enums.PassengerEnum} .
     * @param bags The amount of bags the passenger owns.
     * @param situation The passenger's situation : either "FDT" (final destination) or "TRF" (in transit).
     * @param id The passenger's id. Ids range from 0 to {@link main.global} .
     */
    public void passengerInit(PassengerEnum state, int bags, String situation ,int id) {
        rl.lock();
        try{
            passengerState(id, state);
            this.passengerLuggage[id] = Integer.toString(bags);
            this.passengerSituation[id] = situation;
            this.passengerCollectedLuggage[id] = "0";
            write();
        }catch(Exception e){}
        finally{
            rl.unlock();
        }
    }

    public void passEnterLuggageCollectionPoint(int id) {
        rl.lock();
        try{
            if(!passengerStates[id].equals("LCP")){
                passengerState(id,PassengerEnum.AT_THE_LUGGAGE_COLLECTION_POINT);
                write();
            }
        }catch(Exception e){}
        finally{
            rl.unlock();
        }
    }

    /**
     * Removes a bag from the conveyor luggage and adds a bag to the passenger collected luggage.
     * @param id The id of the passenger to affect. Ids range from 0 to {@link main.global}-1 .
     */
    public void passCollectBag(int id) {
        rl.lock();
        try{
            passengerCollectedLuggage[id] = Integer.toString(Integer.parseInt(passengerCollectedLuggage[id])+1);
            conveyorLuggage--;
            write();
        }catch(Exception e){}
        finally{
            rl.unlock();
        }
    }

    /**
     * Sets the passenger to At the Baggage Reclaim Office (BRO) and adds a bag to the lost bag count.
     * @param id The id of the passenger to affect. Ids range from 0 to {@link main.global}-1 .
     */
    public void passComplain(int id) {
        rl.lock();
        try{
            passengerState(id,PassengerEnum.AT_THE_BAGGAGE_RECLAIM_OFFICE);
            addLostBag();
            write();
        }catch(Exception e){}
        finally{
            rl.unlock();
        }
    }

    /**
     * Sets the passenger to At the Arrival Terminal Tranfer (ATT) and adds the passenger to the bus waiting queue.
     * @param id The id of the passenger to affect. Ids range from 0 to {@link main.global}-1 .
     */
    public void passJoinBusQueue(int id) {
        rl.lock();
        try{
            passengerState(id ,PassengerEnum.AT_THE_ARRIVAL_TRANSFER_TERMINAL);
            queue[queueIdx++] = Integer.toString(id);
            write();
        }catch(Exception e){}
        finally{
            rl.unlock();
        }
    }

    /**
     * Removes the passenger from the bus waiting queue and sits it on the bus.
     * @param id The id of the passenger to affect. Ids range from 0 to {@link main.global}-1 .
     */
    public void passSitInBus(int id) {
        rl.lock();
        try{
            //remove from queue
            for(int i=0; i<queue.length; i++) {
                if(Integer.toString(id).equals(queue[i])){
                    for(int j=i; j<queue.length-1; j++) {
                        queue[j] = queue[j+1];
                    }
                    queue[queue.length-1] = "-";
                    break;
                }
            }
            queueIdx--;

            seats[seatsIdx++] = Integer.toString(id);
            write();
        }catch(Exception e){}
        finally{
            rl.unlock();
        }
    }

    /**
     * Sets the indicated passenger's state to Terminal Tranfer (TRT), only if it isn't already a that state.
     * @param id The id of the passenger to affect. Ids range from 0 to {@link main.global}-1 .
     */
    public void passBusRide(int id) {
        rl.lock();
        try{
            if(!passengerStates[id].equals("TRT")){
                passengerState(id , PassengerEnum.TERMINAL_TRANSFER);
                write();
            }
        }catch(Exception e){}
        finally{
            rl.unlock();
        }
    }

    /**
     * Sets the passenger's state to At the Departure Tranfer Terminal (DTT) and removes him from the bus.
     * @param id The id of the passenger to affect. Ids range from 0 to {@link main.global}-1 .
     */
    public void passLeaveBus(int id) {
        rl.lock();
        try{
            passengerState(id ,PassengerEnum.AT_THE_DEPARTURE_TRANSFER_TERMINAL);
            seats[--seatsIdx] = "-";
            write();
        }catch(Exception e){}
        finally{
            rl.unlock();
        }
    }

    /**
     * Sets the indicated passenger's state to Exiting the Arrival Terminal (EAT), only if it isn't already a that state.
     * @param id The id of the passenger to affect. Ids range from 0 to {@link main.global}-1 .
     */
    public void passGoHome(int id) {
        rl.lock();
        try{
            if(!passengerStates[id].equals("EAT")){
                passengerState(id ,PassengerEnum.EXITING_THE_ARRIVAL_TERMINAL);
                write();
            }
        }catch(Exception e){}
        finally{
            rl.unlock();
        }
    }

    /**
     * Sets the indicated passenger's state to Entering the Departure Terminal (EDT), only if it isn't already a that state.
     * @param id The id of the passenger to affect. Ids range from 0 to {@link main.global}-1 .
     */
    public void passPrepareNextLeg(int id) {
        rl.lock();
        try{
            if(!passengerStates[id].equals("EDT")){
                passengerState(id , PassengerEnum.ENTERING_THE_DEPARTURE_TERMINAL);
                write();
            }
        }catch(Exception e){}
        finally{
            rl.unlock();
        }
    }

    /**
     * Resets the logging state of all passengers.
     * Sets their states and situations to "---".
     * Sets their luggage and collected luggage to "-".
     */
    private void passengersReset() {
        rl.lock();
        try{
            for(int i=0;i<global.NR_PASSENGERS;i++) passengerStates[i] = "---";
            for(int i=0;i<global.NR_PASSENGERS;i++) passengerSituation[i] = "---";
            for(int i=0;i<global.NR_PASSENGERS;i++) passengerLuggage[i] = "-";
            for(int i=0;i<global.NR_PASSENGERS;i++) passengerCollectedLuggage[i] = "-";
        }catch(Exception e){}
        finally{
            rl.unlock();
        }
    }

    /**
     * Resets states to start the next flight.
     * Does not write the log file on it's own. The update has to be written by another change. (The first passenger does so upon initialization)
     */
    public void startNextFlight(int flightLuggage) {
        rl.lock();
        try{
            this.porterStates = "WPTL";
            this.bDriverStates = "PKAT";
            addFlight();
            setFlightLuggage(flightLuggage);
            passengersReset();
        }catch(Exception e){}
        finally{
            rl.unlock();
        }
    }


     /**
     * Adds a final desitnation to the final report.
     */
    public void addFinalDestinations() {
        rl.lock();
        try{
            this.finalDestinations++;
        }catch(Exception e){}
        finally{
            rl.unlock();
        }
    }

    /**
     * Adds a transit desitnation to the final report.
     */
    public void addTransit() {
        rl.lock();
        try{
            this.transit++;
        }catch(Exception e){}
        finally{
            rl.unlock();
        }
    }
    /**
     * add bags to repository
     */

    public void addBag() {
        rl.lock();
        try{
            this.bags++;
        }catch(Exception e){}
        finally{
            rl.unlock();
        }
    }
    /**
     * add lots bags to repository
     */

    public void addLostBag() {
        rl.lock();
        try{
            this.lostBags++;
        }catch(Exception e){}
        finally{
            rl.unlock();
        }
    }

    // private void updateStatePorterOrBDriver(){
    //     String info1 = " " + this.fn + "  " + this.bn[this.fn] + "  " + porterStates[this.porterState.ordinal()] + "  " + this.cb + "  " + this.sr[this.fn]  + "   " 
    //                        + bDriverStates[this.bDriverState.ordinal()] + "   " 
    //                        + this.q[0] + "  " + this.q[1] + "  " + this.q[2] + "  " + this.q[3] + "  " + this.q[4] + "  " + this.q[5] + "  " 
    //                        + this.s[0] + "  " + this.s[1] + "  " + this.s[2];
    //     String info2 = "";
    //     String tmp;
    //     for(int i = 0; i < 6; i++){
    //         tmp = "";
    //         if(this.passengerState[i] == null) tmp = "--- ---  -   -";
    //         else{
    //             tmp = passengerStates[this.passengerState[i].ordinal()] + " " + this.passengerDest[i] + "  " + this.nr[i] + "   " + this.na[i];
    //         }
    //         info2 += tmp + "  ";
    //     }
    //     writeToLogger(info1);
    //     writeToLogger(info2);
    // }

    
    /**
     * Writes an update to the log file.
     * @throws IOException
     */
    private void write() throws IOException{
        rl.lock();
        FileWriter fw = null;
        try{
            fw = new FileWriter(logger, true); // Append
            String busQueue_Seats = "";
            for(int i=0; i<queue.length; i++) {
                busQueue_Seats += queue[i] + "  ";
            }
            busQueue_Seats += " ";
            for(int i=0; i<seats.length; i++) {
                busQueue_Seats += seats[i] + "  ";
            }
            busQueue_Seats += "\n";
            String line1 = " " + Integer.toString(flight) + " " + Integer.toString(flightLuggage) + "   " + porterStates.toString()
                           + "  " + Integer.toString(conveyorLuggage) + "  " + Integer.toString(storeroomLuggage) +  "   "
                           + bDriverStates.toString() + "   " + busQueue_Seats;
            fw.write(line1);

            String line2 = "";
            for(int i=0; i<maxPassengers; i++){
                line2 += passengerStates[i] + " " + passengerSituation[i] + "  " + passengerLuggage[i] + "   " + passengerCollectedLuggage[i] + "  ";
            } 
            line2 += "\n";
            fw.write(line2);

        }catch(Exception e){}
        finally{
            fw.close();
            rl.unlock();
        }
    }

    void initializeLogger(String toWrite){
        assert toWrite != null : "ERROR: nothing to update!";

        try {
             FileWriter myWriter = new FileWriter(this.logger, true);
            myWriter.write(toWrite + '\n');
            myWriter.close();
        } catch ( IOException e) {
            System.out.println("Thread: " + Thread.currentThread().getName() + " terminated.");
			System.out.println("Error: " + e.getMessage());
			System.exit(1);
        } 

    }



    /**
     * Writes the final report to the log file and closes it.
     * @throws IOException
     */
    public void close() throws IOException{
        rl.lock();
        FileWriter fw = null;
        try{
            fw = new FileWriter(logger, true); // Append
            fw.write("\nFinal Report\n");
            fw.write("N. of passengers which have this airport as their final destination = " + finalDestinations + "\n");
            fw.write("N. of passengers which are in transit = " + transit + "\n");
            fw.write("N. of bags that should have been transported in the the planes hold = " + bags + "\n");
            fw.write("N. of bags that were lost = " + lostBags + "\n");
            fw.close();
        }catch(Exception e){}
        finally{
            fw.close();
            rl.unlock();
        }
    }

}