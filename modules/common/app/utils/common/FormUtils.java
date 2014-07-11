package utils.common;

import java.util.List;

import play.data.Form;
import play.data.validation.ValidationError;

public class FormUtils {
    public static String joinFormErrorMessages(Form<?> form) {
        StringBuilder sb = new StringBuilder();
        for (List<ValidationError> errors : form.errors().values()) {
            for (ValidationError error : errors) {
                sb.append(error.message()).append("\n");
            }
        }
        return sb.toString();
    }
}
