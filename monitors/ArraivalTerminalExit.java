package monitors;
import interfaces.IArraivalTerminalExitPassenger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ArraivalTerminalExit implements IArraivalTerminalExitPassenger{

    private final ReentrantLock rl;
    private final Condition waitingEnd;
    
    private int passengers = 0;
    private int nrPassengers;
    private boolean goingHome;
  
    public ArraivalTerminalExit(int nrPassengers) {
        rl = new ReentrantLock(true);
        waitingEnd = rl.newCondition();
        this.nrPassengers = nrPassengers;
        this.goingHome = false;
    }
    @Override
    public boolean goHome(int npassengers) {
        rl.lock();
        try {
            boolean lastone = npassengers+passengers == nrPassengers;
            if(lastone ){
                goingHome = true;
                waitingEnd.signalAll();
            }
            while(!goingHome){
                waitingEnd.await();
            
            }
            passengers--;

            return lastone;
        } catch(Exception ex) {return false;}
        finally {
            rl.unlock();
        }
    }

    @Override
    public void awakePassengers(){
        rl.lock();
        try {
            goingHome = true;
            waitingEnd.signalAll();
            
        } catch (Exception e) {}
        finally{
            rl.unlock();
        }
    }


    @Override
    public int nPassengersDepartureAT(){
        rl.lock();
        try{
            return passengers;
        }catch(Exception ex){ return passengers;}
        finally{
            rl.unlock();
        }
    
    }
    @Override
    public void syncPassenger(){
        rl.lock();    
        try{
            passengers ++;
        }catch(Exception ex){}
        finally{
            rl.unlock();
        }
   
    }
   



}
