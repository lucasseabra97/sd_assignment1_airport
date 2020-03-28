package threads;
import monitors.*;

public class Time extends Thread {

    private int time = 0; 

    private final ArraivalTerminalTransferQuay arrivalTerminalTransferQuay;

    public Time(ArraivalTerminalTransferQuay arrivalTerminalTransferQuay) {
        this.arrivalTerminalTransferQuay = arrivalTerminalTransferQuay;
    }

    @Override
    public void run() {
        while (true) {  
            try {
                System.out.println("aquuiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii"); 
                //Thread.sleep(50);
                int timeout = 1000;
                Thread.sleep(timeout);
                
                //arrivalTerminalTransferQuay.departureTime();

            } catch (Exception e) {}
        }
    }

}