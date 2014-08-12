package controllers.common;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import play.api.templates.Html;
import play.i18n.Lang;
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
}
