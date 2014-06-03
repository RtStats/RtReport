package forms.admin;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import play.data.validation.ValidationError;
import play.i18n.Messages;

public class FormAddAuthor {
    public String name, info;

    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<ValidationError>();

        String name = !StringUtils.isBlank(this.name) ? this.name.trim() : null;
        String info = !StringUtils.isBlank(this.info) ? this.info.trim() : null;
        if (StringUtils.isBlank(name)) {
            errors.add(new ValidationError("name", Messages.get("error.author.empty_name", name)));
        }
        return errors.isEmpty() ? null : errors;
    }
}
