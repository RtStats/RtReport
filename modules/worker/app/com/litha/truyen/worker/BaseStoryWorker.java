package com.litha.truyen.worker;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import akka.actor.UntypedActor;

public abstract class BaseStoryWorker extends UntypedActor {

    private Lock lock = new ReentrantLock();

    @Override
    public void onReceive(Object msg) throws Exception {
        if (msg instanceof StoryMessage) {
            if (lock.tryLock(5, TimeUnit.SECONDS)) {
                try {
                    StoryMessage msgStory = (StoryMessage) msg;
                    process(msgStory);
                } finally {
                    lock.unlock();
                }
            }
        }
    }

    protected void process(StoryMessage msg) {
        String html = Utils.fetchUrl(msg.url);
        System.out.println("Page size: " + html.length());
        List<Map<String, Object>> chapterList = extractStoryChapters(html);
        System.out.println(chapterList);
    }

    protected abstract List<Map<String, Object>> extractStoryChapters(String html);
}
