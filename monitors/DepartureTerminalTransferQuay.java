package monitors;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import interfaces.IDepartureTerminalTransferQBusDriver;
import interfaces.IDepartureTerminalTransferQPassenger;
public class DepartureTerminalTransferQuay implements IDepartureTerminalTransferQBusDriver , IDepartureTerminalTransferQPassenger {
    private final ReentrantLock rl;
    private int nPassengers = 0;
    private final Condition passengersOut;
    private final Condition waitingRide;
    public  DepartureTerminalTransferQuay(){
        rl = new ReentrantLock(true);
        waitingRide = rl.newCondition();
        passengersOut = rl.newCondition();
      

    }
    
    @Override
    public void waitRide(){
        rl.lock();
        try{
            System.out.println("");
            nPassengers++;
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
    public void parkTheBusAndLetPassOff() {
        rl.lock();
        try {
            System.out.println("Bus parked and let off");
            waitingRide.signalAll();
            passengersOut.await();
            System.out.println("Bus going backward");
        } catch(Exception ex) {}
        finally {
            rl.unlock();
        }
    }

}
