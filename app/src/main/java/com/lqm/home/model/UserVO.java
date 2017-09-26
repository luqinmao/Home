package com.lqm.home.model;

import java.util.List;

/**
 * Created by luqinmao on 2017/1/10.
 */

public class UserVO {

    private UserServer myselfInfo;

    private List<UserServer> friends;

    public UserServer getMyselfInfo() {
        return myselfInfo;
    }

    public void setMyselfInfo(UserServer myselfInfo) {
        this.myselfInfo = myselfInfo;
    }

    public List<UserServer> getFriends() {
        return friends;
    }

    public void setFriends(List<UserServer> friends) {
        this.friends = friends;
    }
}