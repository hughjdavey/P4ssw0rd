package com.hugh.p4ssw0rd;

import java.io.Serializable;
import java.util.Comparator;

public class Password implements Serializable {
    long timeAdded;
    String identifier, password, username, url;

    public Password(String identifier, String password) {
        this(identifier, password, "username1999", "https://www.example.com/");
    }

    public Password(String identifier, String password, String username, String url) {
        this.identifier = identifier;
        this.password = password;
        this.timeAdded = System.currentTimeMillis();
        this.username = (username == null) ? "" : username;
        this.url = (url == null) ? "" : url;
    }

    public static Comparator<Password> ALPHABETICAL = new Comparator<Password>() {
        @Override
        public int compare(Password p1, Password p2) {
            return p1.identifier.compareTo(p2.identifier);
        }
    };

    public static Comparator<Password> ALPHABETICAL_REVERSE = new Comparator<Password>() {
        @Override
        public int compare(Password p1, Password p2) {
            return p1.identifier.compareTo(p2.identifier) * -1;
        }
    };

    public static Comparator<Password> NEWEST_FIRST = new Comparator<Password>() {
        @Override
        public int compare(Password p1, Password p2) {
            return Long.compare(p1.timeAdded, p2.timeAdded) * -1;
        }
    };

    public static Comparator<Password> OLDEST_FIRST = new Comparator<Password>() {
        @Override
        public int compare(Password p1, Password p2) {
            return Long.compare(p1.timeAdded, p2.timeAdded);
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Password)) {
            return false;
        }

        Password pass = (Password) o;
        return timeAdded == pass.timeAdded &&
                (identifier == null ? pass.identifier == null : identifier.equals(pass.identifier)) &&
                (password == null ? pass.password == null : password.equals(pass.password)) &&
                (username == null ? pass.username == null : username.equals(pass.username)) &&
                (url == null ? pass.url == null : url.equals(pass.url));
    }

    @Override
    public String toString() {
        return getClass().getName() + "[" +
                "id=" + identifier + ", " +
                "pw=" + password + ", " +
                "usr=" + username + ", " +
                "url=" + url + "]";
    }

    @Override
    public int hashCode() {
        int result = 17;

        result = 31 * result +
                (identifier == null ? 0 : identifier.hashCode());
        result = 31 * result +
                (password == null ? 0 : password.hashCode());
        result = 31 * result +
                (username == null ? 0 : username.hashCode());
        result = 31 * result +
                (url == null ? 0 : url.hashCode());
        result = 31 * result +
                (int) (timeAdded ^ (timeAdded >>> 32));

        return result;
    }
}
