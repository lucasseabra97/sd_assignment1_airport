package shared_regions;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import entities.Passenger;
import interfaces.IDepartureTerminalTransferQBusDriver;
import interfaces.IDepartureTerminalTransferQPassenger;



/**
 * Departure Terminal Transfer Quay memory region.
 * 
 * @author Lucas Seabra
 * @author Joao Monteiro
 */
public class DepartureTerminalTransferQuay implements IDepartureTerminalTransferQBusDriver , IDepartureTerminalTransferQPassenger {
    /**
    * Departure Terminal Transfer Quay for locking 
	*/
    private final ReentrantLock rl;
    /**
    *   Departure Terminal Transfer Quay variable to count passengers entering the bus
	*/
    private int nPassengers = 0;
    /**
    *  Departure Terminal Transfer Quay Conditional variable for waiting all passengers out the bus
	*/
    private final Condition passengersOut;
    /**
    *  Departure Terminal Transfer Quay boolean variable for waiting while bus has not arrived
	*/
    private boolean busArrived ;
        /**
    *  Departure Terminal Transfer Quay Conditional variable for waiting while bus has not arrived
	*/
    private final Condition waitingRide;
     /**
    *  Departure Terminal Transfer Quay Integer variable for counting all passengers out the bus
	*/
    private int counterpassengersOut=0; 
    /**
     * General Repository
     */

    private GeneralRepository rep;
    /**
	* Departure Terminal Transfer Quay  shared Memory constructor 
	* @param rep
	*/
    public  DepartureTerminalTransferQuay(GeneralRepository rep){
        rl = new ReentrantLock(true);
        waitingRide = rl.newCondition();
        passengersOut = rl.newCondition();
        System.out.println("DEPARTURE TERMINAL RUN");
        this.busArrived = false; 
        this.rep=rep;

    }
    

     /**
	 * Passenger at  TERMINAL_TRANSFER in the bus waiting for ride to complete  
	 * 
	 * 
	 */    
    @Override
    public void waitRide(){
        //System.out.println("waiting ride");
        rl.lock();
        try{
            Passenger passenger = (Passenger) Thread.currentThread();
            rep.passBusRide(passenger.getPassengerID());

            //System.out.println("waiting ride");
            nPassengers++;
            while(!busArrived)
                waitingRide.await();
           
            

        }catch(Exception e){}
        finally{
            rl.unlock();
        }

    }
     /**
	 * Passenger at  AT_THE_DEPARTURE_TRANSFER_TERMINAL and leaving the bus to ENTERING_THE_DEPARTURE_TERMINAL 
	 * 
	 * 
	 */
    @Override
    public void leaveTheBus(){
        rl.lock();
        try { 

            Passenger passenger = (Passenger) Thread.currentThread();
            rep.passLeaveBus(passenger.getPassengerID());

            nPassengers --;
            counterpassengersOut ++;
            if(nPassengers == 0){
                passengersOut.signal();
                System.out.println("Wake up bus to go backward");
            }
                

        } catch (Exception e) {}
    
        finally{
            rl.unlock();
        }
    
    }  

    /**
	 * Bus driver at  PARKING_AT_THE_DEPARTURE_TERMINAL and waiting until all passengers leave the bus and goes to DRIVING_BACKWARD 
	 * 
	 * 
	 */
    @Override
    public void parkTheBusAndLetPassOff( int busSize) {
        rl.lock();
        try {
            rep.driverParkingDepartureTerminal();
            
            System.out.println("Bus parked and let off"+ nPassengers) ;
            busArrived = true; 
            waitingRide.signalAll();
            passengersOut.await();
            rep.driverDrivingBackward();
            System.out.println("Bus going backward");
        } catch(Exception ex) {}
        finally {
            rl.unlock();
        }
    }

}
