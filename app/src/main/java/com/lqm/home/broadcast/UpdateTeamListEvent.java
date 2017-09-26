package com.lqm.home.broadcast;

/**
 * Created by luqinmao on 2017/1/5.
 */

public class UpdateTeamListEvent {
    public String message;

    public UpdateTeamListEvent(String message) {
        this.message = message;
    }

    public UpdateTeamListEvent() {
    }

    public String getMessage(){
        return message;
    }
}
