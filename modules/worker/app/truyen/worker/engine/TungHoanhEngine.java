package truyen.worker.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import truyen.worker.BaseWorker;
import jodd.jerry.Jerry;
import jodd.jerry.JerryFunction;

public class TungHoanhEngine extends BaseWorker {

    @Override
    protected List<Map<String, Object>> extractStoryChapters(String html) {
        final List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        Jerry doc = Jerry.jerry(html);
        doc.$("div#table-cm > li").each(new JerryFunction() {
            @Override
            public boolean onNode(Jerry node, int index) {
                Jerry nodeTitle = node.$("div.name > a");
                Map<String, Object> nodeData = new HashMap<String, Object>();
                nodeData.put("chapter", index + 1);
                nodeData.put("title", nodeTitle.text());
                nodeData.put("url", nodeTitle.attr("href"));
                result.add(nodeData);
                return true;
            }
        });
        return result;
    }

}