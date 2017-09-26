package com.lqm.home.model;

/**
 * 发现模块 item
 */

public class FindItemModel {

    private int icon;
    private int drawable;
    private String title;
    private String content;

    public int getDrawable() {
        return drawable;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public void setDrawable(int drawable) {
        this.drawable = drawable;
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
}
