package shared_regions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import commonInfra.*;
import entities.*;
import interfaces.*;
import main.global;


/**
 * Arraival Terminal Transfer Quay shared memory region.
 * 
 * @author Lucas Seabra
 * @author Joao Monteiro
 */

public class ArraivalTerminalTransferQuay implements IArraivalTerminalTransferQPassenger,IArraivalTerminalTransferQBusDriver{
    /**
    * Arriaval Terminal Transfer Quay for locking 
	*/
    private final ReentrantLock rl;
    /**
    * Arriaval Terminal Transfer Quay  Conditional variable for Passengers to wait for bus signal
	*/
    //private final Condition waitPlace;
    /**
    * Arriaval Terminal Transfer Quay  Conditional variable to  signal BusDriver its full 
	*/
    private final Condition waitFull;
    /**
    *  Arriaval Terminal Transfer Quay Conditional variable to announce bus is going FOWARD
    */
    private final Condition waitAnnouncment;
    /**
    *  Arriaval Terminal Transfer Quay Conditional variable to wait to enter in bus
    */
    private final Condition waitEnterBus;
    /**
    * Arriaval Terminal Transfer Quay  variable determine bus Capacity
	*/
    private int busSize;
    /**
    * Arriaval Terminal Transfer Quay variable to count all passengers
	*/
   // private int passengers = 0;
      /**
    * Arriaval Terminal Transfer Quay  variable to count passengers In the bus
	*/
    //private int passengersInside = 0;
    /**
    * Arriaval Terminal Transfer Quay  variable to count passengers entering
    */
    private int passengersEntering = 0;
    /**
    * Arriaval Terminal Transfer Quay  variable to determine if cycle ended
	*/
    private Boolean endOfDay = false;
    /**
     * List of passengers in the waiting line.
     */
    private List<Integer> passengerQueue;
    
    /**
     * Lis tof passengers inside the bus.
     */
    private List<Integer> insidePassengers;
    
    /**
     * Indication that the bus is ready to travel.
     */
    private Boolean busTravelling;
    
    
    
    
    
    /** 
     * General Repository
    */
     private GeneralRepository rep;
    
    
    /**
	* Arriaval Terminal Transfer Quay shared Memomry constructor
	* 
	* @param busSize
	* @param rep
	*/
    public ArraivalTerminalTransferQuay(GeneralRepository rep) {
        rl = new ReentrantLock(true);
        this.busSize=global.BUS_SIZE;
        //this.waitPlace = rl.newCondition();
        this.waitFull = rl.newCondition();
        this.waitAnnouncment = rl.newCondition();
        this.waitEnterBus = rl.newCondition();
        
        passengerQueue = new ArrayList<>();
        insidePassengers = new ArrayList<>();
        busTravelling = false;
        
        
        this.rep = rep;

    }

    
    public void departureTime() {
        rl.lock();
        try {
            waitFull.signal();
        } catch(Exception ex) {
        } finally {
            rl.unlock();
        }
    }
    /**
	*  Passenger at the AT_THE_ARRIVAL_TRANSFER_TERMINAL state and waiting to take a Bus 
	*  @param passengerID
    */

    @Override
    public void takeABus(int passengerID){
        rl.lock();
        try{
            passengerQueue.add(passengerID);

            Passenger passenger = (Passenger) Thread.currentThread();
            rep.passJoinBusQueue(passenger.getPassengerID());

            //before blocking the 3 guy wakes up the BD
            if(passengerQueue.size() == busSize)
                waitFull.signal();

            while((busTravelling && passengerQueue.contains(passengerID)) || passengerQueue.contains(passengerID))
                waitAnnouncment.await();
            
            if(passengerQueue.size() == busSize)
                waitFull.signal();

        }catch(Exception ex){}
        finally {
            rl.unlock();
        }
     }

     /**
	*  Passenger at the AT_THE_ARRIVAL_TRANSFER_TERMINAL state and entering the Bus going to ENTERING_THE_DEPARTURE_TERMINAL
	*  @param passengerID
    */
    @Override
    public void enterTheBus(int passengerID){
        rl.lock();
        try{
            insidePassengers.add(passengerID);

            Passenger passenger = (Passenger) Thread.currentThread();
            rep.passSitInBus(passenger.getPassengerID());
            
            if (insidePassengers.size() == passengersEntering) {
                waitEnterBus.signalAll();
            }
        }catch(Exception ex){}
        finally {
            rl.unlock();
        }
    }
    /**
	*  Bus Driver at the PARKING_AT_THE_ARRIVAL_TERMINAL and determing if needs to go FOWARD
	*  @return BusdriverAction
    */
    @Override
    public BusDriverAction hasDaysWorkEnded(){
        rl.lock();
        try {

            int count = 0;
            busTravelling = false;

            rep.driverParkingArrivalTerminal();

            if(passengerQueue.size() > 0) {
                waitAnnouncment.signalAll();
            } 

            while(passengerQueue.size() != busSize && !endOfDay) {
                waitFull.awaitNanos(10 * 1000000);
                count++;
                if(count > 10 && passengerQueue.size() > 0)
                    break;
            }
            return endOfDay ? BusDriverAction.dayEnded : BusDriverAction.goToDepartureTerminal;

        } catch (Exception e) {return BusDriverAction.stayParked;}
        finally{
            rl.unlock();
        }

    }

      /**
	*  Bus Driver at the PARKING_AT_THE_ARRIVAL_TERMINAL and next action goToDepartureTerminal 
	*  @return BusdriverAction
    */
    @Override
    public int annoucingBusBoarding() {
		rl.lock();
		try {  
            
            busTravelling = true;
            
            passengersEntering = passengerQueue.size() > busSize ? busSize : passengerQueue.size();
            
            insidePassengers.clear();
            
            System.out.println("A ANUNCIAR PARTIDA");
            for(int i = 0; i < busSize && passengerQueue.size() > 0; i++) {
                passengerQueue.remove(0);
                waitAnnouncment.signal();
            }
            while(insidePassengers.size() != passengersEntering) {
                waitEnterBus.await();
            }

            if(insidePassengers.size() > 0)
                rep.driverDrivingForward();

            return insidePassengers.size();


		} catch (Exception ex) {
			System.out.println(ex);
            return 0;
		} finally {
			rl.unlock();
		}
    }
    

    @Override
    public void endOfDay() {
        rl.lock();
        endOfDay = true;
        waitFull.signal();
        rl.unlock();
        
    }
}
