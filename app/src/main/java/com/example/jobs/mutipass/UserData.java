package com.example.jobs.mutipass;

/**
 * Created by jobs on 2016/10/2.
 */

public class UserData {
    private String acount;
    private String passwd;

    public UserData(String acount, String passwd) {
        this.acount = acount;
        this.passwd = passwd;
    }


    public String getAcount() {
        return acount;
    }

    public String getPasswd() {
        return passwd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserData)) return false;

        UserData userData = (UserData) o;

        if (!getAcount().equals(userData.getAcount())) return false;
        return getPasswd().equals(userData.getPasswd());

    }

    @Override
    public int hashCode() {
        int result = getAcount().hashCode();
        result = 31 * result + getPasswd().hashCode();
        return result;
    }
}
