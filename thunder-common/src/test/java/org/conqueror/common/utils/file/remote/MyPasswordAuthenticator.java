package org.conqueror.common.utils.file.remote;

import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.session.ServerSession;


public class MyPasswordAuthenticator implements PasswordAuthenticator {

    public static final String USER = "test-user";
    public static final String PASSWORD = "test-password";

    public boolean authenticate(String username, String password, ServerSession session) {
        return USER.equals(username) && PASSWORD.equals(password);
    }
}
