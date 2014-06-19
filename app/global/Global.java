package global;

import java.util.concurrent.TimeUnit;

import play.Application;
import play.GlobalSettings;
import play.Logger;
import truyen.worker.ControllerWorker;
import truyen.worker.WorkerRegistry;
import akka.actor.Cancellable;

import com.github.ddth.plommon.utils.AkkaUtils;

public class Global extends GlobalSettings {

    private Cancellable controllerWorker;

    @Override
    public void onStart(Application app) {
        super.onStart(app);
        WorkerRegistry.initRegistry();
        controllerWorker = AkkaUtils.schedule(ControllerWorker.class, 5, TimeUnit.SECONDS, 5,
                TimeUnit.SECONDS, "");
    }

    @Override
    public void onStop(Application app) {
        try {
            WorkerRegistry.destroyRegistry();
        } catch (Exception e) {
            Logger.error(e.getMessage(), e);
        }

        try {
            controllerWorker.cancel();
        } catch (Exception e) {
            Logger.error(e.getMessage(), e);
        }

        try {
            AkkaUtils.actorSystem().shutdown();
        } catch (Exception e) {
            Logger.error(e.getMessage(), e);
        }

        super.onStop(app);
    }
}
