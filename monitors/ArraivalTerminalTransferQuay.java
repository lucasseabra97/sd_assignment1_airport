package monitors;

import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import model.*;
import threads.Passenger;
import interfaces.*;

public class ArraivalTerminalTransferQuay implements IArraivalTerminalTransferQPassenger,IArraivalTerminalTransferQBusDriver{
    private final ReentrantLock rl;
    private final Queue<Passenger> waitingForBus = new LinkedList<>();
    private int busSize;
    private int passengersIn;
    private int passengersLeftTogo;
    private final Condition cBusDriver; //variavel de condiçao para acordar o busdriver
    private final Condition waitingAnnouncment;//variavel de condiçao para acordar o passageiro qd o bus chega
    private final Condition cPassWaitingToEnter; //variavel de condiçao para acordar o passageir->mimica
    public ArraivalTerminalTransferQuay(int busSize){
        rl = new ReentrantLock(true);
        this.busSize=busSize;
        this.waitingAnnouncment = rl.newCondition();
        this.cBusDriver = rl.newCondition();;
        this.cPassWaitingToEnter = rl.newCondition();
        
    }




    @Override
    public void takeABus(Passenger p){
        //the driver is waken up the first time by the operation takeABus of the passenger 
        //who arrives at the transfer terminal and finds out her place in the waiting queue
        // equals the bus capacity, or when the departure time has been reached
        rl.lock();
        try{
            //nPassWaiting++;
            waitingForBus.add(p);
            cPassWaitingToEnter.await();

            // while(waitingForBus.size()>busSize){
            //     
            // }
            //se ha 3 passageiros entao embarcam
            
            // if(waitingForBus.size()==busSize){
            //     cBusDriver.signal();
            //     cPassWaitingToEnter.await();
            // }
            //se ha 2 ou menos passageiros para embarcar entao mete os no autocarro 
            // if(waitingForBus.size() <busSize){
            

            //waitingAnnouncment()
            // }
            
        }catch(Exception ex){}
        finally {
            rl.unlock();
        } 
     }
    @Override
    public void enterTheBus(){
        //and is waken up by the operation announcingBusBoarding of the driver to mimic her entry in the bus
        //Na operação enterTheBus, cada passageiro, ao ser avisado pelo motorista que pode entrar no 
        //autocarro, entra e senta-se num lugar disponível para efectuar a viagem de transferência 
        rl.lock();
        try{
            while(waitingForBus.size()>busSize){
                cPassWaitingToEnter.await(); 
            }
            //se ha 3 passageiros entao embarcam
            
            if(waitingForBus.size()==busSize){
                cBusDriver.signalAll();
                passengersIn=3;
                cPassWaitingToEnter.await();
            }
            //se ha 2 ou menos passageiros para embarcar entao mete os no autocarro 
            if(waitingForBus.size() <busSize){
                
            }
            passengersIn++;
            waitingForBus.remove();
            //se ja entraram 3 e eram 5 na queue faltam 2 para enbarcar
            passengersLeftTogo = waitingForBus.size() - passengersIn; 
        }catch(Exception ex){}
        finally {
            rl.unlock();
        }
    }
    @Override
    public BusDriverAction hasDaysWorkEnded(){
        if(waitingForBus.size()>0){
            if(passengersIn == busSize){
               return BusDriverAction.goToDepartureTerminal;
            }
            //falta aqui qlq coisa
        }
        return null;
    }
    @Override
    public boolean annoucingBusBoarding() {
		rl.lock();
		try {
            System.out.println("BUS AS ARRIVED AND WAITING FOR PASSENGERS TO ENTER");
            //LIMPAR QUEUE
            waitingAnnouncment.signalAll();  
			// waitAnnouncement.signalAll();
			// waitEnter.await();
			// passengers = passengers - passengersInside;
            return true;
			// return passengersInside;
		} catch (Exception ex) {
			return true;
		} finally {
			rl.unlock();
		}
	}
}
