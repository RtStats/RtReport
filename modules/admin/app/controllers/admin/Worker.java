package controllers.admin;

import play.api.templates.Html;
import play.data.Form;
import play.i18n.Messages;
import play.libs.F.Function0;
import play.libs.F.Promise;
import play.mvc.Result;
import truyen.common.Constants;
import truyen.common.bo.truyen.BookBo;
import truyen.common.bo.truyen.TruyenDao;
import truyen.common.bo.worker.WorkerBo;
import truyen.common.bo.worker.WorkerDao;
import truyen.common.compisitions.AuthRequired;
import truyen.common.util.FormUtils;
import truyen.worker.WorkerRegistry;
import controllers.common.BaseController;
import forms.admin.FormAddWorker;

@AuthRequired(userGroups = { Constants.USER_GROUP_ADMINISTRATOR, Constants.USER_GROUP_MODERATOR })
public class Worker extends BaseController {

    public final static String VIEW_WORKERS = SECTION + "workers";

    /*
     * Handles GET:/workers
     */
    public static Promise<Result> workers() {
        Promise<Result> promise = Promise.promise(new Function0<Result>() {
            public Result apply() throws Exception {
                WorkerBo[] workers = WorkerDao.getAllWorkers();
                BookBo[] books = TruyenDao.getAllBooks();
                String[] engines = WorkerRegistry.ENGINE_LIST.toArray(new String[0]);
                Html html = render(VIEW_WORKERS, workers, books, engines);
                return ok(html);
            }
        });
        return promise;
    }

    /*
     * Handles POST:/addWorker
     */
    public static Promise<Result> addWorkerSubmit() {
        Promise<Result> promise = Promise.promise(new Function0<Result>() {
            public Result apply() throws Exception {
                Form<FormAddWorker> form = Form.form(FormAddWorker.class).bindFromRequest();
                if (form.hasErrors()) {
                    flash(VIEW_WORKERS,
                            Constants.FLASH_MSG_PREFIX_ERROR
                                    + FormUtils.joinFormErrorMessages(form));
                } else {
                    FormAddWorker formData = form.get();
                    WorkerBo worker = new WorkerBo().setBook(formData.book)
                            .setEngine(formData.engine).setUrl(formData.url)
                            .setStatus(WorkerBo.STATUS_NEW);
                    worker = WorkerDao.create(worker);
                    flash(VIEW_WORKERS,
                            Messages.get("msg.add_worker.done", worker.getBook().getTitle()));
                }
                return redirect(controllers.admin.routes.Worker.workers().url());
            }
        });
        return promise;
    }

    /*
     * Handles POST:/editWorker
     */
    public static Promise<Result> editWorkerSubmit(final int id) {
        Promise<Result> promise = Promise.promise(new Function0<Result>() {
            public Result apply() throws Exception {
                WorkerBo worker = WorkerDao.getWorker(id);
                if (worker == null) {
                    flash(VIEW_WORKERS,
                            Constants.FLASH_MSG_PREFIX_ERROR
                                    + Messages.get("error.worker.not_found"));
                } else {
                    Form<FormAddWorker> form = Form.form(FormAddWorker.class).bindFromRequest();
                    if (form.hasErrors()) {
                        flash(VIEW_WORKERS,
                                Constants.FLASH_MSG_PREFIX_ERROR
                                        + FormUtils.joinFormErrorMessages(form));
                    } else {
                        FormAddWorker formData = form.get();
                        worker.setBook(formData.book).setEngine(formData.engine)
                                .setUrl(formData.url);
                        worker = WorkerDao.update(worker);
                        flash(VIEW_WORKERS,
                                Messages.get("msg.edit_worker.done", worker.getBook().getTitle()));
                    }
                }
                return redirect(controllers.admin.routes.Worker.workers().url());
            }
        });
        return promise;
    }

    /*
     * Handles POST:/deleteWorker
     */
    public static Promise<Result> deleteWorkerSubmit(final int id) {
        Promise<Result> promise = Promise.promise(new Function0<Result>() {
            public Result apply() throws Exception {
                WorkerBo worker = WorkerDao.getWorker(id);
                if (worker == null) {
                    flash(VIEW_WORKERS,
                            Constants.FLASH_MSG_PREFIX_ERROR
                                    + Messages.get("error.worker.not_found"));
                } else {
                    WorkerDao.delete(worker);
                    flash(VIEW_WORKERS, Messages.get("msg.delete_worker.done", worker.getId()));
                }
                return redirect(controllers.admin.routes.Worker.workers().url());
            }
        });
        return promise;
    }
}
