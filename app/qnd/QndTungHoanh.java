package qnd;

import truyen.worker.StoryMessage;
import truyen.worker.engine.TungHoanhEngine;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class QndTungHoanh {

    public static void main(String[] args) throws InterruptedException {
        ActorSystem system = ActorSystem.create("MySystem");
        ActorRef tunghoanhActor = system.actorOf(Props.create(TungHoanhEngine.class),
                "tunghoanh");

        StoryMessage msg = new StoryMessage();
        msg.url = "http://tunghoanh.vn/than-toa-Fpaaaab.html";
        msg.chapter = 0;
        tunghoanhActor.tell(msg, ActorRef.noSender());

        Thread.sleep(5000);
        system.shutdown();

        // final String url = "http://tunghoanh.vn/ma-thien-ky-fpaaaab.html";
        //
        // HttpBrowser browser = new HttpBrowser();
        // HttpRequest request = HttpRequest.get(url);
        // HttpResponse response = browser.sendRequest(request);
        // String html = browser.getPage();
        // response.close();
        // browser.close();
        //
        // Jerry doc = Jerry.jerry(html);
        // Jerry selection = doc.$("div#table-cm > li");
        // System.out.println(selection.get(0).getHtml());
        //
        // // HttpRequest request = new HttpRequest(url).fetchSizeLimit(1024);
        // // HttpResponse response = request.doGet();
        // // System.out.println(response.headers());
        // // System.out.println(response.content());
    }
}
