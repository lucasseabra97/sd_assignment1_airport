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
    private int busSize;
    private final Condition cBusDriver; //variavel de condiçao para acordar o busdriver
    //private final Condition waitingAnnouncment;//variavel de condiçao para acordar o passageiro qd o bus chega
    private final Condition cPassWaitingToEnter; //variavel de condiçao para acordar o passageir->mimica
    private final Condition busIsFull; //3 passenger wakes up the busDriver to go to Departure
    public ArraivalTerminalTransferQuay(int busSize){
        rl = new ReentrantLock(true);
        this.busSize=busSize;
        this.waitingAnnouncment = rl.newCondition();
        this.cBusDriver = rl.newCondition();
        this.cPassWaitingToEnter = rl.newCondition();
        waitingForBus = new LinkedList<>();
        enterBus = new enterBus<>();
    }

    @Override
    public void takeABus(Passenger p){
        //the driver is waken up the first time by the operation takeABus of the passenger 
        //who arrives at the transfer terminal and finds out her place in the waiting queue
        // equals the bus capacity, or when the departure time has been reached
        rl.lock();
        try{ 
            waitingForBus.add(p);
            while(waitingForBus.size()>=busSize){
                cPassWaitingToEnter.await();
            }
            //se ha 3 passageiros entao embarcam
            if(waitingForBus.size()==busSize){
                cBusDriver.signal();
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
           Passenger p  = waitingForBus.remove();
           while(enterInBus.size()<= busSize){
              enterInBus.add(p);
           }
           busisFull.signal();// sinaliza o busDriver que já está cheio e podem ir para o departure

        }catch(Exception ex){}
        finally {
            rl.unlock();
        }
    }
    @Override
    public BusDriverAction hasDaysWorkEnded(){
        rl.lock();
        try {
            if (waitingForBus.size() >0)
                
            busIsFull.await();
            if(waitingForBus.size()>0){
                if(enterInBus.size() == busSize){
                    return BusDriverAction.goToDepartureTerminal;
                }
            }
            
        } catch (Exception e) {}
        finally{
            rl.unlock();
        }
      
    }
    @Override
    public boolean annoucingBusBoarding() {
		rl.lock();
		try {
            System.out.println("BUS AS ARRIVED AND WAITING FOR PASSENGERS TO ENTER");
            //podem vir para o bus
            cPassWaitingToEnter.signalAll();  
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
