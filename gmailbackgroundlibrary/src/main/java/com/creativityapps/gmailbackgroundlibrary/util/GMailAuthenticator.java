package com.creativityapps.gmailbackgroundlibrary.util;

/**
 * Created by LEV on 27/08/2019.
 */

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;


class GMailAuthenticator extends Authenticator {
    String user;
    String pw;
    public GMailAuthenticator (String username, String password)
    {
        super();
        this.user = username;
        this.pw = password;
    }
    public PasswordAuthentication getPasswordAuthentication()
    {
        return new PasswordAuthentication(user, pw);
    }
}