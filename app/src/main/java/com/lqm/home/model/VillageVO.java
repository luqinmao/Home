package com.lqm.home.model;

import java.io.Serializable;

/**
 * 每个乡吧的model
 */

public class VillageVO implements Serializable{

    private DataBean data;
    private int code;
    private boolean success;
    private String msg;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static class DataBean {
        private int id;
        private String villageIcon;
        private String title;
        private String villageDesc;
        private int attentionNum;
        private int postNum;
        private String province;
        private String city;
        private String district;
        private String createTime;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getVillageIcon() {
            return villageIcon;
        }

        public void setVillageIcon(String villageIcon) {
            this.villageIcon = villageIcon;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getVillageDesc() {
            return villageDesc;
        }

        public void setVillageDesc(String villageDesc) {
            this.villageDesc = villageDesc;
        }

        public int getAttentionNum() {
            return attentionNum;
        }

        public void setAttentionNum(int attentionNum) {
            this.attentionNum = attentionNum;
        }

        public int getPostNum() {
            return postNum;
        }

        public void setPostNum(int postNum) {
            this.postNum = postNum;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getDistrict() {
            return district;
        }

        public void setDistrict(String district) {
            this.district = district;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }
    }
}
