package forms.common;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import play.data.validation.ValidationError;
import play.i18n.Messages;
import utils.common.AuthUtils;
import bo.common.user.UserBo;
import bo.common.user.UserDao;

public class FormLogin {
    public String username, password;

    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<ValidationError>();

        String username = !StringUtils.isBlank(this.username) ? this.username.trim().toLowerCase()
                : null;
        String password = !StringUtils.isBlank(this.password) ? this.password.trim() : null;
        UserBo user = UserDao.getUser(username);
        if (!AuthUtils.authenticate(user, password)) {
            errors.add(new ValidationError("username", Messages
                    .get("error.signin.failed", username)));
        }

        return errors.isEmpty() ? null : errors;
    }

}
