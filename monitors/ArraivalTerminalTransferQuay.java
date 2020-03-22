package monitors;

import java.util.concurrent.locks.ReentrantLock;
import model.*;
import interfaces.*;

public class ArraivalTerminalTransferQuay implements IArraivalTerminalTransferQPassenger,IArraivalTerminalTransferQBusDriver{
    private final ReentrantLock rl;
    // private int numPassengers =0;
    // private int passengersGoingIn=0;
    // private int passengersIn=0;
     private int busSize;
     
    public ArraivalTerminalTransferQuay(int busSize){
        rl = new ReentrantLock(true);

        this.busSize=busSize;
        // waitingBus = rl.newCondition();
        // waitingBusFull = rl.newCondition();
        // waitingEnterBus = rl.newCondition();
        // waitingSignalFromBusDriver = rl.newCondition();

    }

    @Override
    public BusDriverAction hasDaysWorkEnded(){
        return null;
    }
    @Override
    public void takeABus(){
    // rl.lock();
    //     try{
    //         numPassengers++;
    //         while(passengersGoingIn >=busSize)
    //             waitingBus.await();
    //         passengersGoingIn++;

    //         if(passengersGoingIn == busSize)
    //             waitingBusFull.signal();
    //         waitingSignalFrombusdriver.await();
            
    //     }catch(Exception ex){}
    //     finally {
    //         rl.unlock();
    //     } 
     }
    @Override
    public void enterTheBus(){
    //     rl.lock();
    //     try{
    //        passengersIns++;
    //        if(passengersIn == passengersEntering)
    //            waitingEnterBus.signal();
    //     }catch(Exception ex{}
    //     finally {
    //         rl.unlock();
    //     }
    }
    // @Override
    // public int annoucingBusBoarding() {
	// 	rl.lock();
	// 	try {
	// 		waitAnnouncement.signalAll();
	// 		waitEnter.await();
	// 		passengers = passengers - passengersInside;

	// 		return passengersInside;
	// 	} catch (Exception ex) {
	// 		return 0;
	// 	} finally {
	// 		rl.unlock();
	// 	}
	// }
}
