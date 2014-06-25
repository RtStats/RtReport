package controllers.admin;

import play.api.templates.Html;
import play.data.Form;
import play.i18n.Messages;
import play.libs.F.Function0;
import play.libs.F.Promise;
import play.mvc.Result;
import truyen.common.Constants;
import truyen.common.bo.truyen.AuthorBo;
import truyen.common.bo.truyen.BookBo;
import truyen.common.bo.truyen.CategoryBo;
import truyen.common.bo.truyen.ChapterBo;
import truyen.common.bo.truyen.TruyenDao;
import truyen.common.util.FormUtils;

import compisitions.admin.AuthRequired;

import controllers.common.BaseController;
import forms.admin.FormAddChapter;

@AuthRequired(userGroups = { Constants.USER_GROUP_ADMINISTRATOR, Constants.USER_GROUP_MODERATOR })
public class Chapter extends BaseController {

    public final static String VIEW_NEW_CHAPTERS = SECTION + "new_chapters";

    /*
     * Handles GET:/newChapters
     */
    public static Promise<Result> newChapters() {
        Promise<Result> promise = Promise.promise(new Function0<Result>() {
            public Result apply() throws Exception {
                ChapterBo[] newChapters = TruyenDao.getNewChapters();
                AuthorBo[] authors = TruyenDao.getAllAuthors();
                CategoryBo[] categories = TruyenDao.getAllCategories();
                BookBo[] books = TruyenDao.getAllBooks();
                Html html = render(VIEW_NEW_CHAPTERS, newChapters, books, categories, authors);
                return ok(html);
            }
        });
        return promise;
    }

    /*
     * Handles POST:/updateChapter
     */
    public static Promise<Result> updateChapterSubmit(final int bookId, final int chapterIndex) {
        Promise<Result> promise = Promise.promise(new Function0<Result>() {
            public Result apply() throws Exception {
                ChapterBo chapter = TruyenDao.getChapter(bookId, chapterIndex);
                if (chapter == null) {
                    flash(VIEW_NEW_CHAPTERS,
                            Constants.FLASH_MSG_PREFIX_ERROR
                                    + Messages.get("error.chapter.not_found"));
                } else {
                    Form<FormAddChapter> form = Form.form(FormAddChapter.class).bindFromRequest();
                    if (form.hasErrors()) {
                        flash(VIEW_NEW_CHAPTERS,
                                Constants.FLASH_MSG_PREFIX_ERROR
                                        + FormUtils.joinFormErrorMessages(form));
                    } else {
                        FormAddChapter formData = form.get();
                        chapter.setTitle(formData.title).setContent(formData.content)
                                .setType(formData.chapterType).setActive(formData.isActive);
                        chapter = TruyenDao.update(chapter);
                        if (chapter.isActive()) {
                            chapter = TruyenDao.activateChapter(chapter);
                        }
                        flash(VIEW_NEW_CHAPTERS,
                                Messages.get("msg.edit_chapter.done", chapter.getTitle()));
                    }
                }
                return redirect(controllers.admin.routes.Chapter.newChapters().url());
            }
        });
        return promise;
    }
}
