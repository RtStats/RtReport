package controllers.common;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import play.api.templates.Html;
import play.i18n.Lang;
import play.libs.F.Function0;
import play.libs.F.Promise;
import play.mvc.Controller;
import play.mvc.Result;

public class ErrorsController extends Controller {

    private static Html render(String view, Object... params) throws InstantiationException,
            IllegalAccessException, ClassNotFoundException, SecurityException,
            NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
        String clazzName = "views.html." + view;
        Class<?> clazz = Class.forName(clazzName);

        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (method.getName().equals("render")) {
                // Lang lang = Lang.forCode("vi");
                Lang lang = lang();
                Object[] combinedParams = new Object[params.length + 1];
                combinedParams[params.length] = lang;
                for (int i = 0; i < params.length; i++) {
                    combinedParams[i] = params[i];
                }
                return (Html) method.invoke(null, combinedParams);
            }
        }
        return null;
    }

    public static Promise<Result> error404() {
        Promise<Result> promise = Promise.promise(new Function0<Result>() {
            public Result apply() throws Exception {
                return ok("Page not found!");
                // Html html = render(VIEW_DASHBOARD, xTimestamp, xVND, xXu,
                // xTransaction);
                // return ok(html);
            }
        });
        return promise;
    }

    public static Promise<Result> error403() {
        Promise<Result> promise = Promise.promise(new Function0<Result>() {
            public Result apply() throws Exception {
                return ok("Access denied!");
                // Html html = render(VIEW_DASHBOARD, xTimestamp, xVND, xXu,
                // xTransaction);
                // return ok(html);
            }
        });
        return promise;
    }
}
