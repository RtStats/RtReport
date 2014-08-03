package bo.common.auth;

import bo.common.user.UserBo;

public interface IAuthenticationService {
    public boolean authenticate(UserBo user, String password);
}
