package com.lqm.home.broadcast;

/**
 * Created by luqinmao on 2017/1/5.
 */

public class UpdateVillageEvent {
    public String message;

    public UpdateVillageEvent(String message) {
        this.message = message;
    }

    public UpdateVillageEvent() {
    }

    public String getMessage(){
        return message;
    }
}
