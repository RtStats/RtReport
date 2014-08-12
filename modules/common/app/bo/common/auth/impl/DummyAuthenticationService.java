package bo.common.auth.impl;

import bo.common.auth.IAuthenticationService;
import bo.common.user.UserBo;

/**
 * This dummy {@link IAuthenticationService} will NOT perform any authenticating
 * work, its {@link #authenticate(UserBo, String)} method simple returns {@link
 * true}.
 */
public class DummyAuthenticationService implements IAuthenticationService {

    @Override
    public boolean authenticate(UserBo user, String password) {
        return true;
    }

}
