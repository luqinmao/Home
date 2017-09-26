package com.lqm.home.model;

import java.util.List;

/**
 * 乡吧
 */

public class VillagesVO {

    private int code;
    private String msg;
    private boolean success;
    private List<DataBean> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        private int attentionNum;
        private String city;
        private String createTime;
        private String district;
        private int id;
        private int postNum;
        private String province;
        private String title;
        private String villageDesc;
        private String villageIcon;

        public int getAttentionNum() {
            return attentionNum;
        }

        public void setAttentionNum(int attentionNum) {
            this.attentionNum = attentionNum;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getDistrict() {
            return district;
        }

        public void setDistrict(String district) {
            this.district = district;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
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

        public String getVillageIcon() {
            return villageIcon;
        }

        public void setVillageIcon(String villageIcon) {
            this.villageIcon = villageIcon;
        }
    }
}
