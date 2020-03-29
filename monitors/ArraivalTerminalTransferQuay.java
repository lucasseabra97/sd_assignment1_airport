package monitors;

import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import model.*;
import threads.*;
import interfaces.*;

public class ArraivalTerminalTransferQuay implements IArraivalTerminalTransferQPassenger,IArraivalTerminalTransferQBusDriver{
    private final ReentrantLock rl;
    private final Condition waitPlace;
    private final Condition waitFull;
    private final Condition waitAnnouncment;
    private final Condition waitEnterBus;
    private int busSize;
    private int passengers = 0;
    private int passengersInside = 0;
    private int passengersEntering = 0;
    private Boolean endOfDay = false;

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
