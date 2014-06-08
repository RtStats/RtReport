package global;

import play.Application;
import play.GlobalSettings;
import play.Logger;
import truyen.worker.WorkerRegistry;

public class Global extends GlobalSettings {
    @Override
    public void onStart(Application app) {
        super.onStart(app);
        WorkerRegistry.initRegistry();
    }

    @Override
    public void onStop(Application app) {
        try {
            WorkerRegistry.destroyRegistry();
        } catch (Exception e) {
            Logger.error(e.getMessage(), e);
        }

        super.onStop(app);
    }
}
