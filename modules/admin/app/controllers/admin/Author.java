package controllers.admin;

import play.api.templates.Html;
import play.data.Form;
import play.i18n.Messages;
import play.libs.F.Function0;
import play.libs.F.Promise;
import play.mvc.Result;
import truyen.common.Constants;
import truyen.common.bo.truyen.AuthorBo;
import truyen.common.bo.truyen.TruyenDao;
import truyen.common.compisitions.AuthRequired;
import controllers.common.BaseController;
import forms.admin.FormAddAuthor;

@AuthRequired(userGroups = { Constants.USER_GROUP_ADMINISTRATOR, Constants.USER_GROUP_MODERATOR })
public class Author extends BaseController {

    public final static String VIEW_AUTHORS = SECTION + "authors";

    /*
     * Handles GET:/authors
     */
    public static Promise<Result> authors() {
        Promise<Result> promise = Promise.promise(new Function0<Result>() {
            public Result apply() throws Exception {
                AuthorBo[] authors = TruyenDao.getAllAuthors();
                Html html = render(VIEW_AUTHORS, (Object) authors);
                return ok(html);
            }
        });
        return promise;
    }

    /*
     * Handles POST:/addAuthor
     */
    public static Promise<Result> addAuthorSubmit() {
        Promise<Result> promise = Promise.promise(new Function0<Result>() {
            public Result apply() throws Exception {
                Form<FormAddAuthor> form = Form.form(FormAddAuthor.class).bindFromRequest();
                if (form.hasErrors()) {
                    flash(VIEW_AUTHORS, Constants.FLASH_MSG_PREFIX_ERROR
                            + form.error("name").message());
                } else {
                    FormAddAuthor formData = form.get();
                    AuthorBo author = new AuthorBo().setName(formData.name).setInfo(formData.info);
                    author = TruyenDao.create(author);
                    flash(VIEW_AUTHORS, Messages.get("msg.add_author.done", author.getName()));
                }
                return redirect(controllers.admin.routes.Author.authors().url());
            }
        });
        return promise;
    }

    /*
     * Handles POST:/editAuthor
     */
    public static Promise<Result> editAuthorSubmit(final int id) {
        Promise<Result> promise = Promise.promise(new Function0<Result>() {
            public Result apply() throws Exception {
                AuthorBo author = TruyenDao.getAuthor(id);
                if (author == null) {
                    flash(VIEW_AUTHORS,
                            Constants.FLASH_MSG_PREFIX_ERROR
                                    + Messages.get("error.author.not_found"));
                } else {
                    Form<FormAddAuthor> form = Form.form(FormAddAuthor.class).bindFromRequest();
                    if (form.hasErrors()) {
                        flash(VIEW_AUTHORS, Constants.FLASH_MSG_PREFIX_ERROR
                                + form.error("name").message());
                    } else {
                        FormAddAuthor formData = form.get();
                        author.setName(formData.name).setInfo(formData.info);
                        author = TruyenDao.update(author);
                        flash(VIEW_AUTHORS, Messages.get("msg.edit_author.done", author.getName()));
                    }
                }
                return redirect(controllers.admin.routes.Author.authors().url());
            }
        });
        return promise;
    }

    /*
     * Handles POST:/deleteAuthor
     */
    public static Promise<Result> deleteAuthorSubmit(final int id) {
        Promise<Result> promise = Promise.promise(new Function0<Result>() {
            public Result apply() throws Exception {
                AuthorBo author = TruyenDao.getAuthor(id);
                if (author == null) {
                    flash(VIEW_AUTHORS,
                            Constants.FLASH_MSG_PREFIX_ERROR
                                    + Messages.get("error.author.not_found"));
                } else {
                    TruyenDao.delete(author);
                    flash(VIEW_AUTHORS, Messages.get("msg.delete_author.done", author.getName()));
                }
                return redirect(controllers.admin.routes.Author.authors().url());
            }
        });
        return promise;
    }
}