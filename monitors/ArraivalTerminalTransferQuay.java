package monitors;

import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


import model.*;
import threads.Passenger;
import interfaces.*;

public class ArraivalTerminalTransferQuay implements IArraivalTerminalTransferQPassenger,IArraivalTerminalTransferQBusDriver{
    private final ReentrantLock rl;
    private final Queue<Passenger> waitingForBus; 
    private final Queue<Passenger> enterInBus;
    private int busSize = 0;
    private final Condition cBusDriver; //variavel de condiçao para acordar o busdriver
    //private final Condition waitingAnnouncment;//variavel de condiçao para acordar o passageiro qd o bus chega
    private final Condition cPassWaitingToEnter; //variavel de condiçao para acordar o passageir->mimica
    private final Condition busIsFull; //3 passenger wakes up the busDriver to go to Departure
    public ArraivalTerminalTransferQuay(int busSize){
        rl = new ReentrantLock(true);
        this.busSize=busSize;
        this.busIsFull = rl.newCondition();
        this.cBusDriver = rl.newCondition();
        this.cPassWaitingToEnter = rl.newCondition();
        waitingForBus = new LinkedList<>();
        enterInBus = new LinkedList<>();
    }

    // public void departureTime(timeout) {
        
    //     cBusDriver.signal();
        
    // }

    @Override
    public void takeABus(Passenger p){
        //the driver is waken up the first time by the operation takeABus of the passenger 
        //who arrives at the transfer terminal and finds out her place in the waiting queue
        // equals the bus capacity, or when the departure time has been reached
        rl.lock();
        try{  
            //before blocking the 3 guy wakes up the BD
            waitingForBus.add(p);
            if(waitingForBus.size()>=busSize)
                cPassWaitingToEnter.await(); 
            //se ha 3 passageiros entao embarcam
            if(waitingForBus.size()==busSize){
                System.out.println("Passenger waiting for bus"+p.toString());
                cBusDriver.signal();//acorda o BD mas adormece a seguir à espera do sinal do BD para os 3 entrarem
      
            }
            
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
            
            //sai da fila de espera para entrar no bus
           
           if(enterInBus.size()< busSize){
              enterInBus.add(waitingForBus.remove());
           }
           if (enterInBus.size() == busSize ){
                System.out.println("Passenger signal aqvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvui");
                busIsFull.signal();// sinaliza o busDriver que já está cheio e podem ir para o departure
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
