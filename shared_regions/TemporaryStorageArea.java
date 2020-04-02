package shared_regions;

import commonInfra.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import interfaces.ITemporaryStorageAreaPorter;


public class TemporaryStorageArea implements ITemporaryStorageAreaPorter{

    private final ReentrantLock rl;
    private List<Baggage> bags;

    public TemporaryStorageArea(){
        rl = new ReentrantLock();
        bags = new ArrayList<>();
    }

	@Override
	public void carryItToAppropriateStore(Baggage bag) {
        rl.lock();
        try {
            bags.add(bag);
            
        } catch (Exception e) {
            
        }finally{
            rl.unlock();
        }
		
	}

  
}
