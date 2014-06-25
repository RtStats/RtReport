package utils;

import truyen.common.bo.truyen.CategoryBo;
import truyen.common.util.TruyenUtils;

public class UrlUtils {
    public static String url(CategoryBo cat) {
        String title = TruyenUtils.denormalizeTCVN6909(cat.getTitle());

        title = title.replaceAll("\\W+", "-");
        title = title.replaceAll("^\\-+", "").replaceAll("\\-+$", "").replaceAll("\\-", "-");
        title = title.toLowerCase();

        String strId = "c" + cat.getId() + "-" + title;
        return controllers.routes.Application.category(strId).url();
    }
}
