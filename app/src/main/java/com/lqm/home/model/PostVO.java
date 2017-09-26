package com.lqm.home.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by luqinmao on 2017/1/11.
 */
public class PostVO implements Serializable{


    private List<DataBean> data;
    private int code;
    private boolean success;
    private String msg;


    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
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
        private String username;
        private Object userphoto;
        private String title;
        private String content;
        private String contentImg;
        private String currentPosition;
        private String createTime;
        private String commentNum;
        private int villageId;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public Object getUserphoto() {
            return userphoto;
        }

        public void setUserphoto(Object userphoto) {
            this.userphoto = userphoto;
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

        public String getContentImg() {
            return contentImg;
        }

        public void setContentImg(String contentImg) {
            this.contentImg = contentImg;
        }

        public String getCurrentPosition() {
            return currentPosition;
        }

        public void setCurrentPosition(String currentPosition) {
            this.currentPosition = currentPosition;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getCommentNum() {
            return commentNum;
        }

        public void setCommentNum(String commentNum) {
            this.commentNum = commentNum;
        }

        public int getVillageId() {
            return villageId;
        }

        public void setVillageId(int villageId) {
            this.villageId = villageId;
        }
    }
}
