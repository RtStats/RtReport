package forms.admin;

import java.util.ArrayList;
import java.util.List;

import play.data.validation.ValidationError;

public class FormSiteConfig {
    public String site_name, site_title, site_keywords, site_description;
    public String fb_appid, fb_appscope;

    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<ValidationError>();

        return errors.isEmpty() ? null : errors;
    }
}
