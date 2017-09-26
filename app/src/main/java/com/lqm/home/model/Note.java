package com.lqm.home.model;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.Date;

/**
 * 日记
 */

public class Note extends DataSupport implements Serializable{


    private long id;
    private static final long serialVersionUID=1L;
    private String title;
    private String content;
    private Date time;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
