package com.lqm.home.model;

/**
 * 应用升级
 */

public class UpdateApkModel {
    private int id;
    private int versioncode;
    private String packagename;
    private String updateinfo;
    private String url;
    private String versionname;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVersioncode() {
        return versioncode;
    }

    public void setVersioncode(int versioncode) {
        this.versioncode = versioncode;
    }

    public String getPackagename() {
        return packagename;
    }

    public void setPackagename(String packagename) {
        this.packagename = packagename;
    }

    public String getUpdateinfo() {
        return updateinfo;
    }

    public void setUpdateinfo(String updateinfo) {
        this.updateinfo = updateinfo;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVersionname() {
        return versionname;
    }

    public void setVersionname(String versionname) {
        this.versionname = versionname;
    }
}
