package truyen.worker.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jodd.jerry.Jerry;
import jodd.jerry.JerryFunction;
import truyen.common.bo.truyen.BookBo;
import truyen.common.bo.truyen.ChapterBo;
import truyen.common.bo.worker.WorkerBo;
import truyen.worker.BaseWorker;
import truyen.worker.Utils;

import com.github.ddth.commons.utils.DPathUtils;

public class TungHoanhEngine extends BaseWorker {

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<Map<String, Object>> extractChapterList(WorkerBo worker, BookBo book) {
        String html = Utils.fetchUrl(worker.getUrl());
        final List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        Jerry doc = Jerry.jerry(html);
        doc.$("div#table-cm > li").each(new JerryFunction() {
            @Override
            public boolean onNode(Jerry node, int index) {
                Jerry nodeTitle = node.$("div.name > a");
                Map<String, Object> nodeData = new HashMap<String, Object>();
                nodeData.put(CHAPTER_METADATA_INDEX, index + 1);
                nodeData.put(CHAPTER_METADATA_TITLE, nodeTitle.text());
                nodeData.put(CHAPTER_METADATA_URL, nodeTitle.attr("href"));
                result.add(nodeData);
                return true;
            }
        });
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Map<String, Object> extractChapterContent(WorkerBo worker, BookBo book,
            Map<String, Object> chapterMetadata) {
        String chapterUrl = DPathUtils
                .getValue(chapterMetadata, CHAPTER_METADATA_URL, String.class);
        String[] tokens = chapterUrl.split("/");
        String chapterContentUrl = tokens[0] + "//" + tokens[2] + "/chapter/"
                + tokens[tokens.length - 1];
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(CHAPTER_URL, chapterMetadata.get(CHAPTER_METADATA_URL));
        result.put(CHAPTER_TITLE, chapterMetadata.get(CHAPTER_METADATA_TITLE));

        String content = Utils.fetchUrl(chapterContentUrl);
        content = content.replaceAll("<\\s*\\/[pP]\\s*>", "</p><br/>")
                .replaceAll("<\\s*[bB][rR]\\s*\\/?>", "\n").replaceAll("[\\n\\r]+", "\n\n");
        Jerry doc = Jerry.jerry(content);
        content = doc.text().replaceAll("\\“", "\"").replaceAll("\\”", "\"")
                .replaceAll("\\…", "...");
        result.put(CHAPTER_CONTENT, content);

        result.put(CHAPTER_TYPE, ChapterBo.TYPE_TRANSLATE);

        return result;
    }
}
