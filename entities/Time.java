package entities;


import shared_regions.*;

public class Time extends Thread {
    
    private int time = 0; 

    private final ArraivalTerminalTransferQuay arrivalTerminalTransferQuay;
    private final ArraivalLounge arrivalLounge;

    public Time(ArraivalTerminalTransferQuay arrivalTerminalTransferQuay, ArraivalLounge arrivalLounge) {
        this.arrivalTerminalTransferQuay = arrivalTerminalTransferQuay;
        this.arrivalLounge = arrivalLounge;
    }
    
    @Override
    public void run() {
        
        while (true) {

            try {
                /* System.out.println("TEMPO -> " + time); */
                Thread.sleep(50);
                time += 50;

                if(time % 500 == 0) {
                    arrivalTerminalTransferQuay.departureTime();
                }

                if(time % 2400 == 0) {
                    if(arrivalTerminalTransferQuay.endOfDay()) {
                        arrivalLounge.endOfDay();
                        break;
                    }
                }

            } catch (Exception e) {}

        }
        
    }
    
    
}