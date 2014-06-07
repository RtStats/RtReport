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
import truyen.common.bo.truyen.TruyenDao;
import truyen.common.compisitions.AuthRequired;
import controllers.common.BaseController;
import forms.admin.FormAddBook;

@AuthRequired(userGroups = { Constants.USER_GROUP_ADMINISTRATOR, Constants.USER_GROUP_MODERATOR })
public class Book extends BaseController {

    public final static String VIEW_BOOKS = SECTION + "books";

    /*
     * Handles GET:/books
     */
    public static Promise<Result> books() {
        Promise<Result> promise = Promise.promise(new Function0<Result>() {
            public Result apply() throws Exception {
                AuthorBo[] authors = TruyenDao.getAllAuthors();
                CategoryBo[] categories = TruyenDao.getAllCategories();
                BookBo[] books = TruyenDao.getAllBooks();
                Html html = render(VIEW_BOOKS, books, categories, authors);
                return ok(html);
            }
        });
        return promise;
    }

    /*
     * Handles POST:/addBook
     */
    public static Promise<Result> addBookSubmit() {
        Promise<Result> promise = Promise.promise(new Function0<Result>() {
            public Result apply() throws Exception {
                Form<FormAddBook> form = Form.form(FormAddBook.class).bindFromRequest();
                if (form.hasErrors()) {
                    flash(VIEW_BOOKS, Constants.FLASH_MSG_PREFIX_ERROR
                            + form.error("title").message());
                } else {
                    FormAddBook formData = form.get();
                    BookBo book = new BookBo().setTitle(formData.title)
                            .setSummary(formData.summary).setAvatar(formData.avatar)
                            .setAuthor(formData.author).setCategory(formData.category);
                    book = TruyenDao.create(book);
                    flash(VIEW_BOOKS, Messages.get("msg.add_book.done", book.getTitle()));
                }
                return redirect(controllers.admin.routes.Book.books().url());
            }
        });
        return promise;
    }

    /*
     * Handles POST:/editBook
     */
    public static Promise<Result> editBookSubmit(final int id) {
        Promise<Result> promise = Promise.promise(new Function0<Result>() {
            public Result apply() throws Exception {
                BookBo book = TruyenDao.getBook(id);
                if (book == null) {
                    flash(VIEW_BOOKS,
                            Constants.FLASH_MSG_PREFIX_ERROR + Messages.get("error.book.not_found"));
                } else {
                    Form<FormAddBook> form = Form.form(FormAddBook.class).bindFromRequest();
                    if (form.hasErrors()) {
                        flash(VIEW_BOOKS, Constants.FLASH_MSG_PREFIX_ERROR
                                + form.error("title").message());
                    } else {
                        FormAddBook formData = form.get();
                        book.setTitle(formData.title).setSummary(formData.summary)
                                .setAvatar(formData.avatar).setPublished(formData.isPublished)
                                .setStatus(BookBo.parseStatusString(formData.status));
                        book = TruyenDao.update(book);
                        flash(VIEW_BOOKS, Messages.get("msg.edit_book.done", book.getTitle()));
                    }
                }
                return redirect(controllers.admin.routes.Book.books().url());
            }
        });
        return promise;
    }

    /*
     * Handles POST:/deleteBook
     */
    public static Promise<Result> deleteBookSubmit(final int id) {
        Promise<Result> promise = Promise.promise(new Function0<Result>() {
            public Result apply() throws Exception {
                BookBo book = TruyenDao.getBook(id);
                if (book == null) {
                    flash(VIEW_BOOKS,
                            Constants.FLASH_MSG_PREFIX_ERROR + Messages.get("error.book.not_found"));
                } else {
                    if (book.getNumChapters() == 0) {
                        TruyenDao.delete(book);
                    } else {
                        book.setStatus(BookBo.STATUS_DELETED);
                        book = TruyenDao.update(book);
                    }
                    flash(VIEW_BOOKS, Messages.get("msg.delete_book.done", book.getTitle()));
                }
                return redirect(controllers.admin.routes.Book.books().url());
            }
        });
        return promise;
    }
}
