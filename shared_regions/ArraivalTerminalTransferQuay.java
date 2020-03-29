package shared_regions;

import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import commonInfra.*;
import entities.*;
import interfaces.*;

public class ArraivalTerminalTransferQuay implements IArraivalTerminalTransferQPassenger,IArraivalTerminalTransferQBusDriver{
    /**
    * Arriaval Terminal Transfer Quay for locking 
	*/
    private final ReentrantLock rl;
    /**
    * Arriaval Terminal Transfer Quay  Conditional variable for Passengers to wait for bus signal
	*/
    private final Condition waitPlace;
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
    private int passengers = 0;
      /**
    * Arriaval Terminal Transfer Quay  variable to count passengers In the bus
	*/
    private int passengersInside = 0;
    /**
    * Arriaval Terminal Transfer Quay  variable to count passengers entering
    */
    private int passengersEntering = 0;
    /**
    * Arriaval Terminal Transfer Quay  variable to determine if cycle ended
	*/
    private Boolean endOfDay = false;

     /**
	* Arriaval Terminal Transfer Quay shared Mem.
	* 
	* @param busSize
	*
	*/
    public ArraivalTerminalTransferQuay(int busSize) {
        rl = new ReentrantLock(true);
        this.busSize=busSize;
        this.waitPlace = rl.newCondition();
        this.waitFull = rl.newCondition();
        this.waitAnnouncment = rl.newCondition();
        this.waitEnterBus = rl.newCondition();
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
            //before blocking the 3 guy wakes up the BD
            passengers++;
            while(passengersEntering >= busSize) {
                waitPlace.await();
            }
            passengersEntering++;
            if (passengersEntering == busSize) {
                waitFull.signal();
            }
            waitAnnouncment.await();

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
            passengersInside++;
            if (passengersInside == passengersEntering) {
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

            if(passengers > 0) {
                waitPlace.signalAll();
            }
            waitFull.await();
            if(passengers >0){
                return BusDriverAction.goToDepartureTerminal;
            }
            else if(endOfDay){
                return BusDriverAction.dayEnded;
            }
            else{
                return BusDriverAction.stayParked;
            }

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
    public boolean annoucingBusBoarding() {
		rl.lock();
		try {
            System.out.println("A ANUNCIAR PARTIDA");
			waitAnnouncment.signalAll();
			waitEnterBus.await();
            passengers = passengers - passengersEntering;
            passengersEntering = 0;
            passengersInside = 0;
            return true;

		} catch (Exception ex) {
			return true;
		} finally {
			rl.unlock();
		}
    }
    

   
    public Boolean endOfDay() {
        rl.lock();
        try {
            endOfDay = true;
            waitFull.signal();
            return passengers == 0;
        } catch(Exception ex) {
            return true;
        } finally {
            rl.unlock();
        }
    }
}
