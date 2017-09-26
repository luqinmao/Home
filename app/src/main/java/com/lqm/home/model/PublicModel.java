package com.lqm.home.model;

import java.io.Serializable;

/**
 * 微信公众号model
 */

public class PublicModel implements Serializable {

    private int id;
    private String publickey;
    private String name;
    private String icon;
    private String publicdesc;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPublickey() {
        return publickey;
    }

    public void setPublickey(String publickey) {
        this.publickey = publickey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getPublicdesc() {
        return publicdesc;
    }

    public void setPublicdesc(String publicdesc) {
        this.publicdesc = publicdesc;
    }
}
