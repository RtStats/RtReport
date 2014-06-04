package forms.admin;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import play.data.validation.ValidationError;
import play.i18n.Messages;

public class FormAddCategory {
    public String title, summary;

    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<ValidationError>();

        title = !StringUtils.isBlank(this.title) ? this.title.trim() : null;
        summary = !StringUtils.isBlank(this.summary) ? this.summary.trim() : null;
        if (StringUtils.isBlank(title)) {
            errors.add(new ValidationError("title", Messages.get("error.title.empty_title")));
        }
        return errors.isEmpty() ? null : errors;
    }
}
