package monitors;
import threads.*;
import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


import model.*;
import threads.Passenger;
import interfaces.*;

public class ArraivalTerminalTransferQuay implements IArraivalTerminalTransferQPassenger,IArraivalTerminalTransferQBusDriver{
    private final ReentrantLock rl;
	private final Condition waitLine;
	private final Condition waitFull;
	private final Condition waitAnnouncement;
	private final Condition waitEnter;
	private int flightCount;
	private int maxFlights;
	private int busSize;
	private int passengers = 0;
	private int passengersInside = 0;
	private int passengersEntering = 0;
    public ArraivalTerminalTransferQuay(int busSize,int maxFlights){
        rl = new ReentrantLock(true);
		waitLine = rl.newCondition();
		waitFull = rl.newCondition();
		waitAnnouncement = rl.newCondition();
		waitEnter = rl.newCondition();
		this.busSize = busSize;
		this.maxFlights = maxFlights;
    }
    /*
    public void departureTime() {
        rl.lock();
        try {
            //cBusDriver.signal();
            timeout = true;
        } catch(Exception ex) {
        } finally {
            rl.unlock();
        }
       
    }
    */
    public void setFlight(int nFlight){
		flightCount = nFlight+1;
	}
    @Override
    public void takeABus(Passenger p){
        //the driver is waken up the first time by the operation takeABus of the passenger 
        //who arrives at the transfer terminal and finds out her place in the waiting queue
        // equals the bus capacity, or when the departure time has been reached
        rl.lock();
        try{  
            //before blocking the 3 guy wakes up the BD
            passengers++;
         
            while(passengersEntering >= busSize) {
                waitLine.await();
            }
            passengersEntering++;
            if (passengersEntering == busSize) {
                waitFull.signal();
            }
            waitAnnouncement.await();
            
        }catch(Exception ex){}
        finally {
            rl.unlock();
        } 
     }
    @Override
    public void enterTheBus(){
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
            busIsFull.await();
            System.out.println("bus queue in size: "+ enterInBus.size());
            
            if(enterInBus.size() == busSize ){
                System.out.println("Bus driving goging foward: "+enterInBus.size());
                return BusDriverAction.goToDepartureTerminal;
                
            }
           
            System.out.println("Bus driver waiting for bus full");
            return BusDriverAction.stayParked;
        
                       
        } catch (Exception e) {return BusDriverAction.stayParked;}
        finally{
            rl.unlock();
        }
      
    }
    @Override
    public boolean annoucingBusBoarding() {
		rl.lock();
		try {
            System.out.println("BUS AS ARRIVED AND WAITING FOR PASSENGERS TO ENTER");
            cPassWaitingToEnter.signalAll();
            //podem vir para o bus
            
            //LIMPAR QUEUe porque nao ha forma de tirar passageiros no departure terminal
            enterInBus.clear();
            return true;
			
		} catch (Exception ex) {
			return true;
		} finally {
			rl.unlock();
		}
	}
}
