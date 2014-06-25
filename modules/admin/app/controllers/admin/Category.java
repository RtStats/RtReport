package controllers.admin;

import play.api.templates.Html;
import play.data.Form;
import play.i18n.Messages;
import play.libs.F.Function0;
import play.libs.F.Promise;
import play.mvc.Result;
import truyen.common.Constants;
import truyen.common.bo.truyen.CategoryBo;
import truyen.common.bo.truyen.TruyenDao;

import compisitions.admin.AuthRequired;

import controllers.common.BaseController;
import forms.admin.FormAddCategory;

@AuthRequired(userGroups = { Constants.USER_GROUP_ADMINISTRATOR, Constants.USER_GROUP_MODERATOR })
public class Category extends BaseController {

    public final static String VIEW_CATEGORIES = SECTION + "categories";

    /*
     * Handles GET:/categories
     */
    public static Promise<Result> categories() {
        Promise<Result> promise = Promise.promise(new Function0<Result>() {
            public Result apply() throws Exception {
                CategoryBo[] cats = TruyenDao.getAllCategories();
                Html html = render(VIEW_CATEGORIES, (Object) cats);
                return ok(html);
            }
        });
        return promise;
    }

    /*
     * Handles GET:/moveUpCategory
     */
    public static Promise<Result> moveUpCategory(final int id) {
        Promise<Result> promise = Promise.promise(new Function0<Result>() {
            public Result apply() throws Exception {
                CategoryBo category = TruyenDao.getCategory(id);
                if (category == null) {
                    flash(VIEW_CATEGORIES,
                            Constants.FLASH_MSG_PREFIX_ERROR
                                    + Messages.get("error.category.not_found"));
                } else {
                    TruyenDao.moveUp(category);
                    flash(VIEW_CATEGORIES,
                            Messages.get("msg.edit_category.done", category.getTitle()));
                }
                return redirect(controllers.admin.routes.Category.categories().url());
            }
        });
        return promise;
    }

    /*
     * Handles GET:/moveDownCategory
     */
    public static Promise<Result> moveDownCategory(final int id) {
        Promise<Result> promise = Promise.promise(new Function0<Result>() {
            public Result apply() throws Exception {
                CategoryBo category = TruyenDao.getCategory(id);
                if (category == null) {
                    flash(VIEW_CATEGORIES,
                            Constants.FLASH_MSG_PREFIX_ERROR
                                    + Messages.get("error.category.not_found"));
                } else {
                    TruyenDao.moveDown(category);
                    flash(VIEW_CATEGORIES,
                            Messages.get("msg.edit_category.done", category.getTitle()));
                }
                return redirect(controllers.admin.routes.Category.categories().url());
            }
        });
        return promise;
    }

    /*
     * Handles POST:/addCategory
     */
    public static Promise<Result> addCategorySubmit() {
        Promise<Result> promise = Promise.promise(new Function0<Result>() {
            public Result apply() throws Exception {
                Form<FormAddCategory> form = Form.form(FormAddCategory.class).bindFromRequest();
                if (form.hasErrors()) {
                    flash(VIEW_CATEGORIES, Constants.FLASH_MSG_PREFIX_ERROR
                            + form.error("title").message());
                } else {
                    FormAddCategory formData = form.get();
                    CategoryBo category = new CategoryBo().setTitle(formData.title)
                            .setSummary(formData.summary)
                            .setPosition((int) (System.currentTimeMillis() / 1000));
                    category = TruyenDao.create(category);
                    flash(VIEW_CATEGORIES,
                            Messages.get("msg.add_category.done", category.getTitle()));
                }
                return redirect(controllers.admin.routes.Category.categories().url());
            }
        });
        return promise;
    }

    /*
     * Handles POST:/editCategory
     */
    public static Promise<Result> editCategorySubmit(final int id) {
        Promise<Result> promise = Promise.promise(new Function0<Result>() {
            public Result apply() throws Exception {
                CategoryBo category = TruyenDao.getCategory(id);
                if (category == null) {
                    flash(VIEW_CATEGORIES,
                            Constants.FLASH_MSG_PREFIX_ERROR
                                    + Messages.get("error.category.not_found"));
                } else {
                    Form<FormAddCategory> form = Form.form(FormAddCategory.class).bindFromRequest();
                    if (form.hasErrors()) {
                        flash(VIEW_CATEGORIES,
                                Constants.FLASH_MSG_PREFIX_ERROR + form.error("title").message());
                    } else {
                        FormAddCategory formData = form.get();
                        category.setTitle(formData.title).setSummary(formData.summary);
                        category = TruyenDao.update(category);
                        flash(VIEW_CATEGORIES,
                                Messages.get("msg.edit_category.done", category.getTitle()));
                    }
                }
                return redirect(controllers.admin.routes.Category.categories().url());
            }
        });
        return promise;
    }

    /*
     * Handles POST:/deleteCategory
     */
    public static Promise<Result> deleteCategorySubmit(final int id) {
        Promise<Result> promise = Promise.promise(new Function0<Result>() {
            public Result apply() throws Exception {
                CategoryBo category = TruyenDao.getCategory(id);
                if (category == null) {
                    flash(VIEW_CATEGORIES,
                            Constants.FLASH_MSG_PREFIX_ERROR
                                    + Messages.get("error.category.not_found"));
                } else {
                    TruyenDao.delete(category);
                    flash(VIEW_CATEGORIES,
                            Messages.get("msg.delete_category.done", category.getTitle()));
                }
                return redirect(controllers.admin.routes.Category.categories().url());
            }
        });
        return promise;
    }
}
