package shared_regions;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import commonInfra.*;
import entities.*;

public class GeneralRepository{
    /**
     * File pass as argument 
    */
    private File logger;
    /**
     * thread porter
    */
    private Porter porter;
    /**
     * thread busdriver
    */
    private BusDriver busDriver;
    /**
     * thread passenger
    */
    private Passenger[] passengers;

    /**
     * Flight number
     */
    private int fn;
    /**
     * Number of pieces of luggage presently at the plane's hold
     */
    private int[] bn = {0, 1, 2, 3, 4};
    /**
     * State of the porter
     */
    private PorterEnum porterState;
    /**
     * Number of pieces of luggage presently on the conveyor belt
     */
    private int cb;
    /**
     * Number of pieces of luggage belonging to passengers in transit presently stored at the storeroom
     */
    private int[] sr = {0, 0, 0, 0, 0};
    /**
     * State of the driver
     */
    private BusDriverEnum bDriverState;
    /**
     * Occupation state for the waiting queue (passenger id / - (empty))
     */
    private String[] q = {"-", "-", "-", "-", "-", "-"};
    /**
     * Occupation state for seat in the bus (passenger id / - (empty))
     */
    private String[] s = {"-", "-", "-"};
    /**
     * State of passenger # (# - 0 .. 5)
     */
    private PassengerEnum[] passengerState = new PassengerEnum[6];
    /**
     * Situation of passenger # (# - 0 .. 5) – TRT (in transit) / FDT (has this airport as her  destination)
     */
    private String si;
    /**
     * Number of pieces of luggage the passenger # (# - 0 .. 5) carried at the start of her journey
     */
    private int[] nr = new int[6]; 
    /**
     * Number of pieces of luggage the passenger # (# - 0 .. 5) she has presently collected
     */
    private int[] na = new int[6];
    /**
     * Shows if this airport is  destination of the passenger (TRT - in transit; FDT -  destination);
     */
    private String[] passengerDest = new String[6];

    /**
     * Counter for number of passengers with final destination
     */
    private int final_dest_passengers = 0;

    /**
     * Counter for total of number of bags lost.
     */
    private int missingBags = 0;
    /**
     * Total number of bags that passed by the planes'hold.
     */
    private int bn_total;

    // Abbreviations of the porter and driver states, in order
    private  String[] porterStates = {"WPTL", "APLH", "ALCB", "ASTR"};
    private  String[] bDriverStates = {"PKAT", "DRFW", "PKDT", "DRBW"};
    private  String[] passengerStates = {"WSD", "LCP", "BRO", "EAT", "ATT", "TRT", "DTT", "EDT"};

    public GeneralRepository(File logger) {
        this.logger = logger;
        
         String header = "               AIRPORT RHAPSODY - Description of the internal state of the problem";
         String s1 = "PLANE    PORTER                  DRIVER";
         String s2 = "FN BN  Stat CB SR   Stat  Q1 Q2 Q3 Q4 Q5 Q6  S1 S2 S3";
         String s3 = "                                                         PASSENGERS";
         String s4 = "St1 Si1 NR1 NA1 St2 Si2 NR2 NA2 St3 Si3 NR3 NA3 St4 Si4 NR4 NA4 St5 Si5 NR5 NA5 St6 Si6 NR6 NA6";


        initializeLogger(header);
        initializeLogger("");
        initializeLogger(s1);
        initializeLogger(s2);
        initializeLogger(s3);
        initializeLogger(s4);


        this.porterState = PorterEnum.WAITING_FOR_A_PLANE_TO_LAND;
        this.bDriverState = BusDriverEnum.PARKING_AT_THE_ARRIVAL_TERMINAL;

    }
    /**
     * Update state of the passenger
     * @param passengerID
     * @param passengerState
     * @param Dest
     * @param nr_bags
     */
    public synchronized void missingBags(int nrBags, int passengerID){
        this.missingBags +=1;
    }
    /**
     * State of the passenger
     */
    public synchronized void passengerState(int passengerID, PassengerEnum passengerState,  boolean Dest,  int nr_bags){
        if(Dest) this.passengerDest[passengerID] = "FDT";
        else this.passengerDest[passengerID] = "TRT";
        this.nr[passengerID] = nr_bags;
        this.na[passengerID] = 0;
        this.passengerState[passengerID] = passengerState;
        updateStatePorterOrBDriver();  
    }
     /**
     * State of the passenger
     */
    public synchronized void passengerState(int passengerID, PassengerEnum passengerState){
        if(this.passengerState[passengerID] != passengerState){
            this.passengerState[passengerID] = passengerState;
            updateStatePorterOrBDriver();
        }
    }

     /**
     * State of the porter
     */
    public synchronized void porterState(PorterEnum porterState){
        if(porterState != this.porterState){
            this.porterState = porterState;
            updateStatePorterOrBDriver();
        } 
    }

    private void updateStatePorterOrBDriver(){
        String info1 = " " + this.fn + "  " + this.bn[this.fn] + "  " + porterStates[this.porterState.ordinal()] + "  " + this.cb + "  " + this.sr[this.fn]  + "   " 
                           + bDriverStates[this.bDriverState.ordinal()] + "   " 
                           + this.q[0] + "  " + this.q[1] + "  " + this.q[2] + "  " + this.q[3] + "  " + this.q[4] + "  " + this.q[5] + "  " 
                           + this.s[0] + "  " + this.s[1] + "  " + this.s[2];
        String info2 = "";
        String tmp;
        for(int i = 0; i < 6; i++){
            tmp = "";
            if(this.passengerState[i] == null) tmp = "--- ---  -   -";
            else{
                tmp = passengerStates[this.passengerState[i].ordinal()] + " " + this.passengerDest[i] + "  " + this.nr[i] + "   " + this.na[i];
            }
            info2 += tmp + "  ";
        }
        writeToLogger(info1);
        writeToLogger(info2);
    }

    void writeToLogger( String toWrite){
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

}