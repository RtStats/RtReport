package truyen.worker;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import truyen.common.bo.worker.WorkerBo;
import truyen.common.bo.worker.WorkerDao;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;

public class ControllerWorker extends UntypedActor {

    private Lock lock = new ReentrantLock();

    @Override
    public void onReceive(Object msg) throws Exception {
        if (lock.tryLock(5, TimeUnit.SECONDS)) {
            try {
                WorkerBo[] workers = WorkerDao.getAllWorkers();
                for (WorkerBo worker : workers) {
                    ActorRef actorRef = WorkerRegistry.ENGINES.get(worker.getEngine());
                    if (actorRef != null) {
                        actorRef.tell(worker, this.getSelf());
                    }
                }
            } finally {
                lock.unlock();
            }
        }
    }

}
