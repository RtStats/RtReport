package controllers;

import play.libs.F.Function0;
import play.libs.F.Promise;
import play.mvc.Result;
import controllers.common.BaseController;

public class QndController extends BaseController {
	public static Promise<Result> qnd() {
		Promise<Result> promise = Promise.promise(new Function0<Result>() {
			public Result apply() throws Exception {
				String url = controllers.routes.QndController.qnd()
						.absoluteURL(request());
				url = request().getHeader("Host");
				return ok(url);
			}
		});
		return promise;
	}
}
