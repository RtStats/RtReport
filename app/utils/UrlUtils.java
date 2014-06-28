package utils;

import truyen.common.bo.truyen.AuthorBo;
import truyen.common.bo.truyen.BookBo;
import truyen.common.bo.truyen.CategoryBo;
import truyen.common.bo.truyen.ChapterBo;
import truyen.common.util.TruyenUtils;

public class UrlUtils {

    public static String url(AuthorBo author) {
        String name = TruyenUtils.denormalizeTCVN6909(author.getName());

        name = name.replaceAll("\\W+", "-");
        name = name.replaceAll("^\\-+", "").replaceAll("\\-+$", "").replaceAll("\\-", "-");
        name = name.toLowerCase();

        String strId = "a" + author.getId() + "-" + name;
        return controllers.routes.Application.author(strId).url();
    }

    public static String url(BookBo book) {
        String title = TruyenUtils.denormalizeTCVN6909(book.getTitle());

        title = title.replaceAll("\\W+", "-");
        title = title.replaceAll("^\\-+", "").replaceAll("\\-+$", "").replaceAll("\\-", "-");
        title = title.toLowerCase();

        String strId = "b" + book.getId() + "-" + title;
        return controllers.routes.Application.book(strId).url();
    }

    public static String url(ChapterBo chapter) {
        String title = TruyenUtils.denormalizeTCVN6909(chapter.getTitle());

        title = title.replaceAll("\\W+", "-");
        title = title.replaceAll("^\\-+", "").replaceAll("\\-+$", "").replaceAll("\\-", "-");
        title = title.toLowerCase();

        BookBo book = chapter.getBook();

        String strId = "b" + book.getId() + "-" + chapter.getIndex() + "-" + title;
        return controllers.routes.Application.book(strId).url();
    }

    public static String url(CategoryBo cat) {
        String title = TruyenUtils.denormalizeTCVN6909(cat.getTitle());

        title = title.replaceAll("\\W+", "-");
        title = title.replaceAll("^\\-+", "").replaceAll("\\-+$", "").replaceAll("\\-", "-");
        title = title.toLowerCase();

        String strId = "c" + cat.getId() + "-" + title;
        return controllers.routes.Application.category(strId).url();
    }
}
