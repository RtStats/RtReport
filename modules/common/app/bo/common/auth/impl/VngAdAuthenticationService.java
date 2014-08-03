package bo.common.auth.impl;

import java.util.Hashtable;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import bo.common.auth.IAuthenticationService;
import bo.common.auth.MySSLSocketFactory;
import bo.common.user.UserBo;

public class VngAdAuthenticationService implements IAuthenticationService {

    private static String SECURITY_AUTHENTICATION = "simple";
    private static String SECURITY_PROTOCOL = "ssl";
    private static String ldapUrl = "ldaps://ldap.vng.com.vn:636";
    private static String domain = "vng.com.vn";

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean authenticate(UserBo user, String password) {
        Hashtable<String, Object> authEnv = new Hashtable<String, Object>();
        authEnv.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        authEnv.put(Context.SECURITY_AUTHENTICATION, SECURITY_AUTHENTICATION);
        authEnv.put(Context.SECURITY_PROTOCOL, SECURITY_PROTOCOL);
        authEnv.put(Context.PROVIDER_URL, ldapUrl);
        authEnv.put("java.naming.ldap.factory.socket", MySSLSocketFactory.class.getName());
        String username = user.getUsername();
        authEnv.put(Context.SECURITY_PRINCIPAL, username + "@" + domain);
        authEnv.put(Context.SECURITY_CREDENTIALS, password);

        try {
            DirContext authContext = new InitialDirContext(authEnv);
            return authContext != null;
        } catch (AuthenticationException authEx) {
        } catch (NamingException namEx) {
        }
        return false;
    }

}
