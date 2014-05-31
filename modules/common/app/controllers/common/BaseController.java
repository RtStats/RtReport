package controllers.common;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import play.api.templates.Html;
import play.libs.F.Function0;
import play.libs.F.Promise;
import play.mvc.Controller;
import play.mvc.Result;

public class BaseController extends Controller {

    /**
     * Redirects to a url.
     * 
     * @param url
     * @return
     */
    public static Promise<Result> redirectTo(final String url) {
        Promise<Result> promise = Promise.promise(new Function0<Result>() {
            public Result apply() throws Exception {
                return redirect(url);
            }
        });
        return promise;
    }

    protected static Html render(String view, Object... params) throws InstantiationException,
            IllegalAccessException, ClassNotFoundException, SecurityException,
            NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
        String clazzName = "views.html." + view;
        Class<?> clazz = Class.forName(clazzName);

        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (method.getName().equals("render")) {
                return (Html) method.invoke(null, params);
            }
        }
        return null;
    }
}
