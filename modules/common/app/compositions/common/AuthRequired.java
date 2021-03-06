package compositions.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import play.mvc.With;

@With(AuthRequiredAction.class)
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthRequired {
    int[] userGroups() default {};

    String urlLogin() default "";

    String url403() default "";
}
