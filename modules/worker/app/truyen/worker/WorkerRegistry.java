package truyen.worker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import play.Logger;
import truyen.worker.engine.TungHoanhEngine;
import akka.actor.ActorRef;

import com.github.ddth.plommon.utils.AkkaUtils;

public class WorkerRegistry {

    public final static Map<String, ActorRef> ENGINES = new HashMap<String, ActorRef>();
    public final static List<String> ENGINE_LIST = new ArrayList<String>();

    public static void initRegistry() {
        ENGINES.clear();
        Logger.info("Initializing registry...");
        ENGINES.put("TungHoanh.com", AkkaUtils.actorOf(TungHoanhEngine.class));
        Logger.info("\tAdded engine TungHoanh.com engine");

        ENGINE_LIST.clear();
        ENGINE_LIST.addAll(ENGINES.keySet());
    }

    public static void destroyRegistry() {
        ENGINES.clear();
        ENGINE_LIST.clear();
    }
}
