package monitors;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import interfaces.IDepartureTerminalTransferQBusDriver;
import interfaces.IDepartureTerminalTransferQPassenger;
public class DepartureTerminalTransferQuay implements IDepartureTerminalTransferQBusDriver , IDepartureTerminalTransferQPassenger {
    private final ReentrantLock rl;
    private int nPassengers = 0;
    private final Condition passengersOut;
    private boolean busArrived ;
    private final Condition waitingRide;
    private int counterpassengersOut=0; 
    public  DepartureTerminalTransferQuay(){
        rl = new ReentrantLock(true);
        waitingRide = rl.newCondition();
        passengersOut = rl.newCondition();
        System.out.println("DEPARTURE TERMINAL RUN");
        this.busArrived = false; 

    }
    
    @Override
    public void waitRide(){
        //System.out.println("waiting ride");
        rl.lock();
        try{
            System.out.println("waiting ride");
            nPassengers++;
            while(!busArrived)
                waitingRide.await();
           
            

        }catch(Exception e){}
        finally{
            rl.unlock();
        }

    }
    
    @Override
    public void leaveTheBus(){
        rl.lock();
        try { 
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
    @Override
    public void parkTheBusAndLetPassOff( int busSize) {
        rl.lock();
        try {
            
            
            System.out.println("Bus parked and let off"+ nPassengers) ;
            busArrived = true; 
            waitingRide.signalAll();
            passengersOut.await();
            System.out.println("Bus going backward");
        } catch(Exception ex) {}
        finally {
            rl.unlock();
        }
    }

}
