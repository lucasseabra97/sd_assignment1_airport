package threads;
import monitors.*;

public class Time extends Thread {

    private int time = 0; 

    private final ArrivalTerminalTransferQuay arrivalTerminalTransferQuay;

    public Time(ArrivalTerminalTransferQuay arrivalTerminalTransferQuay){
        this.arrivalTerminalTransferQuay = arrivalTerminalTransferQuay;
    }

    @Override
    public void run() {
        try {
            /* System.out.println("TEMPO -> " + time); */
            //Thread.sleep(50);
            Thread.sleep(timeout);
            
            cBusDriver.signal();

        } catch (Exception e) {}

    }

}