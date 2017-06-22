package org.java.notification.user;

import org.java.notification.Entity;

/**
 * Created by msamoylych on 25.04.2017.
 */
public class WebUser extends Entity {

    private String login;

    private String password;

    private String salt;

    public String login() {
        return login;
    }

    public void login(String login) {
        this.login = login;
    }

    public String password() {
        return password;
    }

    public void password(String password) {
        this.password = password;
    }

    public String salt() {
        return salt;
    }

    public void salt(String salt) {
        this.salt = salt;
    }
}
