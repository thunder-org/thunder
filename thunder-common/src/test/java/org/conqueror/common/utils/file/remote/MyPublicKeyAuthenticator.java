package org.conqueror.common.utils.file.remote;

import org.apache.sshd.server.auth.pubkey.PublickeyAuthenticator;
import org.apache.sshd.server.session.ServerSession;

import java.security.PublicKey;

public class MyPublicKeyAuthenticator implements PublickeyAuthenticator {
    public boolean authenticate(String s, PublicKey publicKey, ServerSession serverSession) {
        return false;
    }
}
